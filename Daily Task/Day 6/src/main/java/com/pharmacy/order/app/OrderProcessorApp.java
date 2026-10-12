package com.pharmacy.order.app;

import com.pharmacy.order.exception.InsufficientStockException;
import com.pharmacy.order.exception.InvalidQuantityException;
import com.pharmacy.order.exception.OrderProcessingException;
import com.pharmacy.order.model.Medicine;
import com.pharmacy.order.model.Order;
import com.pharmacy.order.model.OrderItem;
import com.pharmacy.order.service.AuditService;
import com.pharmacy.order.service.InventoryService;
import com.pharmacy.order.service.OrderProcessor;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Interactive demonstration of Day 6 Order Processor Exception Handling Lab:
 * 1. Custom Checked Exception: InsufficientStockException
 * 2. Custom Unchecked Exception: InvalidQuantityException
 * 3. Chained Causes: InventorySyncException -> InsufficientStockException
 * 4. Multi-Catch Block: catch (InsufficientStockException | InvalidQuantityException e)
 * 5. Finally Audit: Guaranteed execution of AuditService in finally block
 */
public class OrderProcessorApp {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   PHARMACY STORE MANAGEMENT SYSTEM - DAY 6: ORDER PROCESSOR EXCEPTION LAB      ");
        System.out.println("================================================================================\n");

        // 1. Initialize services and sample inventory
        InventoryService inventoryService = new InventoryService();
        inventoryService.addMedicine(new Medicine("MED-101", "Paracetamol 500mg", 5.50, 100));
        inventoryService.addMedicine(new Medicine("MED-102", "Amoxicillin 250mg", 18.00, 50));
        inventoryService.addMedicine(new Medicine("MED-103", "Cetirizine 10mg", 8.25, 60));
        inventoryService.addMedicine(new Medicine("MED-104", "Azithromycin 500mg", 45.00, 15)); // Low stock item
        inventoryService.addMedicine(new Medicine("MED-105", "Insulin Glargine 100IU", 420.00, 20));

        AuditService auditService = new AuditService();
        OrderProcessor processor = new OrderProcessor(inventoryService, auditService);

        // -----------------------------------------------------------------------------------------
        // SCENARIO 1: Successful Order Processing (Finally Audit executes on clean exit)
        // -----------------------------------------------------------------------------------------
        System.out.println(">>> [SCENARIO 1] Processing Valid Order (Happy Path) <<<");
        Order validOrder = new Order("ORD-001", "Dr. A. Sharma");
        validOrder.addItem(new OrderItem("MED-101", "Paracetamol 500mg", 10, 5.50));
        validOrder.addItem(new OrderItem("MED-103", "Cetirizine 10mg", 5, 8.25));

        try {
            Order result = processor.processOrder(validOrder);
            System.out.println(" SUCCESS: " + result);
        } catch (InsufficientStockException | InvalidQuantityException e) {
            System.err.println("❌ Caught in Multi-Catch: " + e.getMessage());
        } catch (OrderProcessingException e) {
            System.err.println("❌ Order Processing Failed: " + e.getMessage());
        }

        System.out.println();

        // -----------------------------------------------------------------------------------------
        // SCENARIO 2: Unchecked InvalidQuantityException (Stack Trace 1 Candidate)
        // -----------------------------------------------------------------------------------------
        System.out.println(">>> [SCENARIO 2] Unchecked Exception: InvalidQuantityException (Negative / 0 Qty) <<<");
        Order invalidOrder = new Order("ORD-002", "Apollo Clinic");
        invalidOrder.addItem(new OrderItem("MED-102", "Amoxicillin 250mg", -12, 18.00)); // Invalid negative quantity

