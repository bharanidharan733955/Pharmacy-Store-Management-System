package com.pharmacy.core.strategy;

import com.pharmacy.core.model.User;

/**
 * Concrete Discount Strategy for Bulk/Wholesale order quantities.
 * Applies tiered discount percentages based on total items purchased:
 * - 50+ items : 20% discount
 * - 20+ items : 10% discount
 * - 10+ items : 5% discount
 */
public class BulkPurchaseDiscountStrategy implements DiscountStrategy {

    @Override
    public double calculateDiscount(double subtotal, int totalItemQuantity, User authenticatedUser) {
        if (subtotal <= 0 || totalItemQuantity <= 0) {
            return 0.0;
        }

        double discountRate = 0.0;
        if (totalItemQuantity >= 50) {
            discountRate = 0.20; // 20%
        } else if (totalItemQuantity >= 20) {
            discountRate = 0.10; // 10%
        } else if (totalItemQuantity >= 10) {
            discountRate = 0.05; // 5%
        }

        double discountAmount = subtotal * discountRate;
        System.out.printf("Applied Bulk Order Tiered Discount (%.0f%% for %d items): ₹%.2f%n",
                discountRate * 100, totalItemQuantity, discountAmount);

        return discountAmount;
    }

    @Override
    public String getStrategyName() {
        return "Bulk Order Wholesale Discount";
    }

    @Override
    public String getStrategyDescription() {
        return "Tiered volume discounts (10+ items: 5%, 20+ items: 10%, 50+ items: 20%)";
    }
}
