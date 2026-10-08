package com.bank.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BankAccount Core Domain Tests")
class BankAccountTest {

    @BeforeEach
    void setUp() {
        BankAccount.resetTotalAccounts();
    }

    @Test
    @DisplayName("Test Constructor 1 (Chained) - Default Balance and Account Type")
    void testConstructor1Chaining() {
        BankAccount acc = new BankAccount("John Doe");

        assertEquals("ACC-0001", acc.getAccountNumber());
        assertEquals("John Doe", acc.getAccountHolderName());
        assertEquals(1000.00, acc.getBalance(), 0.001);
        assertEquals("SAVINGS", acc.getAccountType());
        assertEquals(1, BankAccount.getTotalAccounts());
    }

    @Test
    @DisplayName("Test Constructor 2 (Chained) - Custom Balance and Default Account Type")
    void testConstructor2Chaining() {
        BankAccount acc = new BankAccount("Jane Doe", 2500.50);

        assertEquals("ACC-0001", acc.getAccountNumber());
        assertEquals("Jane Doe", acc.getAccountHolderName());
        assertEquals(2500.50, acc.getBalance(), 0.001);
        assertEquals("SAVINGS", acc.getAccountType());
        assertEquals(1, BankAccount.getTotalAccounts());
    }

    @Test
    @DisplayName("Test Constructor 3 - Full Parameter Initialization")
    void testConstructor3Full() {
        BankAccount acc = new BankAccount("Robert Paulson", 5000.00, "CURRENT");

        assertEquals("ACC-0001", acc.getAccountNumber());
        assertEquals("Robert Paulson", acc.getAccountHolderName());
        assertEquals(5000.00, acc.getBalance(), 0.001);
        assertEquals("CURRENT", acc.getAccountType());
        assertEquals(1, BankAccount.getTotalAccounts());
    }

    @Test
    @DisplayName("Test Static Counter Increments Correctly Across Instantiations")
    void testStaticCounterIncrement() {
        new BankAccount("User 1");
        new BankAccount("User 2");
        new BankAccount("User 3");

        assertEquals(3, BankAccount.getTotalAccounts());
    }

    @Test
    @DisplayName("Test Validated Deposit - Success and Failure Cases")
    void testValidatedDeposit() {
        BankAccount acc = new BankAccount("Tester", 1000.00);

        // Valid deposit
        double updatedBalance = acc.deposit(500.00);
        assertEquals(1500.00, updatedBalance, 0.001);
        assertEquals(1500.00, acc.getBalance(), 0.001);

        // Invalid zero/negative deposits
        assertThrows(IllegalArgumentException.class, () -> acc.deposit(0));
        assertThrows(IllegalArgumentException.class, () -> acc.deposit(-100));
    }

    @Test
    @DisplayName("Test Validated Withdraw - Success and Insufficient Funds Validation")
    void testValidatedWithdraw() {
        BankAccount acc = new BankAccount("Tester", 1000.00);

        // Valid withdrawal
        double updatedBalance = acc.withdraw(300.00);
        assertEquals(700.00, updatedBalance, 0.001);

        // Withdrawal exceeding balance
        assertThrows(IllegalArgumentException.class, () -> acc.withdraw(800.00));

        // Invalid zero/negative withdrawal
        assertThrows(IllegalArgumentException.class, () -> acc.withdraw(0));
        assertThrows(IllegalArgumentException.class, () -> acc.withdraw(-50.00));
    }

    @Test
    @DisplayName("Test Equals and HashCode Contract")
    void testEqualsAndHashCode() {
        BankAccount acc1 = new BankAccount("Alice", 1000.00);
        BankAccount acc2 = new BankAccount("Bob", 2000.00);

        // Different account numbers -> Not equal
        assertNotEquals(acc1, acc2);
        assertNotEquals(acc1.hashCode(), acc2.hashCode());

        // Same reference -> Equal
        assertEquals(acc1, acc1);
        assertEquals(acc1.hashCode(), acc1.hashCode());

        // Null and non-BankAccount object checks
        assertNotEquals(null, acc1);
        assertNotEquals("Not a BankAccount", acc1);
    }
}
