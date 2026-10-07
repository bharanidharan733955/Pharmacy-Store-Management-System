package com.pharmacy.cli;

import com.pharmacy.core.model.Batch;
import com.pharmacy.core.model.Invoice;
import com.pharmacy.core.model.Medicine;
import com.pharmacy.core.model.Supplier;
import com.pharmacy.core.service.PharmacyService;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class PharmacyConsoleApp {

    private final PharmacyService pharmacyService;

    public PharmacyConsoleApp() {
        this.pharmacyService = new PharmacyService();
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        do {
            System.out.println("\n==========================================================================");
            System.out.println("            💊 PHARMACY STORE MANAGEMENT SYSTEM (Console CLI)             ");
            System.out.println("==========================================================================");
            System.out.println("Active User Role: " + pharmacyService.getCurrentRole());
            System.out.println("--------------------------------------------------------------------------");
            System.out.println("1. [FR-1] Medicine Management");
            System.out.println("2. [FR-2] Supplier Management");
            System.out.println("3. [FR-3] Purchase Entry & Order Management");
            System.out.println("4. [FR-4] Batch Creation & FEFO Batch Selection");
            System.out.println("5. [FR-5] Inventory & Stock Levels");
            System.out.println("6. [FR-6] POS Billing & Invoice Generation");
            System.out.println("7. [FR-7] Sales & Stock Reports");
            System.out.println("8. [FR-8] Expiry Management & Returns");
            System.out.println("9. [FR-9] Role & Access Control Switch");
            System.out.println("10. Exit System");
            System.out.println("==========================================================================");
            System.out.print("Select an option (1-10): ");

            String input = scanner.nextLine().trim();
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Invalid input format! Please enter a valid number (1-10).");
                continue;
            }

            if (choice < 1 || choice > 10) {
                System.out.println("[ERROR] Option out of range! Choose a number between 1 and 10.");
                continue;
            }

            switch (choice) {
                case 1 -> handleMedicineManagement(scanner);
                case 2 -> handleSupplierManagement(scanner);
                case 3 -> handlePurchaseEntry(scanner);
                case 4 -> handleFEFOBatchSelection(scanner);
                case 5 -> handleInventoryManagement();
                case 6 -> handlePOSBilling(scanner);
                case 7 -> handleSalesReports();
                case 8 -> handleExpiryManagement();
                case 9 -> handleRoleAccessControl(scanner);
                case 10 -> System.out.println("\nThank you for using Pharmacy Store Management System. Goodbye!");
                default -> System.out.println("[ERROR] Invalid selection.");
            }

        } while (choice != 10);
    }

    // FR-1: Medicine Management
    private void handleMedicineManagement(Scanner scanner) {
        System.out.println("\n--- [FR-1] MEDICINE MANAGEMENT ---");
        System.out.println("1. View Registered Medicines");
        System.out.println("2. Add New Medicine");
        System.out.print("Choice: ");
        String subChoice = scanner.nextLine().trim();

        if ("1".equals(subChoice)) {
            pharmacyService.getAllMedicines().forEach(System.out::println);
        } else if ("2".equals(subChoice)) {
            System.out.print("Enter ID (e.g. M104): ");
            String id = scanner.nextLine().trim();
            System.out.print("Enter Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Enter Category: ");
            String cat = scanner.nextLine().trim();
            System.out.print("Enter Unit Price (₹): ");
            double price = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Requires Prescription? (true/false): ");
            boolean rx = Boolean.parseBoolean(scanner.nextLine().trim());

            pharmacyService.addMedicine(new Medicine(id, name, cat, price, rx));
            System.out.println("[SUCCESS] Medicine added successfully.");
        }
    }

    // FR-2: Supplier Management
    private void handleSupplierManagement(Scanner scanner) {
        System.out.println("\n--- [FR-2] SUPPLIER MANAGEMENT ---");
        System.out.println("1. View Registered Suppliers");
        System.out.println("2. Add New Supplier");
        System.out.print("Choice: ");
        String subChoice = scanner.nextLine().trim();

        if ("1".equals(subChoice)) {
            pharmacyService.getAllSuppliers().forEach(System.out::println);
        } else if ("2".equals(subChoice)) {
            System.out.print("Enter Supplier ID (e.g. S103): ");
            String id = scanner.nextLine().trim();
            System.out.print("Enter Company Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Enter Contact Person: ");
            String contact = scanner.nextLine().trim();
            System.out.print("Enter Phone: ");
            String phone = scanner.nextLine().trim();

            pharmacyService.addSupplier(new Supplier(id, name, contact, phone));
            System.out.println("[SUCCESS] Supplier added successfully.");
        }
    }

    // FR-3: Purchase Entry
    private void handlePurchaseEntry(Scanner scanner) {
        System.out.println("\n--- [FR-3] PURCHASE ENTRY & ORDER MANAGEMENT ---");
        System.out.print("Enter Supplier ID: ");
        String supId = scanner.nextLine().trim();
        System.out.print("Enter Medicine ID (e.g. M101): ");
        String medId = scanner.nextLine().trim();
        System.out.print("Enter Batch Number (e.g. B2026-C): ");
        String batchNum = scanner.nextLine().trim();
        System.out.print("Enter Quantity Received: ");
        int qty = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Enter Expiry Date (YYYY-MM-DD): ");
        LocalDate expiry = LocalDate.parse(scanner.nextLine().trim());

        boolean ok = pharmacyService.recordPurchaseEntry(supId, medId, batchNum, qty, expiry);
        if (ok) {
            System.out.println("[SUCCESS] Purchase entry recorded and inventory batch updated.");
        } else {
            System.out.println("[ERROR] Purchase entry failed. Medicine ID not found!");
        }
    }

    // FR-4: FEFO Batch Selection
    private void handleFEFOBatchSelection(Scanner scanner) {
        System.out.println("\n--- [FR-4] BATCH CREATION & FEFO SELECTION ---");
        System.out.print("Enter Medicine ID (e.g. M101): ");
        String medId = scanner.nextLine().trim();

        List<Batch> fefoList = pharmacyService.getBatchesByFEFO(medId);
        System.out.println("Batches sorted by FEFO (First Expiry, First Out):");
        if (fefoList.isEmpty()) {
            System.out.println("No active batches found for Medicine ID: " + medId);
        } else {
            fefoList.forEach(System.out::println);
        }
    }

    // FR-5: Inventory Management
    private void handleInventoryManagement() {
        System.out.println("\n--- [FR-5] INVENTORY & STOCK LEVELS ---");
        List<Batch> allBatches = pharmacyService.getAllBatches();
        allBatches.forEach(System.out::println);
    }

    // FR-6: POS Billing
    private void handlePOSBilling(Scanner scanner) {
        System.out.println("\n--- [FR-6] POS BILLING & INVOICE GENERATION ---");
        System.out.print("Enter Medicine ID to bill (e.g. M101): ");
        String medId = scanner.nextLine().trim();
        System.out.print("Enter Quantity: ");
        int qty = Integer.parseInt(scanner.nextLine().trim());

        Invoice inv = pharmacyService.processPOSBilling(medId, qty);
        if (inv != null) {
            System.out.println("[SUCCESS] Invoice Generated via FEFO Selection:");
            System.out.println(inv);
        } else {
            System.out.println("[ERROR] Billing failed! Insufficient non-expired stock for Medicine ID: " + medId);
        }
    }

    // FR-7: Sales Reports
    private void handleSalesReports() {
        System.out.println("\n--- [FR-7] SALES & STOCK REPORTS ---");
        List<Invoice> invoices = pharmacyService.getAllInvoices();
        if (invoices.isEmpty()) {
            System.out.println("No sales invoices recorded yet.");
        } else {
            double totalRev = invoices.stream().mapToDouble(Invoice::getTotalAmount).sum();
            invoices.forEach(System.out::println);
            System.out.printf("Total Cumulative Revenue: ₹%.2f\n", totalRev);
        }
    }

    // FR-8: Expiry Management
    private void handleExpiryManagement() {
        System.out.println("\n--- [FR-8] EXPIRY MANAGEMENT & RETURNS ---");
        List<Batch> expired = pharmacyService.getExpiredBatches();
        if (expired.isEmpty()) {
            System.out.println("No expired batches detected in stock.");
        } else {
            System.out.println("Expired Batches flagged for vendor return:");
            expired.forEach(System.out::println);
        }
    }

    // FR-9: Role Access Control
    private void handleRoleAccessControl(Scanner scanner) {
        System.out.println("\n--- [FR-9] ROLE & ACCESS CONTROL ---");
        System.out.println("Select Role to Switch:");
        System.out.println("1. Admin");
        System.out.println("2. Pharmacist");
        System.out.println("3. Cashier");
        System.out.print("Choice: ");
        String rChoice = scanner.nextLine().trim();

        switch (rChoice) {
            case "1" -> pharmacyService.setCurrentRole("Admin");
            case "2" -> pharmacyService.setCurrentRole("Pharmacist");
            case "3" -> pharmacyService.setCurrentRole("Cashier");
            default -> System.out.println("[ERROR] Invalid role choice.");
        }
        System.out.println("[SUCCESS] Switched active role to: " + pharmacyService.getCurrentRole());
    }
}
