package com.pharmacy.core.strategy;

import com.pharmacy.core.model.User;

/**
 * Strategy interface for calculating item/invoice discounts in the Pharmacy Store.
 * Demonstrates the Strategy Behavioral Design Pattern.
 */
public interface DiscountStrategy {

    /**
     * Calculate discount amount for a purchase transaction.
     *
     * @param subtotal          Original order subtotal
     * @param totalItemQuantity Total quantity of items in order
     * @param authenticatedUser User/Employee authorizing the transaction
     * @return Discount amount to be deducted from subtotal
     */
    double calculateDiscount(double subtotal, int totalItemQuantity, User authenticatedUser);

    /**
     * Get human-readable name of the discount strategy.
     *
     * @return strategy display name
     */
    String getStrategyName();

    /**
     * Get detailed description of discount rules.
     *
     * @return strategy description
     */
    String getStrategyDescription();
}
