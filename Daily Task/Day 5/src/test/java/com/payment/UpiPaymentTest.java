package com.payment;

import com.payment.model.PaymentStatus;
import com.payment.model.UpiPayment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpiPaymentTest {

    private UpiPayment upiPayment;

    @BeforeEach
    void setUp() {
        upiPayment = new UpiPayment("TXN-UPI-888", 850.00, "user@okicici", "PhonePe");
    }

    @Test
    @DisplayName("Invalid UPI ID without '@' throws IllegalArgumentException")
    void testInvalidUpiId() {
        assertThrows(IllegalArgumentException.class, () ->
            new UpiPayment("TXN-UPI-000", 100.00, "usericici", "PhonePe")
        );
    }

    @Test
    @DisplayName("UPI payment assigns transaction reference number on success")
    void testProcessPayment() {
        assertTrue(upiPayment.processPayment());
        assertNotNull(upiPayment.getTransactionRefNo());
        assertTrue(upiPayment.getTransactionRefNo().startsWith("UPI-REF-"));
        assertEquals(PaymentStatus.SUCCESS, upiPayment.getStatus());
    }

    @Test
    @DisplayName("Overloaded pay with discount calculates final amount")
    void testOverloadedPayDiscount() {
        // 10% discount on 1000 = 900
        assertTrue(upiPayment.pay(1000.00, 10.0));
        assertEquals(900.00, upiPayment.getAmount());
        assertEquals(PaymentStatus.SUCCESS, upiPayment.getStatus());
    }

    @Test
    @DisplayName("UPI refund validation")
    void testUpiRefund() {
        upiPayment.processPayment();
        assertTrue(upiPayment.processRefund(425.00));
        assertEquals(425.00, upiPayment.getRefundedAmount());
        assertEquals(PaymentStatus.PARTIALLY_REFUNDED, upiPayment.getStatus());
    }
}
