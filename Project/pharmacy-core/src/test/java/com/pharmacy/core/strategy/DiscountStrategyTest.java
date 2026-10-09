package com.pharmacy.core.strategy;

import com.pharmacy.core.model.AdminUser;
import com.pharmacy.core.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DiscountStrategyTest {

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new AdminUser("admin", "admin@test.com", "Admin User");
    }

    @Test
    @DisplayName("Senior Citizen Discount calculates 15% capped at ₹500")
    void testSeniorCitizenDiscount() {
        DiscountStrategy seniorStrategy = new SeniorCitizenDiscountStrategy(15.0, 500.00);

        // Subtotal ₹2000 => 15% is ₹300 (under cap)
        double discount1 = seniorStrategy.calculateDiscount(2000.00, 2, testUser);
        assertEquals(300.00, discount1, 0.01);

        // Subtotal ₹5000 => 15% is ₹750 => Capped at ₹500
        double discount2 = seniorStrategy.calculateDiscount(5000.00, 5, testUser);
        assertEquals(500.00, discount2, 0.01);
    }

    @Test
    @DisplayName("Bulk Purchase Discount applies tiered percentages based on item quantity")
    void testBulkPurchaseDiscount() {
        DiscountStrategy bulkStrategy = new BulkPurchaseDiscountStrategy();

        // 5 items => 0% discount
        assertEquals(0.00, bulkStrategy.calculateDiscount(1000.00, 5, testUser));

        // 15 items => 5% discount on 1000 = ₹50
        assertEquals(50.00, bulkStrategy.calculateDiscount(1000.00, 15, testUser));

        // 25 items => 10% discount on 1000 = ₹100
        assertEquals(100.00, bulkStrategy.calculateDiscount(1000.00, 25, testUser));

        // 60 items => 20% discount on 1000 = ₹200
        assertEquals(200.00, bulkStrategy.calculateDiscount(1000.00, 60, testUser));
    }
}
