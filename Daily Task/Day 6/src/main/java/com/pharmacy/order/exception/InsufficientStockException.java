package com.pharmacy.order.exception;

/**
 * Custom checked exception indicating that an item's requested quantity
 * exceeds the available warehouse inventory.
 * <p>
 * Being a checked exception, callers must either handle this explicitly
 * or declare it in their {@code throws} clause.
 */
public class InsufficientStockException extends Exception {
    private final String medicineId;
    private final String medicineName;
    private final int requestedQuantity;
    private final int availableQuantity;

    public InsufficientStockException(String message) {
        super(message);
        this.medicineId = "UNKNOWN";
        this.medicineName = "UNKNOWN";
        this.requestedQuantity = 0;
        this.availableQuantity = 0;
    }

    public InsufficientStockException(String message, Throwable cause) {
        super(message, cause);
        this.medicineId = "UNKNOWN";
        this.medicineName = "UNKNOWN";
        this.requestedQuantity = 0;
        this.availableQuantity = 0;
    }

    public InsufficientStockException(String medicineId, String medicineName, int requestedQuantity, int availableQuantity) {
        super(String.format("Insufficient stock for '%s' (ID: %s). Requested: %d, Available: %d",
                medicineName, medicineId, requestedQuantity, availableQuantity));
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    public InsufficientStockException(String medicineId, String medicineName, int requestedQuantity,
                                      int availableQuantity, String message, Throwable cause) {
        super(message, cause);
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }
}
