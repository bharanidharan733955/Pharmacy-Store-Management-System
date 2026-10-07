package com.pharmacy.core.model;

import java.time.LocalDate;

public class Batch {
    private final String batchNumber;
    private final String medicineId;
    private final LocalDate expiryDate;
    private int quantity;

    public Batch(String batchNumber, String medicineId, LocalDate expiryDate, int quantity) {
        this.batchNumber = batchNumber;
        this.medicineId = medicineId;
        this.expiryDate = expiryDate;
        this.quantity = quantity;
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

    public int getQuantity() {
        return quantity;
    }

    public void reduceQuantity(int amount) {
        this.quantity -= amount;
    }

    @Override
    public String toString() {
        return String.format("Batch: %-10s | Med ID: %-8s | Expiry: %s | Qty: %d",
                batchNumber, medicineId, expiryDate, quantity);
    }
}
