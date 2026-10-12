package com.pharmacy.order.exception;

/**
 * Low-level checked exception representing an infrastructure or database error
 * during warehouse inventory synchronization.
 * <p>
 * This exception serves as a root cause when demonstrating chained exceptions.
 */
public class InventorySyncException extends Exception {
    private final String warehouseNode;

    public InventorySyncException(String message, String warehouseNode) {
        super(message);
        this.warehouseNode = warehouseNode;
    }

    public InventorySyncException(String message, String warehouseNode, Throwable cause) {
        super(message, cause);
        this.warehouseNode = warehouseNode;
    }

    public String getWarehouseNode() {
        return warehouseNode;
    }
}
