package com.pharmacy.core.model;

import java.util.List;

/**
 * Concrete AdminUser extending User in the Role Hierarchy.
 * Has administrative access across all pharmacy modules.
 */
public class AdminUser extends User {

    private String adminLevel; // e.g. "SUPER_ADMIN", "STORE_ADMIN"

    public AdminUser(String id, String username, String email, String fullName, String adminLevel) {
        super(id, username, email, fullName, UserRole.ADMIN);
        this.adminLevel = adminLevel != null ? adminLevel : "STORE_ADMIN";
        initializeAdminPermissions();
    }

    public AdminUser(String username, String email, String fullName) {
        this(null, username, email, fullName, "STORE_ADMIN");
    }

    private void initializeAdminPermissions() {
        List<String> defaultAdminPermissions = List.of(
            "MANAGE_INVENTORY",
            "MANAGE_USERS",
            "PROCESS_BILLING",
            "VIEW_FINANCIAL_REPORTS",
            "MANAGE_SUPPLIERS",
            "APPLY_CUSTOM_DISCOUNTS",
            "SYSTEM_CONFIGURATION"
        );
        defaultAdminPermissions.forEach(this::grantPermission);
    }

    @Override
    public String getRoleDescription() {
        return "System Administrator [" + adminLevel + "] - Full access to all administrative and pharmacy operations.";
    }

    @Override
    public boolean canPerformAction(String actionCode) {
        // Admins can perform any action or explicitly granted permission
        return true;
    }

    public String getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel;
        touch();
    }
}
