package com.pharmacy.order.exception;

/**
 * High-level business exception thrown when an order processing operation fails.
 * Designed to encapsulate chained root causes (e.g. InsufficientStockException,
 * InventorySyncException, etc.).
 */
public class OrderProcessingException extends Exception {
    private final String orderId;

    public OrderProcessingException(String orderId, String message) {
        super(String.format("Order [%s] processing failed: %s", orderId, message));
        this.orderId = orderId;
    }

    public OrderProcessingException(String orderId, String message, Throwable cause) {
        super(String.format("Order [%s] processing failed: %s", orderId, message), cause);
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
