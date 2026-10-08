package com.pharmacy.core.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Represents a POS Sale Invoice in the Pharmacy Store Management System.
 * Incorporates private fields (encapsulation), static instance counter,
 * constructor chaining using this(...), field validation, and equals/hashCode.
 */
public class Invoice {

    // Static counter for tracking total Invoice instances created
    private static int invoiceCounter = 0;

    // Encapsulated private fields
    private final String invoiceId;
    private final String medicineName;
    private final int quantity;
    private final double totalAmount;
    private final String timestamp;

    /**
     * Constructor 1 (Two Arguments): Chained to Master Constructor.
     * Defaults totalAmount to 0.00.
     */
    public Invoice(String medicineName, int quantity) {
        this(null, medicineName, quantity, 0.00);
    }

    /**
     * Constructor 2 (Three Arguments): Chained to Master Constructor.
     * Auto-generates invoice ID by delegating null ID to Master Constructor.
     */
    public Invoice(String medicineName, int quantity, double totalAmount) {
        this(null, medicineName, quantity, totalAmount);
    }

    /**
     * Master Constructor (Four Arguments).
     * Validates input fields, increments static counter, and auto-generates invoice ID if null/empty.
     */
    public Invoice(String invoiceId, String medicineName, int quantity, double totalAmount) {
        if (medicineName == null || medicineName.trim().isEmpty()) {
            throw new IllegalArgumentException("Medicine name cannot be null or empty.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        if (totalAmount < 0) {
            throw new IllegalArgumentException("Total amount cannot be negative.");
        }

        invoiceCounter++;
        this.invoiceId = (invoiceId != null && !invoiceId.trim().isEmpty())
                ? invoiceId.trim()
                : "INV-" + String.format("%04d", invoiceCounter);
        this.medicineName = medicineName.trim();
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // Static Getter & Reset for invoice counter
    public static int getInvoiceCounter() {
        return invoiceCounter;
    }

    public static void resetInvoiceCounter() {
        invoiceCounter = 0;
    }

    // Getters
    public String getInvoiceId() {
        return invoiceId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Equals: Invoices are equal if they share the same invoice ID.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Invoice invoice = (Invoice) o;
        return Objects.equals(invoiceId.toUpperCase(), invoice.invoiceId.toUpperCase());
    }

    /**
     * HashCode: Generated based on invoice ID.
     */
    @Override
    public int hashCode() {
        return Objects.hash(invoiceId.toUpperCase());
    }

    @Override
    public String toString() {
        return String.format("[%s] Inv: %s | Item: %-15s | Qty: %2d | Total: ₹%8.2f",
                timestamp, invoiceId, medicineName, quantity, totalAmount);
    }
}
