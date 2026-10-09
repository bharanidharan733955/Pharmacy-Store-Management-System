package com.payment.model;

import com.payment.service.Refundable;

/**
 * Concrete UpiPayment class extending Payment and implementing Refundable interface.
 */
public class UpiPayment extends Payment implements Refundable {
    private final String upiId;
    private String transactionRefNo;
    private final String upiProvider;

    public UpiPayment(String transactionId, double amount, String upiId, String upiProvider) {
        super(transactionId, amount);
        if (upiId == null || !upiId.contains("@")) {
            throw new IllegalArgumentException("Invalid UPI ID format. Must contain '@'.");
        }
        this.upiId = upiId;
        this.upiProvider = upiProvider != null ? upiProvider : "UPI Direct";
    }

    @Override
    public boolean processPayment() {
        System.out.println("Initiating UPI Payment via " + upiProvider + " for VPA: " + upiId + "...");
        this.transactionRefNo = "UPI-REF-" + System.currentTimeMillis();
        setStatus(PaymentStatus.SUCCESS);
        System.out.println("UPI Payment SUCCESSful. Ref No: " + transactionRefNo + ", Amount: ₹" + getAmount());
        return true;
    }

    /**
     * Overloaded pay method for UPI Recurring Auto-Pay Mandate.
     */
    public boolean pay(double amount, String mandateId, boolean isAutoPay) {
        if (isAutoPay) {
            System.out.println("Processing UPI Recurring Auto-Pay Mandate [" + mandateId + "] for amount: ₹" + amount);
        }
        return pay(amount);
    }

    @Override
    public boolean processRefund(double refundAmount) {
        if (!isEligibleForRefund()) {
            System.err.println("UPI Refund Error: Transaction status is " + getStatus());
            return false;
        }
        double remainingRefundable = getMaxRefundableAmount();
        if (refundAmount <= 0 || refundAmount > remainingRefundable) {
            System.err.println("UPI Refund Error: Invalid amount ₹" + refundAmount + ". Maximum refundable is ₹" + remainingRefundable);
            return false;
        }

        addRefundedAmount(refundAmount);
        if (getRefundedAmount() >= getAmount()) {
            setStatus(PaymentStatus.REFUNDED);
        } else {
            setStatus(PaymentStatus.PARTIALLY_REFUNDED);
        }

        System.out.printf("Instant UPI Refund of ₹%.2f processed to VPA: %s (Ref: %s)%n", refundAmount, upiId, transactionRefNo);
        return true;
    }

    @Override
    public double getMaxRefundableAmount() {
        return getAmount() - getRefundedAmount();
    }

    @Override
    public boolean isEligibleForRefund() {
        return getStatus() == PaymentStatus.SUCCESS || getStatus() == PaymentStatus.PARTIALLY_REFUNDED;
    }

    @Override
    public String getPaymentChannelDetails() {
        return "UPI (" + upiProvider + ") [" + upiId + "]";
    }

    public String getUpiId() {
        return upiId;
    }

    public String getTransactionRefNo() {
        return transactionRefNo;
    }

    public String getUpiProvider() {
        return upiProvider;
    }
}
