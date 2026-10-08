package com.bank.service;

/**
 * Custom Exception thrown when an account has insufficient funds for a transaction.
 */
public class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
