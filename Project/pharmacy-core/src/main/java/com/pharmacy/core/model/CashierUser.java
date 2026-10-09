package com.pharmacy.core.model;

import java.util.List;

/**
 * Concrete CashierUser extending User in the Role Hierarchy.
 * Authorized for point-of-sale billing, issuing customer receipts, and processing cash transactions.
 */
public class CashierUser extends User {

    private final String posRegisterId;

    public CashierUser(String id, String username, String email, String fullName, String posRegisterId) {
        super(id, username, email, fullName, UserRole.CASHIER);
        this.posRegisterId = posRegisterId != null ? posRegisterId : "POS-REG-01";
        initializeCashierPermissions();
    }

    public CashierUser(String username, String email, String fullName, String posRegisterId) {
        this(null, username, email, fullName, posRegisterId);
    }

    private void initializeCashierPermissions() {
        List<String> defaultPermissions = List.of(
            "PROCESS_BILLING",
            "ISSUE_RECEIPT",
            "PROCESS_CUSTOMER_REFUND",
            "VIEW_INVENTORY"
        );
        defaultPermissions.forEach(this::grantPermission);
    }

    @Override
    public String getRoleDescription() {
        return "POS Cashier [Register: " + posRegisterId + "] - Authorized for POS counter billing and customer payment processing.";
    }

    @Override
    public boolean canPerformAction(String actionCode) {
        if (actionCode == null) return false;
        String action = actionCode.toUpperCase();
        // Cashiers cannot edit stock or manage suppliers/users
        if ("MANAGE_INVENTORY".equals(action) || "MANAGE_SUPPLIERS".equals(action) || "MANAGE_USERS".equals(action)) {
            return false;
        }
        return hasPermission(action);
    }

    public String getPosRegisterId() {
        return posRegisterId;
    }
}
