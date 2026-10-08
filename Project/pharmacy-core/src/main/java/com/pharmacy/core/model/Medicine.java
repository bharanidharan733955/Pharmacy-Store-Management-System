package com.pharmacy.core.model;

import java.util.Objects;

/**
 * Represents a Medicine item in the Pharmacy Store Management System.
 * Incorporates private fields (encapsulation), static instance counter,
 * 3-level chained constructors using this(...), field validation, and equals/hashCode.
 */
public class Medicine {

    // Static counter for tracking total Medicine instances created
    private static int medicineCounter = 0;

    // Encapsulated private fields
    private final String id;
    private String name;
    private String category;
    private double unitPrice;
    private boolean requiresPrescription;

    /**
     * Constructor 1 (Single Argument): Chained to Constructor 2.
     * Defaults category to "GENERAL" and unitPrice to 10.00.
     *
     * @param name Name of the medicine
     */
    public Medicine(String name) {
        this(name, "GENERAL", 10.00);
    }

    /**
     * Constructor 2 (Three Arguments): Chained to Constructor 3.
     * Defaults prescription requirement to false.
     *
     * @param name      Name of the medicine
     * @param category  Category of the medicine
     * @param unitPrice Unit price of the medicine
     */
    public Medicine(String name, String category, double unitPrice) {
        this(null, name, category, unitPrice, false);
    }

    /**
     * Constructor 3 (Five Arguments): Master Constructor.
     * Validates input fields, increments static counter, and auto-generates ID if null/empty.
     *
     * @param id                   Unique medicine ID (auto-generated if null or empty)
     * @param name                 Name of the medicine
     * @param category             Category (e.g. Analgesic, Antibiotic)
     * @param unitPrice            Unit price (must be >= 0)
     * @param requiresPrescription Whether prescription is required for POS billing
     */
    public Medicine(String id, String name, String category, double unitPrice, boolean requiresPrescription) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Medicine name cannot be null or empty.");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Medicine category cannot be null or empty.");
        }
        if (unitPrice < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative.");
        }

        medicineCounter++;
        this.id = (id != null && !id.trim().isEmpty()) ? id.trim() : "MED-" + String.format("%04d", medicineCounter);
        this.name = name.trim();
        this.category = category.trim();
        this.unitPrice = unitPrice;
        this.requiresPrescription = requiresPrescription;
    }

    // Static Getter & Reset methods for medicine counter
    public static int getMedicineCounter() {
        return medicineCounter;
    }

    public static void resetMedicineCounter() {
        medicineCounter = 0;
    }

    // Getters and Validated Setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Medicine name cannot be null or empty.");
        }
        this.name = name.trim();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Medicine category cannot be null or empty.");
        }
        this.category = category.trim();
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        if (unitPrice < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative.");
        }
        this.unitPrice = unitPrice;
    }

    public boolean isRequiresPrescription() {
        return requiresPrescription;
    }

    public void setRequiresPrescription(boolean requiresPrescription) {
        this.requiresPrescription = requiresPrescription;
    }

    /**
     * Equals: Medicines are equal if they have the same ID (case-insensitive).
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medicine medicine = (Medicine) o;
        return Objects.equals(id.toUpperCase(), medicine.id.toUpperCase());
    }

    /**
     * HashCode: Generated based on medicine ID.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id.toUpperCase());
    }

    @Override
    public String toString() {
        return String.format("[%s] %-20s | Cat: %-12s | Price: ₹%7.2f | Rx Required: %s",
                id, name, category, unitPrice, requiresPrescription ? "YES" : "NO");
    }
}
