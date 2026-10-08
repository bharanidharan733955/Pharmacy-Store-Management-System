package com.pharmacy.core.model;

import java.util.Objects;

/**
 * Represents a Supplier entity in the Pharmacy Store Management System.
 * Incorporates private fields (encapsulation), static instance counter,
 * 3-level chained constructors using this(...), field validation, and equals/hashCode.
 */
public class Supplier {

    // Static counter for tracking total Supplier instances created
    private static int supplierCounter = 0;

    // Encapsulated private fields
    private final String id;
    private String name;
    private String contactPerson;
    private String phone;

    /**
     * Constructor 1 (Single Argument): Chained to Constructor 2.
     * Defaults contact person and phone to "N/A".
     *
     * @param name Supplier company/distributor name
     */
    public Supplier(String name) {
        this(name, "N/A", "N/A");
    }

    /**
     * Constructor 2 (Three Arguments): Chained to Constructor 3.
     * Auto-generates ID by delegating null ID to Master Constructor.
     *
     * @param name          Supplier name
     * @param contactPerson Contact person name
     * @param phone         Contact phone number
     */
    public Supplier(String name, String contactPerson, String phone) {
        this(null, name, contactPerson, phone);
    }

    /**
     * Constructor 3 (Four Arguments): Master Constructor.
     * Validates input fields, increments static counter, and auto-generates ID if null/empty.
     *
     * @param id            Unique supplier ID (auto-generated if null or empty)
     * @param name          Supplier name
     * @param contactPerson Contact person name
     * @param phone         Contact phone number
     */
    public Supplier(String id, String name, String contactPerson, String phone) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Supplier name cannot be null or empty.");
        }
        if (contactPerson == null || contactPerson.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact person cannot be null or empty.");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty.");
        }

        supplierCounter++;
        this.id = (id != null && !id.trim().isEmpty()) ? id.trim() : "SUP-" + String.format("%04d", supplierCounter);
        this.name = name.trim();
        this.contactPerson = contactPerson.trim();
        this.phone = phone.trim();
    }

    // Static Getter & Reset for supplier counter
    public static int getSupplierCounter() {
        return supplierCounter;
    }

    public static void resetSupplierCounter() {
        supplierCounter = 0;
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
            throw new IllegalArgumentException("Supplier name cannot be null or empty.");
        }
        this.name = name.trim();
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        if (contactPerson == null || contactPerson.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact person cannot be null or empty.");
        }
        this.contactPerson = contactPerson.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty.");
        }
        this.phone = phone.trim();
    }

    /**
     * Equals: Suppliers are equal if they share the same ID.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Supplier supplier = (Supplier) o;
        return Objects.equals(id.toUpperCase(), supplier.id.toUpperCase());
    }

    /**
     * HashCode: Generated based on supplier ID.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id.toUpperCase());
    }

    @Override
    public String toString() {
        return String.format("[%s] %-22s | Contact: %-15s | Phone: %s", id, name, contactPerson, phone);
    }
}
