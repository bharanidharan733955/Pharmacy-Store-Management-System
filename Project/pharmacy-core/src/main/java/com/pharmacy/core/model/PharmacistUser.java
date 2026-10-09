package com.pharmacy.core.model;

import java.util.List;

/**
 * Concrete PharmacistUser extending User in the Role Hierarchy.
 * Authorized for medicine dispensing, inventory operations, and supplier management.
 */
public class PharmacistUser extends User {

    private final String licenseNumber;

    public PharmacistUser(String id, String username, String email, String fullName, String licenseNumber) {
        super(id, username, email, fullName, UserRole.PHARMACIST);
        if (licenseNumber == null || licenseNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Pharmacist license number is mandatory.");
        }
        this.licenseNumber = licenseNumber;
        initializePharmacistPermissions();
    }

    public PharmacistUser(String username, String email, String fullName, String licenseNumber) {
        this(null, username, email, fullName, licenseNumber);
    }

    private void initializePharmacistPermissions() {
        List<String> defaultPermissions = List.of(
            "MANAGE_INVENTORY",
            "PROCESS_BILLING",
            "DISPENSE_PRESCRIPTION",
            "MANAGE_SUPPLIERS",
            "VIEW_BATCH_EXPIRY"
        );
        defaultPermissions.forEach(this::grantPermission);
    }

    @Override
    public String getRoleDescription() {
        return "Licensed Pharmacist [License: " + licenseNumber + "] - Authorized for prescription dispensing and inventory stock management.";
    }

    @Override
    public boolean canPerformAction(String actionCode) {
        if (actionCode == null) return false;
        String action = actionCode.toUpperCase();
        // Pharmacist cannot access system admin settings or user management
        if ("MANAGE_USERS".equals(action) || "SYSTEM_CONFIGURATION".equals(action)) {
            return false;
        }
        return hasPermission(action);
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }
}
