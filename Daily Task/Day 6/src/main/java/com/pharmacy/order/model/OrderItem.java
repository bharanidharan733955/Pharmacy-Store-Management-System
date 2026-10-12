package com.pharmacy.order.model;

public class OrderItem {
    private final String medicineId;
    private final String medicineName;
    private final int quantity;
    private final double unitPrice;

    public OrderItem(String medicineId, String medicineName, int quantity, double unitPrice) {
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getSubtotal() {
        return quantity * unitPrice;
    }

    @Override
    public String toString() {
        return String.format("%s (x%d @ ₹%.2f = ₹%.2f)", medicineName, quantity, unitPrice, getSubtotal());
    }
}
