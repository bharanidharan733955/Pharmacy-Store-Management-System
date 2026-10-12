package com.pharmacy.order.service;

import com.pharmacy.order.exception.InsufficientStockException;
import com.pharmacy.order.exception.InvalidQuantityException;
import com.pharmacy.order.exception.InventorySyncException;
import com.pharmacy.order.exception.OrderProcessingException;
import com.pharmacy.order.model.Medicine;
import com.pharmacy.order.model.Order;
import com.pharmacy.order.model.OrderItem;
import com.pharmacy.order.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Day 6 - Order Processor Exception Handling Tests")
public class OrderProcessorTest {

    private InventoryService inventoryService;
    private AuditService auditService;
    private OrderProcessor processor;

    @BeforeEach
    public void setUp() {
        inventoryService = new InventoryService();
        inventoryService.addMedicine(new Medicine("MED-101", "Paracetamol 500mg", 5.0, 50));
        inventoryService.addMedicine(new Medicine("MED-102", "Amoxicillin 250mg", 15.0, 20));
        inventoryService.addMedicine(new Medicine("MED-103", "Cetirizine 10mg", 8.0, 10));

        auditService = new AuditService();
        processor = new OrderProcessor(inventoryService, auditService);
    }

    @Test
    @DisplayName("1. Happy Path: Process valid order and guarantee finally audit execution")
    void testSuccessfulOrderProcessing() throws Exception {
        Order order = new Order("ORD-TEST-01", "John Doe");
        order.addItem(new OrderItem("MED-101", "Paracetamol 500mg", 5, 5.0));

        Order result = processor.processOrder(order);

        assertEquals(OrderStatus.CONFIRMED, result.getStatus());
        assertEquals(45, inventoryService.getMedicine("MED-101").get().getStockQuantity());

        // Verify finally audit block executed
        assertEquals(1, auditService.getAuditRecords().size());
        var record = auditService.getAuditRecords().get(0);
        assertEquals("ORD-TEST-01", record.getOrderId());
        assertEquals(OrderStatus.CONFIRMED, record.getFinalStatus());
        assertNull(record.getExceptionType());
    }

    @Test
    @DisplayName("2. Unchecked Exception: InvalidQuantityException thrown for negative quantity")
    void testNegativeQuantityThrowsUncheckedException() {
        Order order = new Order("ORD-TEST-02", "Jane Smith");
        order.addItem(new OrderItem("MED-101", "Paracetamol 500mg", -3, 5.0));

        InvalidQuantityException thrown = assertThrows(
                InvalidQuantityException.class,
                () -> processor.processOrder(order)
        );

        assertTrue(thrown instanceof RuntimeException, "Must be an unchecked RuntimeException");
        assertEquals(-3, thrown.getInvalidQuantity());
        assertEquals(OrderStatus.REJECTED, order.getStatus());

        // Stock should remain unchanged
        assertEquals(50, inventoryService.getMedicine("MED-101").get().getStockQuantity());

        // Verify finally block recorded audit
        assertEquals(1, auditService.getAuditRecords().size());
        var record = auditService.getAuditRecords().get(0);
        assertEquals("ORD-TEST-02", record.getOrderId());
        assertEquals(OrderStatus.REJECTED, record.getFinalStatus());
        assertEquals("InvalidQuantityException", record.getExceptionType());
    }

    @Test
    @DisplayName("3. Checked Exception: InsufficientStockException thrown when requesting excess stock")
    void testInsufficientStockThrowsCheckedException() {
        Order order = new Order("ORD-TEST-03", "Hospital Clinic");
        // MED-102 has only 20 units; requesting 35
        order.addItem(new OrderItem("MED-102", "Amoxicillin 250mg", 35, 15.0));

        InsufficientStockException thrown = assertThrows(
                InsufficientStockException.class,
                () -> processor.processOrder(order)
        );

        assertFalse(RuntimeException.class.isAssignableFrom(thrown.getClass()), "Must be a checked Exception, not RuntimeException");
        assertInstanceOf(Exception.class, thrown);
        assertEquals(35, thrown.getRequestedQuantity());
        assertEquals(20, thrown.getAvailableQuantity());
        assertEquals("MED-102", thrown.getMedicineId());
        assertEquals(OrderStatus.FAILED, order.getStatus());

        // Verify finally block recorded audit
        assertEquals(1, auditService.getAuditRecords().size());
        var record = auditService.getAuditRecords().get(0);
        assertEquals("ORD-TEST-03", record.getOrderId());
        assertEquals(OrderStatus.FAILED, record.getFinalStatus());
        assertEquals("InsufficientStockException", record.getExceptionType());
    }

