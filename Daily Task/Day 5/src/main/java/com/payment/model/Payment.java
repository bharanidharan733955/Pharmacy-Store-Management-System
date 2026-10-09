package com.payment.model;

import java.time.LocalDateTime;

/**
 * Abstract superclass representing a general Payment transaction.
 * Demonstrates abstract methods, encapsulation, method overloading, and polymorphic payment execution.
 */
public abstract class Payment {
    private final String transactionId;
    private double amount;
    private final LocalDateTime timestamp;
    private PaymentStatus status;
    private double refundedAmount;

    public Payment(String transactionId, double amount) {
        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be null or empty.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero.");
        }
        this.transactionId = transactionId;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.status = PaymentStatus.PENDING;
        this.refundedAmount = 0.0;
    }

    // Abstract method to be implemented by concrete payment channels (Card, UPI, Cash)
    public abstract boolean processPayment();

    // Abstract method for acquiring payment channel specific details
    public abstract String getPaymentChannelDetails();

    // -------------------------------------------------------------
    // OVERLOADED pay() METHODS (Compile-time Polymorphism / Method Overloading)
    // -------------------------------------------------------------

    /**
     * Overloaded pay #1: Execute payment with configured amount.
     */
    public boolean pay() {
        return processPayment();
    }

    /**
     * Overloaded pay #2: Update payment amount and execute transaction.
     */
    public boolean pay(double customAmount) {
        if (customAmount <= 0) {
            throw new IllegalArgumentException("Custom payment amount must be positive.");
        }
        this.amount = customAmount;
        return processPayment();
    }

    /**
     * Overloaded pay #3: Execute payment specifying amount and currency.
     */
    public boolean pay(double customAmount, String currency) {
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency code cannot be empty.");
        }
        this.amount = customAmount;
        System.out.println("Processing transaction " + transactionId + " in currency: " + currency);
        return processPayment();
    }

    /**
     * Overloaded pay #4: Execute payment applying a percentage discount.
     */
    public boolean pay(double customAmount, double discountPercentage) {
        if (discountPercentage < 0 || discountPercentage > 100) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100.");
        }
        double discount = customAmount * (discountPercentage / 100.0);
        double finalAmount = customAmount - discount;
        this.amount = finalAmount;
        System.out.printf("Applied discount of %.2f%%. Final payable amount: ₹%.2f%n", discountPercentage, finalAmount);
        return processPayment();
    }

    /**
     * Overloaded pay #5: Execute payment applying a promotional voucher code.
     */
    public boolean pay(double customAmount, String promoCode, double promoDiscount) {
        System.out.println("Applying Promo Code: [" + promoCode + "]");
        return pay(customAmount - promoDiscount);
    }

    // Getters and Setters
    public String getTransactionId() {
        return transactionId;
    }

    public double getAmount() {
        return amount;
    }

    protected void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public double getRefundedAmount() {
        return refundedAmount;
    }

    public void addRefundedAmount(double refund) {
        this.refundedAmount += refund;
    }

    public String generateReceipt() {
        return String.format(
            "================ RECEIPT ================\n" +
            "Txn ID    : %s\n" +
            "Channel   : %s\n" +
            "Amount    : ₹%.2f\n" +
            "Status    : %s\n" +
            "Refunded  : ₹%.2f\n" +
            "Timestamp : %s\n" +
            "=========================================",
            transactionId, getPaymentChannelDetails(), amount, status, refundedAmount, timestamp
        );
    }

    @Override
    public String toString() {
        return "Payment{" +
                "transactionId='" + transactionId + '\'' +
                ", amount=" + amount +
                ", status=" + status +
                ", timestamp=" + timestamp +
                '}';
    }
}
