package com.bank.model;

import java.util.Objects;

/**
 * BankAccount represents a customer bank account.
 * Demonstrates encapsulation with private fields, a static account counter,
 * 3-level constructor chaining using this(...), input validation for deposit/withdraw,
 * and overridden equals() / hashCode() methods.
 */
public class BankAccount {

    // Static counter tracking total BankAccount instances created
    private static int totalAccounts = 0;

    // Private fields (Encapsulation)
    private final String accountNumber;
    private String accountHolderName;
    private double balance;
    private String accountType;

    /**
     * Constructor 1 (Single Argument): Chained to Constructor 2.
     * Passes a default initial balance of ₹1,000.00.
     *
     * @param accountHolderName Name of the account holder
     */
    public BankAccount(String accountHolderName) {
        this(accountHolderName, 1000.00);
    }

    /**
     * Constructor 2 (Two Arguments): Chained to Constructor 3.
     * Passes a default account type of "SAVINGS".
     *
     * @param accountHolderName Name of the account holder
     * @param initialBalance    Initial account balance
     */
    public BankAccount(String accountHolderName, double initialBalance) {
        this(accountHolderName, initialBalance, "SAVINGS");
    }

    /**
     * Constructor 3 (Three Arguments): Primary Constructor.
     * Validates input parameters, increments static counter, and generates account number.
     *
     * @param accountHolderName Name of the account holder
     * @param initialBalance    Initial account balance (must be >= 0)
     * @param accountType       Account type (e.g., SAVINGS, CURRENT)
     */
    public BankAccount(String accountHolderName, double initialBalance, String accountType) {
        if (accountHolderName == null || accountHolderName.trim().isEmpty()) {
            throw new IllegalArgumentException("Account holder name cannot be null or empty.");
        }
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative.");
        }
        if (accountType == null || accountType.trim().isEmpty()) {
            throw new IllegalArgumentException("Account type cannot be null or empty.");
        }

        totalAccounts++;
        this.accountNumber = "ACC-" + String.format("%04d", totalAccounts);
        this.accountHolderName = accountHolderName.trim();
        this.balance = initialBalance;
        this.accountType = accountType.trim().toUpperCase();
    }

    /**
     * Deposits a specified amount into the account after validation.
     *
     * @param amount Amount to deposit (must be > 0)
     * @return Updated balance after deposit
     */
    public double deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be strictly positive.");
        }
        this.balance += amount;
        return this.balance;
    }

    /**
     * Withdraws a specified amount from the account after validation.
     * 
     * NOTE: Contains a PLANTED BUG for debugging demonstration purposes!
     * Planted Bug: Uses '+' instead of '-' (adds amount to balance during withdrawal).
     * Fix for Hot Code Replace: Change `this.balance += amount;` to `this.balance -= amount;`.
     *
     * @param amount Amount to withdraw (must be > 0 and <= balance)
     * @return Updated balance after withdrawal
     */
    public double withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be strictly positive.");
        }
        if (amount > this.balance) {
            throw new IllegalArgumentException("Insufficient funds. Current balance: ₹" + this.balance);
        }

        // =========================================================================
        // PLANTED BUG: The balance is erroneously increased instead of decreased!
        // Debugging Task: Set a conditional breakpoint when `amount > 500`
        // Watch Expression: `this.balance`
        // Fix with Hot Code Replace: Change `+=` to `-=`
        // =========================================================================
        this.balance -= amount; // <-- CORRECTED CODE (For bug demo, change to += during debug session or toggle)
        return this.balance;
    }

    /**
     * Helper method to execute withdraw with planted bug for Debugging Lab session.
     *
     * @param amount Amount to withdraw
     * @return Updated balance with planted bug applied
     */
    public double withdrawWithPlantedBug(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be strictly positive.");
        }
        if (amount > this.balance) {
            throw new IllegalArgumentException("Insufficient funds. Current balance: ₹" + this.balance);
        }
        // Planted Bug: Adding instead of subtracting
        this.balance += amount;
        return this.balance;
    }

    // Static Getter for total account counter
    public static int getTotalAccounts() {
        return totalAccounts;
    }

    // Static method to reset counter (useful for unit tests)
    public static void resetTotalAccounts() {
        totalAccounts = 0;
    }

    // Getters and Setters
    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        if (accountHolderName == null || accountHolderName.trim().isEmpty()) {
            throw new IllegalArgumentException("Account holder name cannot be null or empty.");
        }
        this.accountHolderName = accountHolderName.trim();
    }

    public double getBalance() {
        return balance;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        if (accountType == null || accountType.trim().isEmpty()) {
            throw new IllegalArgumentException("Account type cannot be null or empty.");
        }
        this.accountType = accountType.trim().toUpperCase();
    }

    /**
     * Equals method: Accounts are considered equal if they have the same account number.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankAccount that = (BankAccount) o;
        return Objects.equals(accountNumber, that.accountNumber);
    }

    /**
     * HashCode method: Generates hash code based on account number.
     */
    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    @Override
    public String toString() {
        return String.format("BankAccount [Account No: %s | Holder: %s | Type: %s | Balance: ₹%.2f]",
                accountNumber, accountHolderName, accountType, balance);
    }
}
