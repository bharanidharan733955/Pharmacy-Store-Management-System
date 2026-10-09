package com.payment;

import com.payment.model.CashPayment;
import com.payment.model.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CashPaymentTest {

    private CashPayment cashPayment;

    @BeforeEach
    void setUp() {
        cashPayment = new CashPayment("TXN-CASH-777", 450.00, 500.00);
    }

    @Test
    @DisplayName("Cash payment calculates exact change")
    void testCashPaymentSuccess() {
        assertTrue(cashPayment.processPayment());
        assertEquals(50.00, cashPayment.getChangeGiven());
        assertEquals(PaymentStatus.SUCCESS, cashPayment.getStatus());
    }

    @Test
    @DisplayName("Insufficient cash tendered results in FAILED status")
    void testInsufficientCash() {
        CashPayment insufficient = new CashPayment("TXN-CASH-778", 500.00, 300.00);
        assertFalse(insufficient.processPayment());
        assertEquals(PaymentStatus.FAILED, insufficient.getStatus());
    }

    @Test
    @DisplayName("Cash payment refund calculation")
    void testCashRefund() {
        cashPayment.processPayment();
        assertTrue(cashPayment.processRefund(450.00));
        assertEquals(PaymentStatus.REFUNDED, cashPayment.getStatus());
    }
}
