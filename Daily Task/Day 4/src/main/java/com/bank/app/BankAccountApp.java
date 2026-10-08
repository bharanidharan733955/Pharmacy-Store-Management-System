package com.bank.app;

import com.bank.model.BankAccount;
import com.bank.service.AccountNotFoundException;
import com.bank.service.BankAccountService;
import com.bank.service.InsufficientBalanceException;

import java.util.Scanner;

/**
 * Console application demonstrating:
 * 1. 3 Chained Constructors & Default Parameter Handling
 * 2. Static Counter tracking across account instantiations
 * 3. Object Equality & HashCode comparison
 * 4. Planted Bug Simulation & Hot Code Replace Debug Lab
 * 5. Interactive Banking Console
 */
public class BankAccountApp {

    private static final BankAccountService service = new BankAccountService();

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("            DAY 4: BANK ACCOUNT MANAGEMENT & DEBUG LAB               ");
        System.out.println("======================================================================");

        runDemonstrations();
        runInteractiveMenu();
    }

    /**
     * Executes automated feature demonstrations required for Day 4.
     */
    private static void runDemonstrations() {
        System.out.println("\n--- 1. DEMONSTRATING 3 CHAINED CONSTRUCTORS & STATIC COUNTER ---");
        // Constructor 1: Only name (Defaults to balance ₹1000.00, type SAVINGS)
        BankAccount acc1 = service.createAccount("Alice Smith");
        System.out.println("Created Acc 1 [Constructor 1]: " + acc1);

        // Constructor 2: Name + Custom Balance (Defaults to type SAVINGS)
        BankAccount acc2 = service.createAccount("Bob Jones", 5000.00);
        System.out.println("Created Acc 2 [Constructor 2]: " + acc2);

        // Constructor 3: Name + Custom Balance + Custom Type
        BankAccount acc3 = service.createAccount("Charlie Brown", 15000.00, "CURRENT");
        System.out.println("Created Acc 3 [Constructor 3]: " + acc3);

        System.out.println("\n[Static Counter Check] Total Bank Accounts Created: " + BankAccount.getTotalAccounts());

        System.out.println("\n--- 2. DEMONSTRATING EQUALS() AND HASHCODE() ---");
        // Creating another object reference with same details
        BankAccount acc1Duplicate = new BankAccount("Alice Smith", 1000.00, "SAVINGS");
        System.out.println("Acc 1 Details: " + acc1);
        System.out.println("Acc 1 Copy Details (New instance, new acc number): " + acc1Duplicate);
        System.out.println("acc1.equals(acc1Duplicate)? " + acc1.equals(acc1Duplicate) + " (Different Account Numbers)");

        // Self equality
        System.out.println("acc1.equals(acc1)? " + acc1.equals(acc1) + " (Same Object Identity)");
        System.out.println("acc1.hashCode(): " + acc1.hashCode());

        System.out.println("\n--- 3. DEBUG LAB: PLANTED BUG IN withdraw() SIMULATION ---");
        simulatePlantedBugDemo(acc2);
    }

    /**
     * Simulates the planted bug behavior for educational / debugging lab output.
     */
    private static void simulatePlantedBugDemo(BankAccount account) {
        double startingBalance = account.getBalance();
        double withdrawAmount = 1500.00;

        System.out.println("Starting Balance: ₹" + startingBalance);
        System.out.println("Attempting Withdrawal of: ₹" + withdrawAmount);

        // Demonstrate standard corrected method vs bug method
        System.out.println("\n[Correct Behavior Execution]");
        account.withdraw(withdrawAmount);
        System.out.println("Balance after correct withdrawal: ₹" + account.getBalance());

        System.out.println("\n[Planted Bug Execution Simulation]");
        System.out.println("Executing withdrawal with planted bug (balance += amount instead of -=):");
        double balanceWithBug = account.withdrawWithPlantedBug(withdrawAmount);
        System.out.println("BUG ALERT! Balance increased to: ₹" + balanceWithBug + " (Expected reduction)");

        // Undo bug addition for interactive menu consistency
        account.withdraw(withdrawAmount);
        account.withdraw(withdrawAmount); // Realign balance
        System.out.println("Re-aligned Balance: ₹" + account.getBalance());

        System.out.println("\n[Debug Tip]: To practice live Hot Code Replace (HCR):");
        System.out.println(" 1. Open BankAccount.java and navigate to withdraw().");
        System.out.println(" 2. Set a Conditional Breakpoint on line with condition: amount > 500.");
        System.out.println(" 3. Add Watch Expression: this.balance.");
        System.out.println(" 4. Perform live code change and trigger Hot Code Replace in your IDE!");
    }

    /**
     * Interactive console menu driving bank transactions.
     */
    private static void runInteractiveMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("\n======================================================================");
        System.out.println("                   INTERACTIVE BANKING SYSTEM                         ");
        System.out.println("======================================================================");

        while (running) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Create New Bank Account");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Transfer Funds");
            System.out.println("5. View All Accounts");
            System.out.println("6. Check Static Counter");
            System.out.println("7. Exit Application");
            System.out.print("Enter your choice (1-7): ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> handleCreateAccount(scanner);
                case "2" -> handleDeposit(scanner);
                case "3" -> handleWithdraw(scanner);
                case "4" -> handleTransfer(scanner);
                case "5" -> handleViewAccounts();
                case "6" -> System.out.println("\nTotal Accounts Created Across System: " + service.getTotalAccountCount());
                case "7" -> {
                    System.out.println("\nThank you for using Day 4 Bank Account System. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid option! Please enter a number between 1 and 7.");
            }
        }
    }

    private static void handleCreateAccount(Scanner scanner) {
        System.out.println("\n--- Create Account ---");
        System.out.print("Enter Account Holder Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter Initial Balance (or press Enter for default ₹1000.00): ");
        String balanceInput = scanner.nextLine().trim();

        try {
            if (balanceInput.isEmpty()) {
                BankAccount acc = service.createAccount(name);
                System.out.println("SUCCESS: Account created via Constructor 1 -> " + acc);
            } else {
                double initialBalance = Double.parseDouble(balanceInput);
                System.out.print("Enter Account Type (SAVINGS/CURRENT) (or press Enter for default SAVINGS): ");
                String typeInput = scanner.nextLine().trim();

                if (typeInput.isEmpty()) {
                    BankAccount acc = service.createAccount(name, initialBalance);
                    System.out.println("SUCCESS: Account created via Constructor 2 -> " + acc);
                } else {
                    BankAccount acc = service.createAccount(name, initialBalance, typeInput);
                    System.out.println("SUCCESS: Account created via Constructor 3 -> " + acc);
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("ERROR: Invalid initial balance number!");
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private static void handleDeposit(Scanner scanner) {
        System.out.println("\n--- Deposit Funds ---");
        System.out.print("Enter Account Number (e.g. ACC-0001): ");
        String accNo = scanner.nextLine().trim();

        System.out.print("Enter Deposit Amount: ");
        try {
            double amount = Double.parseDouble(scanner.nextLine().trim());
            double newBalance = service.deposit(accNo, amount);
            System.out.printf("SUCCESS: Deposited ₹%.2f. New Balance: ₹%.2f\n", amount, newBalance);
        } catch (NumberFormatException e) {
            System.out.println("ERROR: Invalid deposit amount!");
        } catch (AccountNotFoundException | IllegalArgumentException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private static void handleWithdraw(Scanner scanner) {
        System.out.println("\n--- Withdraw Funds ---");
        System.out.print("Enter Account Number (e.g. ACC-0001): ");
        String accNo = scanner.nextLine().trim();

        System.out.print("Enter Withdrawal Amount: ");
        try {
            double amount = Double.parseDouble(scanner.nextLine().trim());
            double newBalance = service.withdraw(accNo, amount);
            System.out.printf("SUCCESS: Withdrew ₹%.2f. New Balance: ₹%.2f\n", amount, newBalance);
        } catch (NumberFormatException e) {
            System.out.println("ERROR: Invalid withdrawal amount!");
        } catch (AccountNotFoundException | InsufficientBalanceException | IllegalArgumentException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private static void handleTransfer(Scanner scanner) {
        System.out.println("\n--- Transfer Funds ---");
        System.out.print("Enter Source Account Number: ");
        String src = scanner.nextLine().trim();

        System.out.print("Enter Destination Account Number: ");
        String dest = scanner.nextLine().trim();

        System.out.print("Enter Transfer Amount: ");
        try {
            double amount = Double.parseDouble(scanner.nextLine().trim());
            service.transfer(src, dest, amount);
            System.out.printf("SUCCESS: Transferred ₹%.2f from %s to %s\n", amount, src, dest);
        } catch (NumberFormatException e) {
            System.out.println("ERROR: Invalid transfer amount!");
        } catch (AccountNotFoundException | InsufficientBalanceException | IllegalArgumentException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private static void handleViewAccounts() {
        System.out.println("\n--- Registered Accounts ---");
        if (service.getAllAccounts().isEmpty()) {
            System.out.println("No accounts registered.");
        } else {
            for (BankAccount acc : service.getAllAccounts()) {
                System.out.println("  " + acc);
            }
        }
    }
}
