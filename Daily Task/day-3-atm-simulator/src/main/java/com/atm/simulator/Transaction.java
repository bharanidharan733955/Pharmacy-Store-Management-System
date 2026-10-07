package com.atm.simulator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private final String type;
    private final double amount;
    private final double balanceAfterTransaction;
    private final String timestamp;

    public Transaction(String type, double amount, double balanceAfterTransaction) {
        this.type = type;
        this.amount = amount;
        this.balanceAfterTransaction = balanceAfterTransaction;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfterTransaction() {
        return balanceAfterTransaction;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        String sign = type.equalsIgnoreCase("DEPOSIT") ? "+" : "-";
        return String.format("%-12s %s₹%.2f  (Balance: ₹%.2f)", type, sign, amount, balanceAfterTransaction);
    }
}
