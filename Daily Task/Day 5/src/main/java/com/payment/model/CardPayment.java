package com.payment.model;

import com.payment.service.Refundable;

/**
 * Concrete CardPayment class extending Payment and implementing Refundable interface.
 */
public class CardPayment extends Payment implements Refundable {
    private final String cardNumber;
    private final String cardHolderName;
    private final String cardType; // e.g. "CREDIT" or "DEBIT"
    private final String expiryDate;

    public CardPayment(String transactionId, double amount, String cardNumber, String cardHolderName, String cardType, String expiryDate) {
        super(transactionId, amount);
        if (cardNumber == null || cardNumber.replaceAll("\\s+", "").length() < 12) {
            throw new IllegalArgumentException("Invalid card number format.");
        }
        if (cardHolderName == null || cardHolderName.trim().isEmpty()) {
            throw new IllegalArgumentException("Cardholder name cannot be empty.");
        }
        this.cardNumber = maskCardNumber(cardNumber);
        this.cardHolderName = cardHolderName;
        this.cardType = cardType != null ? cardType.toUpperCase() : "DEBIT";
        this.expiryDate = expiryDate;
    }

    private String maskCardNumber(String rawCardNumber) {
        String clean = rawCardNumber.replaceAll("\\s+", "");
        if (clean.length() <= 4) return clean;
        String lastFour = clean.substring(clean.length() - 4);
        return "****-****-****-" + lastFour;
    }

    @Override
    public boolean processPayment() {
        System.out.println("Validating Card Details for " + cardHolderName + " (" + cardType + ")...");
        // Student 1 Feature: Calculate 1.5% processing fee
        double convenienceFee = getAmount() * 0.015;
        double totalCharged = getAmount() + convenienceFee;
        if (getAmount() <= 0) {
            setStatus(PaymentStatus.FAILED);
            return false;
        }
        setStatus(PaymentStatus.SUCCESS);
        System.out.printf("Card Payment SUCCESSful. Base: ₹%.2f, Fee: ₹%.2f, Total Charged: ₹%.2f to Card: %s%n",
                getAmount(), convenienceFee, totalCharged, cardNumber);
        return true;
    }

    /**
     * Overloaded pay method specifically requiring standard Security PIN authorization.
     */
    public boolean pay(double amount, int pin) {
        if (String.valueOf(pin).length() != 4) {
            System.err.println("Card Authorization Failed: Invalid PIN length.");
            setStatus(PaymentStatus.FAILED);
            return false;
        }
        System.out.println("PIN verification successful for Card: " + cardNumber);
        return pay(amount);
    }

    @Override
    public boolean processRefund(double refundAmount) {
        if (!isEligibleForRefund()) {
            System.err.println("Refund Error: Payment status is " + getStatus() + ". Card refund not allowed.");
            return false;
        }
        double remainingRefundable = getMaxRefundableAmount();
        if (refundAmount <= 0 || refundAmount > remainingRefundable) {
            System.err.println("Refund Error: Requested ₹" + refundAmount + " exceeds max refundable amount ₹" + remainingRefundable);
            return false;
        }

        addRefundedAmount(refundAmount);
        if (getRefundedAmount() >= getAmount()) {
            setStatus(PaymentStatus.REFUNDED);
        } else {
            setStatus(PaymentStatus.PARTIALLY_REFUNDED);
        }

        System.out.printf("Refund SUCCESSful: ₹%.2f credited back to Card %s%n", refundAmount, cardNumber);
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
        return "Card (" + cardType + ") [" + cardNumber + "]";
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCardType() {
        return cardType;
    }

    public String getExpiryDate() {
        return expiryDate;
    }
}
