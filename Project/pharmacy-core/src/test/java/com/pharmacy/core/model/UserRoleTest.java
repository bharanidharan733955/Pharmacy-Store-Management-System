package com.pharmacy.core.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRoleTest {

    @Test
    @DisplayName("AdminUser has full permissions and handles actions")
    void testAdminUserPermissions() {
        AdminUser admin = new AdminUser("admin1", "admin@pharmacy.com", "System Superuser");
        assertEquals(UserRole.ADMIN, admin.getRole());
        assertTrue(admin.canPerformAction("MANAGE_USERS"));
        assertTrue(admin.canPerformAction("SYSTEM_CONFIGURATION"));
        assertTrue(admin.hasPermission("MANAGE_INVENTORY"));
        assertNotNull(admin.getRoleDescription());
    }

    @Test
    @DisplayName("PharmacistUser permission restrictions")
    void testPharmacistPermissions() {
        PharmacistUser pharmacist = new PharmacistUser("pharm1", "pharm@pharmacy.com", "Dr. Sarah", "PH-LICENSE-88");
        assertEquals(UserRole.PHARMACIST, pharmacist.getRole());
        assertTrue(pharmacist.canPerformAction("DISPENSE_PRESCRIPTION"));
        assertFalse(pharmacist.canPerformAction("MANAGE_USERS"));
    }

    @Test
    @DisplayName("CashierUser permission restrictions")
    void testCashierPermissions() {
        CashierUser cashier = new CashierUser("cashier1", "cashier@pharmacy.com", "Alex", "REG-01");
        assertEquals(UserRole.CASHIER, cashier.getRole());
        assertTrue(cashier.canPerformAction("PROCESS_BILLING"));
        assertFalse(cashier.canPerformAction("MANAGE_INVENTORY"));
    }
}
