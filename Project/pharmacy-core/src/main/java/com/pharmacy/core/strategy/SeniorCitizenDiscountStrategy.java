package com.pharmacy.core.strategy;

import com.pharmacy.core.model.User;

/**
 * Concrete Discount Strategy for Senior Citizen healthcare purchases.
 * Applies a 15% discount on medicine subtotal up to a maximum cap of ₹500.00.
 */
public class SeniorCitizenDiscountStrategy implements DiscountStrategy {

    private final double discountPercentage;
    private final double maxDiscountCap;

    public SeniorCitizenDiscountStrategy() {
        this(15.0, 500.00);
    }

    public SeniorCitizenDiscountStrategy(double discountPercentage, double maxDiscountCap) {
        if (discountPercentage < 0 || discountPercentage > 100) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100.");
        }
        this.discountPercentage = discountPercentage;
        this.maxDiscountCap = maxDiscountCap;
    }

    @Override
    public double calculateDiscount(double subtotal, int totalItemQuantity, User authenticatedUser) {
        if (subtotal <= 0) {
            return 0.0;
        }

        double calculatedDiscount = subtotal * (discountPercentage / 100.0);
        double finalDiscount = Math.min(calculatedDiscount, maxDiscountCap);

        System.out.printf("Applied Senior Citizen Discount (%.1f%%): ₹%.2f (Capped at ₹%.2f)%n",
                discountPercentage, finalDiscount, maxDiscountCap);

        return finalDiscount;
    }

    @Override
    public String getStrategyName() {
        return "Senior Citizen Healthcare Discount";
    }

    @Override
    public String getStrategyDescription() {
        return String.format("Flat %.1f%% discount for senior citizen prescriptions (Max Cap: ₹%.2f)", discountPercentage, maxDiscountCap);
    }
}
