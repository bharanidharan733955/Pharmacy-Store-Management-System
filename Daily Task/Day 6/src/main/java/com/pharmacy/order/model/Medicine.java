package com.pharmacy.order.model;

import java.util.Objects;

public class Medicine {
    private final String id;
    private final String name;
    private final double unitPrice;
    private int stockQuantity;

    public Medicine(String id, String name, double unitPrice, int stockQuantity) {
        this.id = Objects.requireNonNull(id, "Medicine ID cannot be null");
        this.name = Objects.requireNonNull(name, "Medicine name cannot be null");
        this.unitPrice = unitPrice;
        this.stockQuantity = stockQuantity;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public synchronized int getStockQuantity() {
        return stockQuantity;
    }

    public synchronized void reduceStock(int quantity) {
        this.stockQuantity -= quantity;
    }

    public synchronized void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    @Override
    public String toString() {
        return String.format("Medicine[id=%s, name='%s', price=₹%.2f, stock=%d]",
                id, name, unitPrice, stockQuantity);
    }
}
