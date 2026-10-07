package com.atm.simulator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ATMTest {

    private ATM atm;

    @BeforeEach
    public void setUp() {
        atm = new ATM(15000.00);
    }

    @Test
    @DisplayName("Test Initial Balance")
    public void testInitialBalance() {
        assertEquals(15000.00, atm.getBalance(), 0.001);
    }

    @Test
    @DisplayName("Test Successful Deposit")
    public void testSuccessfulDeposit() {
        boolean success = atm.deposit(5000.00);
        assertTrue(success);
        assertEquals(20000.00, atm.getBalance(), 0.001);
    }

    @Test
    @DisplayName("Test Invalid Deposit Amount")
    public void testInvalidDepositAmount() {
        boolean success = atm.deposit(-500.00);
        assertFalse(success);
        assertEquals(15000.00, atm.getBalance(), 0.001);
    }

    @Test
    @DisplayName("Test Successful Withdrawal")
    public void testSuccessfulWithdrawal() {
        boolean success = atm.withdraw(3000.00);
        assertTrue(success);
        assertEquals(12000.00, atm.getBalance(), 0.001);
    }

    @Test
    @DisplayName("Test Insufficient Balance Withdrawal")
    public void testInsufficientBalanceWithdrawal() {
        boolean success = atm.withdraw(50000.00);
        assertFalse(success);
        assertEquals(15000.00, atm.getBalance(), 0.001);
    }

    @Test
    @DisplayName("Test Transaction History Recording")
    public void testTransactionHistoryRecording() {
        atm.deposit(5000.00);
        atm.withdraw(2000.00);
        assertEquals(2, atm.getTransactions().size());
    }
}
