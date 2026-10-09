package com.payment.model;

import com.payment.service.Refundable;

/**
 * Concrete CashPayment class extending Payment and implementing Refundable interface.
 */
public class CashPayment extends Payment implements Refundable {
    private double cashTendered;
    private double changeGiven;

    public CashPayment(String transactionId, double amount, double cashTendered) {
        super(transactionId, amount);
        this.cashTendered = cashTendered;
        this.changeGiven = 0.0;
    }

    @Override
    public boolean processPayment() {
        System.out.println("Processing Cash Payment...");
        if (cashTendered < getAmount()) {
            System.err.println("Cash Payment Failed: Insufficient cash tendered. Required: ₹" + getAmount() + ", Provided: ₹" + cashTendered);
            setStatus(PaymentStatus.FAILED);
            return false;
        }

        this.changeGiven = cashTendered - getAmount();
        setStatus(PaymentStatus.SUCCESS);
        System.out.printf("Cash Payment SUCCESSful. Total: ₹%.2f, Tendered: ₹%.2f, Change Returned: ₹%.2f%n",
                getAmount(), cashTendered, changeGiven);
        return true;
    }

    /**
     * Overloaded pay method for Cash payment specifying exact cash tendered amount.
     */
    public boolean pay(double amount, double cashTendered) {
        this.cashTendered = cashTendered;
        return pay(amount);
    }

    @Override
    public boolean processRefund(double refundAmount) {
        if (!isEligibleForRefund()) {
            System.err.println("Cash Refund Error: Payment is not in successful state.");
            return false;
        }
        double maxRefundable = getMaxRefundableAmount();
        if (refundAmount <= 0 || refundAmount > maxRefundable) {
            System.err.println("Cash Refund Error: Requested refund ₹" + refundAmount + " exceeds limit ₹" + maxRefundable);
            return false;
        }

        addRefundedAmount(refundAmount);
        if (getRefundedAmount() >= getAmount()) {
            setStatus(PaymentStatus.REFUNDED);
        } else {
            setStatus(PaymentStatus.PARTIALLY_REFUNDED);
        }
        System.out.printf("Cash Refund SUCCESSful: Dispensed cash ₹%.2f at POS terminal register.%n", refundAmount);
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
        return String.format("Cash Register POS [Tendered: ₹%.2f, Change: ₹%.2f]", cashTendered, changeGiven);
    }

    public double getCashTendered() {
        return cashTendered;
    }

    public double getChangeGiven() {
        return changeGiven;
    }
}