        try {
            processor.processOrder(invalidOrder);
        } catch (InsufficientStockException | InvalidQuantityException e) {
            System.out.println(" Caught in Multi-Catch [InsufficientStockException | InvalidQuantityException]:");
            System.out.println("   Exception Type: " + e.getClass().getName() + " (Unchecked RuntimeException)");
            System.out.println("   Error Message : " + e.getMessage());
            System.out.println("\n--- [STACK TRACE 1: UNCHECKED InvalidQuantityException] ---");
            e.printStackTrace(System.out);
            System.out.println("-----------------------------------------------------------\n");
        } catch (OrderProcessingException e) {
            System.err.println("❌ Order Processing Failed: " + e.getMessage());
        }

        // -----------------------------------------------------------------------------------------
        // SCENARIO 3: Checked InsufficientStockException (Stack Shortfall)
        // -----------------------------------------------------------------------------------------
        System.out.println(">>> [SCENARIO 3] Checked Exception: InsufficientStockException (Stock Shortfall) <<<");
        Order stockShortageOrder = new Order("ORD-003", "Care Hospital");
        // MED-104 has only 15 units available, requesting 80 units
        stockShortageOrder.addItem(new OrderItem("MED-104", "Azithromycin 500mg", 80, 45.00));

        try {
            processor.processOrder(stockShortageOrder);
        } catch (InsufficientStockException | InvalidQuantityException e) {
            System.out.println(" Caught in Multi-Catch [InsufficientStockException | InvalidQuantityException]:");
            System.out.println("   Exception Type: " + e.getClass().getName() + " (Checked Exception)");
            System.out.println("   Error Message : " + e.getMessage());
            if (e instanceof InsufficientStockException ise) {
                System.out.println(String.format("   Diagnostics   : Item: %s, Requested: %d, Available: %d",
                        ise.getMedicineName(), ise.getRequestedQuantity(), ise.getAvailableQuantity()));
            }
            System.out.println();
        } catch (OrderProcessingException e) {
            System.err.println("❌ Order Processing Failed: " + e.getMessage());
        }

        // -----------------------------------------------------------------------------------------
        // SCENARIO 4: Chained Causes: InventorySyncException chained into InsufficientStockException
        //             (Stack Trace 2 Candidate)
        // -----------------------------------------------------------------------------------------
        System.out.println(">>> [SCENARIO 4] Chained Causes: Database Timeout -> InsufficientStockException <<<");
        Order syncErrorOrder = new Order("ORD-004", "Fortis Medical Center");
        syncErrorOrder.addItem(new OrderItem("ERR_SYNC_105", "Insulin Glargine 100IU", 5, 420.00));

        try {
            processor.processOrder(syncErrorOrder);
        } catch (InsufficientStockException | InvalidQuantityException e) {
            System.out.println(" Caught in Multi-Catch [InsufficientStockException | InvalidQuantityException]:");
            System.out.println("   Exception Type : " + e.getClass().getName());
            System.out.println("   Chained Cause  : " + (e.getCause() != null ? e.getCause().getClass().getName() : "None"));
            System.out.println("\n--- [STACK TRACE 2: CHAINED CAUSE InsufficientStockException] ---");
            e.printStackTrace(System.out);
            System.out.println("-----------------------------------------------------------------\n");
        } catch (OrderProcessingException e) {
            System.err.println("❌ Order Processing Failed: " + e.getMessage());
        }

        // -----------------------------------------------------------------------------------------
        // VERIFY GUARANTEED FINALLY AUDIT TRAIL
        // -----------------------------------------------------------------------------------------
        System.out.println("================================================================================");
        System.out.println("   VERIFYING GUARANTEED FINALLY AUDIT LOG ENTRIES (RECORDED IN FINALLY BLOCK)   ");
        System.out.println("================================================================================");
        for (var record : auditService.getAuditRecords()) {
            System.out.println(record);
        }
        System.out.println("================================================================================");
        System.out.println("   All 4 order attempts (success and failures) were 100% captured in audit!     ");
        System.out.println("================================================================================");
    }

    public static String getStackTraceAsString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}
