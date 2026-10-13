package com.pharmacy.core.exception;

/**
 * Thrown when requested sale quantity exceeds available non-expired stock.
 */
public class InsufficientStockException extends BusinessRuleException {

    private final String medicineId;
    private final int requestedQuantity;
    private final int availableStock;

    public InsufficientStockException(String medicineId, int requestedQuantity, int availableStock) {
        super("INSUFFICIENT_STOCK", 409, String.format(
                "Insufficient stock for medicine '%s'. Requested: %d, Available non-expired: %d.",
                medicineId, requestedQuantity, availableStock));
        this.medicineId = medicineId;
        this.requestedQuantity = requestedQuantity;
        this.availableStock = availableStock;
    }

    public InsufficientStockException(String message) {
        super("INSUFFICIENT_STOCK", 409, message);
        this.medicineId = null;
        this.requestedQuantity = 0;
        this.availableStock = 0;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public int getAvailableStock() {
        return availableStock;
    }
}
