package com.bank.service;

import com.bank.model.BankAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BankAccountService Business Logic Tests")
class BankAccountServiceTest {

    private BankAccountService service;

    @BeforeEach
    void setUp() {
        BankAccount.resetTotalAccounts();
        service = new BankAccountService();
    }

    @Test
    @DisplayName("Test Account Creation and Retrieval")
    void testCreateAndGetAccount() throws AccountNotFoundException {
        BankAccount acc = service.createAccount("David Miller", 2000.00);

        assertNotNull(acc);
        assertEquals("ACC-0001", acc.getAccountNumber());

        BankAccount retrieved = service.getAccount("ACC-0001");
        assertEquals(acc, retrieved);
    }

    @Test
    @DisplayName("Test Account Not Found Exception")
    void testAccountNotFound() {
        assertThrows(AccountNotFoundException.class, () -> service.getAccount("ACC-9999"));
    }

    @Test
    @DisplayName("Test Service Deposit and Withdraw Operations")
    void testServiceDepositAndWithdraw() throws AccountNotFoundException, InsufficientBalanceException {
        BankAccount acc = service.createAccount("Emma Watson", 3000.00);

        double balanceAfterDeposit = service.deposit(acc.getAccountNumber(), 1000.00);
        assertEquals(4000.00, balanceAfterDeposit, 0.001);

        double balanceAfterWithdraw = service.withdraw(acc.getAccountNumber(), 1500.00);
        assertEquals(2500.00, balanceAfterWithdraw, 0.001);
    }

    @Test
    @DisplayName("Test Successful Funds Transfer Between Accounts")
    void testTransferSuccess() throws AccountNotFoundException, InsufficientBalanceException {
        BankAccount acc1 = service.createAccount("Sender", 5000.00);
        BankAccount acc2 = service.createAccount("Receiver", 1000.00);

        boolean success = service.transfer(acc1.getAccountNumber(), acc2.getAccountNumber(), 2000.00);

        assertTrue(success);
        assertEquals(3000.00, acc1.getBalance(), 0.001);
        assertEquals(3000.00, acc2.getBalance(), 0.001);
    }

    @Test
    @DisplayName("Test Transfer Fails On Same Account Or Insufficient Balance")
    void testTransferFailures() {
        BankAccount acc1 = service.createAccount("Sender", 500.00);
        BankAccount acc2 = service.createAccount("Receiver", 1000.00);

        // Same account transfer
        assertThrows(IllegalArgumentException.class, () -> 
            service.transfer(acc1.getAccountNumber(), acc1.getAccountNumber(), 100.00)
        );

        // Transfer amount exceeds balance
        assertThrows(InsufficientBalanceException.class, () -> 
            service.transfer(acc1.getAccountNumber(), acc2.getAccountNumber(), 1000.00)
        );
    }
}
