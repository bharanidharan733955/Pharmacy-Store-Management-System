package com.payment.app;

import com.payment.model.*;
import com.payment.service.Refundable;

import java.util.ArrayList;
import java.util.List;

/**
 * Main application demonstrating OOP Payment Hierarchy, Overloaded pay() methods,
 * Interface-based Refunds, Runtime Polymorphism, and Receipts.
 */
public class PaymentApp {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   DAY 5 - PHARMACY STORE PAYMENT HIERARCHY APP  ");
        System.out.println("=================================================\n");

        List<Payment> payments = new ArrayList<>();

        // 1. Create Payment instances (Card, UPI, Cash)
        CardPayment cardPayment = new CardPayment("TXN-CARD-101", 1450.00, "4532789012345678", "Dr. Anita Rao", "CREDIT", "12/28");
        UpiPayment upiPayment = new UpiPayment("TXN-UPI-202", 620.50, "pharmacy.billing@okaxis", "GooglePay");
        CashPayment cashPayment = new CashPayment("TXN-CASH-303", 350.00, 500.00);

        payments.add(cardPayment);
        payments.add(upiPayment);
        payments.add(cashPayment);

        // 2. Demonstrate Overloaded pay() Methods
        System.out.println("--- DEMONSTRATING METHOD OVERLOADING (pay()) ---");
        System.out.println("[Overload 1 - Default pay()]:");
        cardPayment.pay();

        System.out.println("\n[Overload 2 - pay(amount, pin)]:");
        cardPayment.pay(1450.00, 4321);

        System.out.println("\n[Overload 3 - pay(amount, discountPercentage)]:");
        upiPayment.pay(700.00, 10.0); // 10% discount on UPI

        System.out.println("\n[Overload 4 - pay(amount, mandateId, isAutoPay) - Rebased Feature]:");
        upiPayment.pay(850.00, "MANDATE-AUTO-9988", true);

        System.out.println("\n[Overload 5 - pay(amount, promoCode, promoDiscount)]:");
        cashPayment.pay(400.00, "HEALTH20", 50.00);

        System.out.println("\n-------------------------------------------------");
        System.out.println("--- RUNTIME POLYMORPHISM VIA ABSTRACT CLASS ---");
        System.out.println("-------------------------------------------------");
        for (Payment p : payments) {
            System.out.println("Processing: " + p.getPaymentChannelDetails());
            boolean success = p.processPayment();
            System.out.println("Execution Result: " + (success ? "SUCCESS" : "FAILED"));
            System.out.println(p.generateReceipt());
            System.out.println();
        }

        System.out.println("-------------------------------------------------");
        System.out.println("--- INTERFACE REFUND PROCESSING (Refundable) ---");
        System.out.println("-------------------------------------------------");
        for (Payment p : payments) {
            if (p instanceof Refundable refundable) {
                System.out.println("Checking refund capability for " + p.getTransactionId() + "...");
                if (refundable.isEligibleForRefund()) {
                    double refundAmount = p.getAmount() * 0.5; // 50% partial refund
                    double fee = refundable.calculateRefundFee(refundAmount, 2.0); // 2% processing fee
                    System.out.printf("Initiating refund of ₹%.2f (Processing fee: ₹%.2f)%n", refundAmount, fee);
                    refundable.processRefund(refundAmount);
                    System.out.println(p.generateReceipt());
                } else {
                    System.out.println("Payment " + p.getTransactionId() + " is not eligible for refund.");
                }
                System.out.println();
            }
        }

        System.out.println("=================================================");
        System.out.println("    PAYMENT PROCESSING DEMONSTRATION COMPLETE    ");
        System.out.println("=================================================");
    }
}
