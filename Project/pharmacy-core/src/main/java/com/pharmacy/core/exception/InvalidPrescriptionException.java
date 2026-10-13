package com.pharmacy.core.exception;

/**
 * Thrown when a scheduled/prescription-required medicine is billed without a valid prescription reference.
 */
public class InvalidPrescriptionException extends BusinessRuleException {

    private final String medicineId;
    private final String medicineName;

    public InvalidPrescriptionException(String medicineId, String medicineName) {
        super("PRESCRIPTION_REFERENCE_REQUIRED", 409, String.format(
                "Medicine '%s' (%s) is a scheduled drug requiring a valid doctor prescription reference.",
                medicineName, medicineId));
        this.medicineId = medicineId;
        this.medicineName = medicineName;
    }

    public InvalidPrescriptionException(String message) {
        super("PRESCRIPTION_REFERENCE_REQUIRED", 409, message);
        this.medicineId = null;
        this.medicineName = null;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public String getMedicineName() {
        return medicineName;
    }
}
