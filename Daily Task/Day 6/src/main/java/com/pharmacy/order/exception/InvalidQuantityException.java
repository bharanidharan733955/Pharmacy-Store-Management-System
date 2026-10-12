package com.pharmacy.order.exception;

/**
 * Custom unchecked exception indicating that an invalid quantity
 * (e.g., negative value, zero, or beyond allowed batch maximum) was requested.
 * <p>
 * Being an unchecked exception (subclass of {@link RuntimeException}),
 * it signals a programming error or invalid client input validation failure.
 */
public class InvalidQuantityException extends RuntimeException {
    private final String medicineId;
    private final int invalidQuantity;

    public InvalidQuantityException(String message) {
        super(message);
        this.medicineId = "UNKNOWN";
        this.invalidQuantity = 0;
    }

    public InvalidQuantityException(String message, Throwable cause) {
        super(message, cause);
        this.medicineId = "UNKNOWN";
        this.invalidQuantity = 0;
    }

    public InvalidQuantityException(String medicineId, int invalidQuantity, String message) {
        super(String.format("Invalid quantity [%d] for item '%s': %s", invalidQuantity, medicineId, message));
        this.medicineId = medicineId;
        this.invalidQuantity = invalidQuantity;
    }

    public InvalidQuantityException(String medicineId, int invalidQuantity, String message, Throwable cause) {
        super(String.format("Invalid quantity [%d] for item '%s': %s", invalidQuantity, medicineId, message), cause);
        this.medicineId = medicineId;
        this.invalidQuantity = invalidQuantity;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public int getInvalidQuantity() {
        return invalidQuantity;
    }
}
