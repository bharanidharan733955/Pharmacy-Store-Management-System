package com.pharmacy.core.exception;

import java.time.LocalDate;

/**
 * Thrown when a sale attempts to use an expired batch or when all available batches are expired.
 */
public class ExpiredBatchException extends BusinessRuleException {

    private final String batchNumber;
    private final String medicineId;
    private final LocalDate expiryDate;

    public ExpiredBatchException(String batchNumber, String medicineId, LocalDate expiryDate) {
        super("EXPIRED_BATCH", 409, String.format(
                "Cannot dispense expired medicine. Batch '%s' for medicine '%s' expired on %s.",
                batchNumber, medicineId, expiryDate));
        this.batchNumber = batchNumber;
        this.medicineId = medicineId;
        this.expiryDate = expiryDate;
    }

    public ExpiredBatchException(String message) {
        super("EXPIRED_BATCH", 409, message);
        this.batchNumber = null;
        this.medicineId = null;
        this.expiryDate = null;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }
}
