package com.payment;

import com.payment.model.CardPayment;
import com.payment.model.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardPaymentTest {

    private CardPayment cardPayment;

    @BeforeEach
    void setUp() {
        cardPayment = new CardPayment("TXN-CARD-999", 2000.00, "1234567890123456", "John Doe", "CREDIT", "12/26");
    }

    @Test
    @DisplayName("Card masking should obscure all but the last 4 digits")
    void testCardMasking() {
        assertEquals("****-****-****-3456", cardPayment.getCardNumber());
    }

    @Test
    @DisplayName("Invalid card number should throw IllegalArgumentException")
    void testInvalidCardNumber() {
        assertThrows(IllegalArgumentException.class, () ->
            new CardPayment("TXN-CARD-000", 500.00, "123", "John Doe", "DEBIT", "10/25")
        );
    }

    @Test
    @DisplayName("Successful card payment sets status to SUCCESS")
    void testSuccessfulPayment() {
        assertTrue(cardPayment.processPayment());
        assertEquals(PaymentStatus.SUCCESS, cardPayment.getStatus());
    }

    @Test
    @DisplayName("Overloaded pay with invalid PIN should fail")
    void testOverloadedPayInvalidPin() {
        assertFalse(cardPayment.pay(2000.00, 12)); // Invalid 2 digit pin
        assertEquals(PaymentStatus.FAILED, cardPayment.getStatus());
    }

    @Test
    @DisplayName("Partial and full refund processing on Card")
    void testRefundProcessing() {
        cardPayment.processPayment();
        assertTrue(cardPayment.isEligibleForRefund());

        // Partial refund
        assertTrue(cardPayment.processRefund(1000.00));
        assertEquals(PaymentStatus.PARTIALLY_REFUNDED, cardPayment.getStatus());
        assertEquals(1000.00, cardPayment.getMaxRefundableAmount());

        // Full refund remaining
        assertTrue(cardPayment.processRefund(1000.00));
        assertEquals(PaymentStatus.REFUNDED, cardPayment.getStatus());
        assertEquals(0.00, cardPayment.getMaxRefundableAmount());

        // Exceeding refund should fail
        assertFalse(cardPayment.processRefund(100.00));
    }
}
