package com.pharmacy.core.strategy;

import com.pharmacy.core.model.User;

/**
 * Concrete Default Discount Strategy applying zero discount (Standard Retail Pricing).
 */
public class NoDiscountStrategy implements DiscountStrategy {

    @Override
    public double calculateDiscount(double subtotal, int totalItemQuantity, User authenticatedUser) {
        return 0.0;
    }

    @Override
    public String getStrategyName() {
        return "Standard Retail Price (No Discount)";
    }

    @Override
    public String getStrategyDescription() {
        return "Standard retail pricing without special promotional discounts.";
    }
}
