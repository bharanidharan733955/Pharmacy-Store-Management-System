package com.atm.simulator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class ATM {
    private static final int DEMO_PIN = 1234;
    private static final int MAX_PIN_ATTEMPTS = 3;

    private double balance;
    private final List<Transaction> transactions;
    private String environment = "development";
    private double maxWithdrawalLimit = 50000.0;

    public ATM() {
        this(15000.00);
    }

    public ATM(double initialBalance) {
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                Properties props = new Properties();
                props.load(input);
                this.environment = props.getProperty("environment", "development");
                this.maxWithdrawalLimit = Double.parseDouble(props.getProperty("max.withdrawal.limit", "50000.0"));
            }
        } catch (Exception ignored) {
        }
    }

    public double getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public String getEnvironment() {
        return environment;
    }

    public double getMaxWithdrawalLimit() {
        return maxWithdrawalLimit;
    }

    public boolean authenticate(Scanner scanner) {
        int attempts = 0;
        boolean authenticated = false;

        while (attempts < MAX_PIN_ATTEMPTS) {
            System.out.print("Enter PIN: ");
            String input = scanner.nextLine().trim();

            int enteredPin;
            try {
                enteredPin = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                attempts++;
                int remaining = MAX_PIN_ATTEMPTS - attempts;
                if (remaining > 0) {
                    System.out.println("Invalid PIN format. Attempts remaining: " + remaining + "\n");
                } else {
                    System.out.println("Account locked. Too many failed attempts.");
                    break; // break when max attempts reached
                }
                continue;
            }

            if (enteredPin == DEMO_PIN) {
                authenticated = true;
                System.out.println("Access granted.");
                break; // break when correct PIN entered
            } else {
                attempts++;
                int remaining = MAX_PIN_ATTEMPTS - attempts;
                if (remaining > 0) {
                    System.out.println("Invalid PIN. Attempts remaining: " + remaining + "\n");
                } else {
                    System.out.println("Account locked. Too many failed attempts.");
                    break; // break when max attempts reached
                }
            }
        }
        return authenticated;
    }

    public boolean deposit(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid deposit amount. Amount must be greater than 0.");
            return false;
        }
        balance += amount;
        transactions.add(new Transaction("Deposit", amount, balance));
        System.out.println("\nDeposit successful.");
        System.out.printf("Current Balance: ₹%.2f\n", balance);
        return true;
    }

    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid withdrawal amount. Amount must be greater than 0.");
            return false;
        }
        if (amount > balance) {
            System.out.println("Insufficient balance.");
            return false;
        }
        if (amount > maxWithdrawalLimit) {
            System.out.printf("Withdrawal limit exceeded. Maximum limit per transaction is ₹%.2f (%s mode).\n",
                    maxWithdrawalLimit, environment);
            return false;
        }
        balance -= amount;
        transactions.add(new Transaction("Withdrawal", amount, balance));
        System.out.println("\nWithdrawal successful.");
        System.out.printf("Current Balance: ₹%.2f\n", balance);
        return true;
    }

    public void printMiniStatement() {
        System.out.println("\n========================================");
        System.out.println("             MINI STATEMENT");
        System.out.println("========================================");
        if (transactions.isEmpty()) {
            System.out.println("No transactions recorded.");
        } else {
            // Enhanced-for loop requirement
            for (Transaction transaction : transactions) {
                System.out.println(transaction);
            }
        }
        System.out.println("========================================");
        System.out.printf("Current Balance: ₹%.2f\n", balance);
    }

    public void startSession(Scanner scanner) {
        if (!authenticate(scanner)) {
            return;
        }

        int choice = 0;
        do {
            System.out.println("\n========================================");
            System.out.println("              ATM SIMULATOR");
            System.out.println("========================================");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Mini Statement");
            System.out.println("5. Exit");
            System.out.println("========================================");
            System.out.print("Enter your choice: ");

            String input = scanner.nextLine().trim();

            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number (1-5).");
                continue; // Meaningful use of continue
            }

            if (choice < 1 || choice > 5) {
                System.out.println("Invalid choice. Please select an option between 1 and 5.");
                continue; // Meaningful use of continue
            }

            switch (choice) {
                case 1:
                    System.out.printf("\nCurrent Balance: ₹%.2f\n", balance);
                    break;

                case 2:
                    System.out.print("Enter deposit amount: ");
                    String depInput = scanner.nextLine().trim();
                    try {
                        double depAmount = Double.parseDouble(depInput);
                        if (depAmount <= 0) {
                            System.out.println("Invalid deposit amount. Amount must be greater than 0.");
                            continue; // Meaningful use of continue
                        }
                        deposit(depAmount);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid amount entered. Please enter a valid numeric value.");
                        continue; // Meaningful use of continue
                    }
                    break;

                case 3:
                    System.out.print("Enter withdrawal amount: ");
                    String withInput = scanner.nextLine().trim();
                    try {
                        double withAmount = Double.parseDouble(withInput);
                        if (withAmount <= 0) {
                            System.out.println("Invalid withdrawal amount. Amount must be greater than 0.");
                            continue; // Meaningful use of continue
                        }
                        withdraw(withAmount);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid amount entered. Please enter a valid numeric value.");
                        continue; // Meaningful use of continue
                    }
                    break;

                case 4:
                    printMiniStatement();
                    break;

                case 5:
                    System.out.println("\nThank you for using the ATM.");
                    System.out.println("Please collect your card.");
                    break;

                default:
                    System.out.println("Invalid choice.");
                    continue; // Meaningful use of continue
            }

        } while (choice != 5);
    }
}
