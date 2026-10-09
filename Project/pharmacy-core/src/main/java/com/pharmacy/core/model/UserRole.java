package com.pharmacy.core.model;

/**
 * Enum defining the primary authorization roles within the Pharmacy Store Management System.
 */
public enum UserRole {
    ADMIN("Administrator"),
    PHARMACIST("Licensed Pharmacist"),
    CASHIER("POS Cashier"),
    STORE_MANAGER("Store Manager");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
