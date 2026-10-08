package com.bank.service;

/**
 * Custom Exception thrown when an account cannot be located by its account number.
 */
public class AccountNotFoundException extends Exception {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
