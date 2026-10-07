package com.pharmacy.core.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Invoice {
    private final String invoiceId;
    private final String medicineName;
    private final int quantity;
    private final double totalAmount;
    private final String timestamp;

    public Invoice(String invoiceId, String medicineName, int quantity, double totalAmount) {
        this.invoiceId = invoiceId;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

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

    @Override
    public String toString() {
        return String.format("[%s] Inv: %s | Item: %-15s | Qty: %2d | Total: ₹%8.2f",
                timestamp, invoiceId, medicineName, quantity, totalAmount);
    }
}
