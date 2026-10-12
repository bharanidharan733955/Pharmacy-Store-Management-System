package com.pharmacy.order.service;

import com.pharmacy.order.exception.InsufficientStockException;
import com.pharmacy.order.exception.InvalidQuantityException;
import com.pharmacy.order.exception.OrderProcessingException;
import com.pharmacy.order.model.Order;
import com.pharmacy.order.model.OrderItem;
import com.pharmacy.order.model.OrderStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * OrderProcessor manages order validation, stock reservation, and final order confirmation.
 * <p>
 * Key Features Demonstrated:
 * <ul>
 *   <li>Custom Checked Exception: {@link InsufficientStockException}</li>
 *   <li>Custom Unchecked Exception: {@link InvalidQuantityException}</li>
 *   <li>Chained Causes via Throwable constructors</li>
 *   <li>Java 7+ Multi-Catch block syntax</li>
 *   <li>Guaranteed Audit Trail recording inside the {@code finally} block</li>
 * </ul>
 */
public class OrderProcessor {
    private final InventoryService inventoryService;
    private final AuditService auditService;

    public OrderProcessor(InventoryService inventoryService, AuditService auditService) {
        this.inventoryService = inventoryService;
        this.auditService = auditService;
    }

    /**
     * Validates an order's items for correct input constraints.
     * Throws unchecked {@link InvalidQuantityException} for invalid inputs.
     *
     * @param order the order to validate
     * @throws InvalidQuantityException if item quantity is non-positive or exceeds maximum
     */
    public void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (order.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        for (OrderItem item : order.getItems()) {
            // Unchecked exception thrown for logical/input violations
            if (item.getQuantity() <= 0) {
                throw new InvalidQuantityException(
                        item.getMedicineId(),
                        item.getQuantity(),
                        "Quantity must be strictly positive (greater than 0)"
                );
            }
            if (item.getQuantity() > 1000) {
                throw new InvalidQuantityException(
                        item.getMedicineId(),
                        item.getQuantity(),
                        "Quantity exceeds statutory prescription limit of 1000 units per order"
                );
            }
        }
    }

    /**
     * Processes an order with transactional stock reservation, multi-catch handling,
     * and a guaranteed audit trail in the finally block.
     *
     * @param order the order to process
     * @return the processed order with status CONFIRMED
     * @throws InsufficientStockException if stock is insufficient (checked exception)
     * @throws InvalidQuantityException   if quantity is invalid (unchecked exception)
     * @throws OrderProcessingException   if an unexpected failure occurs with chained cause
     */
    public Order processOrder(Order order)
            throws InsufficientStockException, InvalidQuantityException, OrderProcessingException {

        OrderStatus finalStatus = OrderStatus.FAILED;
        Throwable capturedException = null;
        List<OrderItem> reservedItems = new ArrayList<>();

        try {
            order.setStatus(OrderStatus.PROCESSING);

            // 1. Validation phase (may throw unchecked InvalidQuantityException)
            validateOrder(order);

            // 2. Stock reservation phase (may throw checked InsufficientStockException)
            for (OrderItem item : order.getItems()) {
                inventoryService.checkAndReserveStock(item.getMedicineId(), item.getQuantity());
                reservedItems.add(item);
            }

            // 3. Successful order confirmation
            finalStatus = OrderStatus.CONFIRMED;
            order.setStatus(finalStatus);
            return order;

        } catch (InsufficientStockException | InvalidQuantityException ex) {
            // MULTI-CATCH BLOCK:
            // Unified handling for both checked (stock) and unchecked (quantity) order business failures
            capturedException = ex;
            finalStatus = (ex instanceof InvalidQuantityException) ? OrderStatus.REJECTED : OrderStatus.FAILED;
            order.setStatus(finalStatus);

            // Rollback any items that were already reserved during this partial transaction
            rollbackReservedStock(reservedItems);

            // Re-throw so caller contract remains intact
            throw ex;

        } catch (Exception ex) {
            // Catch-all for unanticipated system errors, wrapping them in a chained OrderProcessingException
            capturedException = ex;
            finalStatus = OrderStatus.FAILED;
            order.setStatus(finalStatus);

            rollbackReservedStock(reservedItems);

            throw new OrderProcessingException(
                    order.getOrderId(),
                    "Unexpected system error during order processing pipeline",
                    ex // chained cause!
            );

        } finally {
            // GUARANTEED FINALLY AUDIT:
            // This block executes ALWAYS:
            // - On clean successful completion
            // - On checked InsufficientStockException
            // - On unchecked InvalidQuantityException
            // - On chained OrderProcessingException / runtime errors
            auditService.recordAudit(
                    order.getOrderId(),
                    "PROCESS_ORDER",
                    finalStatus,
                    capturedException == null
                            ? "Order processed and confirmed successfully"
                            : capturedException.getMessage(),
                    capturedException
            );
        }
    }

    /**
     * Helper to rollback reserved inventory on partial transaction failures.
     */
    private void rollbackReservedStock(List<OrderItem> reservedItems) {
        for (OrderItem item : reservedItems) {
            try {
                inventoryService.releaseStock(item.getMedicineId(), item.getQuantity());
            } catch (Exception e) {
                // Ignore rollback errors during cleanup to preserve primary exception
            }
        }
    }

    public InventoryService getInventoryService() {
        return inventoryService;
    }

    public AuditService getAuditService() {
        return auditService;
    }
}
