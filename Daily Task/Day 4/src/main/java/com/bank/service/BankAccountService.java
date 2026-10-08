package com.bank.service;

import com.bank.model.BankAccount;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * BankAccountService handles account management operations, transfers,
 * and high-level banking business logic.
 */
public class BankAccountService {

    private final List<BankAccount> accounts;

    public BankAccountService() {
        this.accounts = new ArrayList<>();
    }

    /**
     * Creates a new BankAccount using Constructor 1 (default balance ₹1000, type SAVINGS).
     */
    public BankAccount createAccount(String holderName) {
        BankAccount account = new BankAccount(holderName);
        accounts.add(account);
        return account;
    }

    /**
     * Creates a new BankAccount using Constructor 2 (custom balance, default type SAVINGS).
     */
    public BankAccount createAccount(String holderName, double initialBalance) {
        BankAccount account = new BankAccount(holderName, initialBalance);
        accounts.add(account);
        return account;
    }

    /**
     * Creates a new BankAccount using Constructor 3 (custom balance, custom account type).
     */
    public BankAccount createAccount(String holderName, double initialBalance, String accountType) {
        BankAccount account = new BankAccount(holderName, initialBalance, accountType);
        accounts.add(account);
        return account;
    }

    /**
     * Retrieves an account by its unique account number.
     */
    public BankAccount getAccount(String accountNumber) throws AccountNotFoundException {
        return accounts.stream()
                .filter(acc -> acc.getAccountNumber().equalsIgnoreCase(accountNumber))
                .findFirst()
                .orElseThrow(() -> new AccountNotFoundException("Account not found for number: " + accountNumber));
    }

    /**
     * Performs a deposit transaction on a targeted account.
     */
    public double deposit(String accountNumber, double amount) throws AccountNotFoundException {
        BankAccount account = getAccount(accountNumber);
        return account.deposit(amount);
    }

    /**
     * Performs a withdrawal transaction on a targeted account.
     */
    public double withdraw(String accountNumber, double amount) throws AccountNotFoundException, InsufficientBalanceException {
        BankAccount account = getAccount(accountNumber);
        try {
            return account.withdraw(amount);
        } catch (IllegalArgumentException e) {
            throw new InsufficientBalanceException(e.getMessage());
        }
    }

    /**
     * Executes a funds transfer between two accounts using atomic steps.
     */
    public boolean transfer(String sourceAccNumber, String destAccNumber, double amount)
            throws AccountNotFoundException, InsufficientBalanceException {
        BankAccount source = getAccount(sourceAccNumber);
        BankAccount destination = getAccount(destAccNumber);

        if (source.equals(destination)) {
            throw new IllegalArgumentException("Cannot transfer money to the same account.");
        }

        // Perform withdrawal from source and deposit into destination
        source.withdraw(amount);
        destination.deposit(amount);
        return true;
    }

    /**
     * Returns an unmodifiable view of all registered bank accounts.
     */
    public List<BankAccount> getAllAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    /**
     * Returns total accounts created across all instances using static member.
     */
    public int getTotalAccountCount() {
        return BankAccount.getTotalAccounts();
    }
}
