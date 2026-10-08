package com.pharmacy.core.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents an Inventory Batch entity in the Pharmacy Store Management System.
 * Incorporates private fields (encapsulation), static instance counter,
 * 3-level chained constructors using this(...), field validation, and equals/hashCode.
 */
public class Batch {

    // Static counter for tracking total Batch instances created
    private static int batchCounter = 0;

    // Encapsulated private fields
    private final String batchNumber;
    private final String medicineId;
    private LocalDate expiryDate;
    private int quantity;

    /**
     * Constructor 1 (Two Arguments): Chained to Constructor 2.
     * Defaults expiry date to 1 year from current date.
     *
     * @param medicineId ID of associated medicine
     * @param quantity   Quantity of stock in this batch
     */
    public Batch(String medicineId, int quantity) {
        this(medicineId, LocalDate.now().plusYears(1), quantity);
    }

    /**
     * Constructor 2 (Three Arguments): Chained to Constructor 3.
     * Auto-generates batch number by passing null to Master Constructor.
     *
     * @param medicineId Associated medicine ID
     * @param expiryDate Expiry date of batch
     * @param quantity   Quantity of stock
     */
    public Batch(String medicineId, LocalDate expiryDate, int quantity) {
        this(null, medicineId, expiryDate, quantity);
    }

    /**
     * Constructor 3 (Four Arguments): Master Constructor.
     * Validates input fields, increments static counter, and auto-generates batch number if null/empty.
     *
     * @param batchNumber Batch number (auto-generated if null or empty)
     * @param medicineId  Associated medicine ID
     * @param expiryDate  Expiry date of batch (must not be null)
     * @param quantity    Batch quantity (must be >= 0)
     */
    public Batch(String batchNumber, String medicineId, LocalDate expiryDate, int quantity) {
        if (medicineId == null || medicineId.trim().isEmpty()) {
            throw new IllegalArgumentException("Medicine ID cannot be null or empty.");
        }
        if (expiryDate == null) {
            throw new IllegalArgumentException("Expiry date cannot be null.");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }

        batchCounter++;
        this.batchNumber = (batchNumber != null && !batchNumber.trim().isEmpty())
                ? batchNumber.trim()
                : "BAT-" + String.format("%04d", batchCounter);
        this.medicineId = medicineId.trim();
        this.expiryDate = expiryDate;
        this.quantity = quantity;
    }

    // Static Getter & Reset for batch counter
    public static int getBatchCounter() {
        return batchCounter;
    }

    public static void resetBatchCounter() {
        batchCounter = 0;
    }

    // Getters and Validated Setters/Methods
    public String getBatchNumber() {
        return batchNumber;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        if (expiryDate == null) {
            throw new IllegalArgumentException("Expiry date cannot be null.");
        }
        this.expiryDate = expiryDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        this.quantity = quantity;
    }

    /**
     * Validates and reduces batch stock quantity.
     *
     * @param amount Amount to reduce (must be > 0 and <= quantity)
     */
    public void reduceQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Reduction amount must be positive.");
        }
        if (amount > this.quantity) {
            throw new IllegalArgumentException("Cannot reduce quantity beyond available stock.");
        }
        this.quantity -= amount;
    }

    /**
     * Validates and increases batch stock quantity.
     *
     * @param amount Amount to add (must be > 0)
     */
    public void addQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Addition amount must be positive.");
        }
        this.quantity += amount;
    }

    /**
     * Returns true if batch expiry date is strictly before today's date.
     */
    public boolean isExpired() {
        return expiryDate.isBefore(LocalDate.now());
    }

    /**
     * Equals: Batches are equal if they share the same batch number.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Batch batch = (Batch) o;
        return Objects.equals(batchNumber.toUpperCase(), batch.batchNumber.toUpperCase());
    }

    /**
     * HashCode: Generated based on batch number.
     */
    @Override
    public int hashCode() {
        return Objects.hash(batchNumber.toUpperCase());
    }

    @Override
    public String toString() {
        return String.format("Batch: %-10s | Med ID: %-8s | Expiry: %s | Qty: %d",
                batchNumber, medicineId, expiryDate, quantity);
    }
}
