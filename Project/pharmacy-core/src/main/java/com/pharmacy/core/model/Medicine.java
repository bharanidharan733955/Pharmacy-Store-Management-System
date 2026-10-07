package com.pharmacy.core.model;

public class Medicine {
    private final String id;
    private final String name;
    private final String category;
    private final double unitPrice;
    private final boolean requiresPrescription;

    public Medicine(String id, String name, String category, double unitPrice, boolean requiresPrescription) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.unitPrice = unitPrice;
        this.requiresPrescription = requiresPrescription;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public boolean isRequiresPrescription() {
        return requiresPrescription;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-20s | Cat: %-12s | Price: ₹%7.2f | Rx Required: %s",
                id, name, category, unitPrice, requiresPrescription ? "YES" : "NO");
    }
}