    @Test
    @DisplayName("4. Chained Causes: Verify lower-level cause is chained into InsufficientStockException")
    void testChainedCausesVerification() {
        Order order = new Order("ORD-TEST-04", "Warehouse Sync Client");
        order.addItem(new OrderItem("ERR_SYNC_999", "Insulin", 2, 100.0));

        InsufficientStockException thrown = assertThrows(
                InsufficientStockException.class,
                () -> processor.processOrder(order)
        );

        // Verify cause chaining
        Throwable cause = thrown.getCause();
        assertNotNull(cause, "Root cause must be chained");
        assertInstanceOf(InventorySyncException.class, cause);
        assertTrue(cause.getMessage().contains("Database connection timed out"));

        // Verify AuditService accurately extracts root cause from chain
        assertEquals(1, auditService.getAuditRecords().size());
        var record = auditService.getAuditRecords().get(0);
        assertEquals("ORD-TEST-04", record.getOrderId());
        assertEquals("InsufficientStockException", record.getExceptionType());
        assertTrue(record.getRootCauseMessage().contains("InventorySyncException"));
    }

    @Test
    @DisplayName("5. Multi-Catch: Verify catching both checked and unchecked exceptions in unified handler")
    void testMultiCatchClientSimulation() {
        Order order1 = new Order("ORD-MULTI-1", "Client A");
        order1.addItem(new OrderItem("MED-101", "Paracetamol", -5, 5.0));

        Order order2 = new Order("ORD-MULTI-2", "Client B");
        order2.addItem(new OrderItem("MED-101", "Paracetamol", 200, 5.0));

        boolean caughtOrder1InMultiCatch = false;
        try {
            processor.processOrder(order1);
        } catch (InsufficientStockException | InvalidQuantityException ex) {
            caughtOrder1InMultiCatch = true;
            assertInstanceOf(InvalidQuantityException.class, ex);
        } catch (OrderProcessingException e) {
            fail("Should have been caught by multi-catch block");
        }
        assertTrue(caughtOrder1InMultiCatch);

        boolean caughtOrder2InMultiCatch = false;
        try {
            processor.processOrder(order2);
        } catch (InsufficientStockException | InvalidQuantityException ex) {
            caughtOrder2InMultiCatch = true;
            assertInstanceOf(InsufficientStockException.class, ex);
        } catch (OrderProcessingException e) {
            fail("Should have been caught by multi-catch block");
        }
        assertTrue(caughtOrder2InMultiCatch);

        // Verify finally audit recorded both
        assertEquals(2, auditService.getAuditRecords().size());
    }

    @Test
    @DisplayName("6. Guaranteed Finally Audit: Audit must be recorded on all execution branches")
    void testAuditExecutionGuaranteedOnMultipleFailures() {
        assertEquals(0, auditService.getAuditRecords().size());

        try {
            Order o1 = new Order("ORD-A", "Client");
            o1.addItem(new OrderItem("MED-101", "Paracetamol", 0, 5.0));
            processor.processOrder(o1);
        } catch (Exception ignored) {}

        try {
            Order o2 = new Order("ORD-B", "Client");
            o2.addItem(new OrderItem("MED-101", "Paracetamol", 9999, 5.0)); // > 1000 prescription limit
            processor.processOrder(o2);
        } catch (Exception ignored) {}

        try {
            Order o3 = new Order("ORD-C", "Client");
            o3.addItem(new OrderItem("MED-101", "Paracetamol", 1, 5.0));
            processor.processOrder(o3);
        } catch (Exception ignored) {}

        assertEquals(3, auditService.getAuditRecords().size(), "Finally block must record audit exactly 3 times");
    }
}
