package com.payment.service;

/**
 * Interface defining refundable capability for payment methods.
 */
public interface Refundable {

    /**
     * Process a full or partial refund for the transaction.
     *
     * @param refundAmount The amount to refund.
     * @return true if refund processing succeeded, false otherwise.
     */
    boolean processRefund(double refundAmount);

    /**
     * Get the maximum amount that can be refunded.
     *
     * @return maximum refundable amount.
     */
    double getMaxRefundableAmount();

    /**
     * Check whether this payment is eligible for refund.
     *
     * @return true if eligible for refund, false otherwise.
     */
    boolean isEligibleForRefund();

    /**
     * Helper default method to calculate refund processing fee.
     *
     * @param amount base refund amount
     * @param feePercentage processing fee percentage
     * @return processing fee deduction
     */
    default double calculateRefundFee(double amount, double feePercentage) {
        if (amount <= 0 || feePercentage < 0) {
            return 0.0;
        }
        return amount * (feePercentage / 100.0);
    }
}
