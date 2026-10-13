package com.pharmacy.web.service;

import com.pharmacy.core.exception.ExpiredBatchException;
import com.pharmacy.core.exception.InsufficientStockException;
import com.pharmacy.core.exception.InvalidPrescriptionException;
import com.pharmacy.core.exception.ResourceNotFoundException;
import com.pharmacy.core.model.Batch;
import com.pharmacy.core.model.Invoice;
import com.pharmacy.core.model.Medicine;
import com.pharmacy.core.model.Supplier;
import com.pharmacy.web.dto.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Enterprise Service handling Pharmacy domain operations, dashboard analytics,
 * and enforcing Business Rule 1 (FEFO/Expired Rejection) & Business Rule 2 (Prescription Validation).
 */
@Service
public class PharmacyWebService {

    private final PasswordEncoder passwordEncoder;

    // Concurrent Thread-safe collections
    private final Map<String, UserAccount> users = new ConcurrentHashMap<>();
    private final List<Medicine> medicines = new CopyOnWriteArrayList<>();
    private final List<Supplier> suppliers = new CopyOnWriteArrayList<>();
    private final List<Batch> batches = new CopyOnWriteArrayList<>();
    private final List<Invoice> invoices = new CopyOnWriteArrayList<>();
    private final List<Map<String, Object>> purchaseLogs = new CopyOnWriteArrayList<>();

    public static class UserAccount {
        private final String username;
        private final String passwordHash;
        private final String role;
        private final String fullName;
        private final Set<String> permissions;

        public UserAccount(String username, String passwordHash, String role, String fullName, Set<String> permissions) {
            this.username = username;
            this.passwordHash = passwordHash;
            this.role = role;
            this.fullName = fullName;
            this.permissions = permissions;
        }

        public String getUsername() { return username; }
        public String getPasswordHash() { return passwordHash; }
        public String getRole() { return role; }
        public String getFullName() { return fullName; }
        public Set<String> getPermissions() { return permissions; }
    }

    public PharmacyWebService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        initSeedData();
    }

    private void initSeedData() {
        // Users for testing/demo
        users.put("admin", new UserAccount("admin", passwordEncoder.encode("admin123"),
                "ADMIN", "Dr. Alice Morgan (Store Director)",
                Set.of("MEDICINE_MANAGE", "SUPPLIER_MANAGE", "STAFF_MANAGE", "REPORTS_VIEW", "BILLING_ACCESS")));

        users.put("pharmacist", new UserAccount("pharmacist", passwordEncoder.encode("pharma123"),
                "PHARMACIST", "John Doe (Lead Pharmacist)",
                Set.of("MEDICINE_VIEW", "PURCHASE_RECORD", "INVENTORY_VIEW", "EXPIRY_MANAGE", "BILLING_ACCESS")));

        users.put("cashier", new UserAccount("cashier", passwordEncoder.encode("cashier123"),
                "CASHIER", "Emily Watson (Chief Cashier)",
                Set.of("MEDICINE_SEARCH", "BILLING_ACCESS", "INVOICE_VIEW")));

        // Medicines
        medicines.add(new Medicine("M101", "Paracetamol 500mg", "Analgesic", 15.50, false));
        medicines.add(new Medicine("M102", "Amoxicillin 250mg", "Antibiotic", 45.00, true));
        medicines.add(new Medicine("M103", "Cetirizine 10mg", "Antihistamine", 12.00, false));
        medicines.add(new Medicine("M104", "Morphine Sulfate 10mg", "Narcotic/Analgesic", 120.00, true));

        // Suppliers
        suppliers.add(new Supplier("S101", "Apex Pharma Dist", "Rajesh Kumar", "+91 9876543210"));
        suppliers.add(new Supplier("S102", "Med Life Logistics", "Anitha Rao", "+91 9123456789"));

        // Batches
        batches.add(new Batch("B2026-A", "M101", LocalDate.now().plusMonths(6), 150));
        batches.add(new Batch("B2025-X", "M101", LocalDate.now().plusMonths(2), 50)); // FEFO prior
        batches.add(new Batch("B2026-B", "M102", LocalDate.now().plusMonths(12), 200));
        batches.add(new Batch("B2024-EXP", "M103", LocalDate.now().minusDays(10), 30)); // Expired stock for BR-1 testing
        batches.add(new Batch("B2026-M", "M104", LocalDate.now().plusMonths(18), 80));

        // Initial Purchase Log
        Map<String, Object> p1 = new LinkedHashMap<>();
        p1.put("purchaseId", "PUR-1001");
        p1.put("supplierId", "S101");
        p1.put("supplierName", "Apex Pharma Dist");
        p1.put("medicineId", "M101");
        p1.put("medicineName", "Paracetamol 500mg");
        p1.put("batchNumber", "B2026-A");
        p1.put("quantity", 150);
        p1.put("expiryDate", LocalDate.now().plusMonths(6).toString());
        p1.put("timestamp", LocalDateTime.now().minusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        purchaseLogs.add(p1);

        // Initial Invoice
        Invoice initialInv = new Invoice("INV-1001", "Paracetamol 500mg", 10, 155.00);
        invoices.add(initialInv);
    }

    // User authentication
    public UserAccount authenticate(String username, String rawPassword) {
        UserAccount account = users.get(username);
        if (account != null && passwordEncoder.matches(rawPassword, account.getPasswordHash())) {
            return account;
        }
        return null;
    }

    public UserAccount getUserByUsername(String username) {
        return users.get(username);
    }

    // Medicines
    public List<Medicine> getAllMedicines() {
        return Collections.unmodifiableList(medicines);
    }

    public Medicine getMedicineById(String id) {
        return medicines.stream()
                .filter(m -> m.getId().equalsIgnoreCase(id.trim()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Medicine", id));
    }

    public Medicine addMedicine(CreateMedicineRequest req) {
        Medicine med = new Medicine(req.getId(), req.getName(), req.getCategory(), req.getUnitPrice(), req.isRequiresPrescription());
        medicines.add(med);
        return med;
    }

    // Suppliers
    public List<Supplier> getAllSuppliers() {
        return Collections.unmodifiableList(suppliers);
    }

    public Supplier getSupplierById(String id) {
        return suppliers.stream()
                .filter(s -> s.getId().equalsIgnoreCase(id.trim()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", id));
    }

    public Supplier addSupplier(CreateSupplierRequest req) {
        Supplier sup = new Supplier(req.getId(), req.getName(), req.getContactPerson(), req.getPhone());
        suppliers.add(sup);
        return sup;
    }

    // Batches & Inventory
    public List<Batch> getAllBatches() {
        return Collections.unmodifiableList(batches);
    }

    public List<Batch> getBatchesForMedicine(String medicineId) {
        return batches.stream()
                .filter(b -> b.getMedicineId().equalsIgnoreCase(medicineId))
                .toList();
    }

    public int getSaleableStock(String medicineId) {
        return batches.stream()
                .filter(b -> b.getMedicineId().equalsIgnoreCase(medicineId) && !b.isExpired() && b.getQuantity() > 0)
                .mapToInt(Batch::getQuantity)
                .sum();
    }

    // Purchase Entry
    public synchronized Map<String, Object> recordPurchase(PurchaseEntryRequest req) {
        Medicine med = getMedicineById(req.getMedicineId());
        Supplier sup = getSupplierById(req.getSupplierId());

        Batch batch = new Batch(req.getBatchNumber(), med.getId(), req.getExpiryDate(), req.getQuantity());
        batches.add(batch);

        Map<String, Object> logEntry = new LinkedHashMap<>();
        logEntry.put("purchaseId", "PUR-" + (1000 + purchaseLogs.size() + 1));
        logEntry.put("supplierId", sup.getId());
        logEntry.put("supplierName", sup.getName());
        logEntry.put("medicineId", med.getId());
        logEntry.put("medicineName", med.getName());
        logEntry.put("batchNumber", batch.getBatchNumber());
        logEntry.put("quantity", req.getQuantity());
        logEntry.put("expiryDate", req.getExpiryDate().toString());
        logEntry.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        purchaseLogs.add(logEntry);

        return logEntry;
    }

    // Invoices & Billing (Enforces Business Rules 1 & 2 with Atomic Transaction Semantics)
    public synchronized Invoice createInvoice(CreateInvoiceRequest req) {
        if (req.getMedicineId() == null || req.getMedicineId().isBlank()) {
            throw new IllegalArgumentException("Medicine ID cannot be blank.");
        }
        if (req.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }

        Medicine medicine = getMedicineById(req.getMedicineId());

        // Business Rule 2: Scheduled medicines require a prescription reference
        if (medicine.isRequiresPrescription()) {
            if (req.getPrescriptionReference() == null || req.getPrescriptionReference().trim().isEmpty()) {
                throw new InvalidPrescriptionException(medicine.getId(), medicine.getName());
            }
        }

        // Business Rule 1: Expired medicine cannot be sold
        if (req.getBatchNumber() != null && !req.getBatchNumber().trim().isEmpty()) {
            // Direct batch sale attempt
            Batch targetBatch = batches.stream()
                    .filter(b -> b.getBatchNumber().equalsIgnoreCase(req.getBatchNumber().trim()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Batch", req.getBatchNumber()));

            if (targetBatch.isExpired()) {
                throw new ExpiredBatchException(targetBatch.getBatchNumber(), medicine.getId(), targetBatch.getExpiryDate());
            }

            if (targetBatch.getQuantity() < req.getQuantity()) {
                throw new InsufficientStockException(medicine.getId(), req.getQuantity(), targetBatch.getQuantity());
            }

            targetBatch.reduceQuantity(req.getQuantity());
        } else {
            // FEFO Allocation: Select earliest-expiring eligible batch first, skip expired batches
            List<Batch> medBatches = batches.stream()
                    .filter(b -> b.getMedicineId().equalsIgnoreCase(medicine.getId()))
                    .toList();

            List<Batch> eligibleBatches = medBatches.stream()
                    .filter(b -> b.getQuantity() > 0 && !b.isExpired())
                    .sorted(Comparator.comparing(Batch::getExpiryDate))
                    .toList();

            int totalEligible = eligibleBatches.stream().mapToInt(Batch::getQuantity).sum();

            if (totalEligible == 0) {
                boolean hasExpired = medBatches.stream().anyMatch(Batch::isExpired);
                if (hasExpired) {
                    throw new ExpiredBatchException("Cannot dispense medicine '" + medicine.getName() + "': all available batches have expired.");
                } else {
                    throw new InsufficientStockException(medicine.getId(), req.getQuantity(), 0);
                }
            }

            if (totalEligible < req.getQuantity()) {
                throw new InsufficientStockException(medicine.getId(), req.getQuantity(), totalEligible);
            }

            // Deduct in FEFO order atomically
            int remaining = req.getQuantity();
            for (Batch b : eligibleBatches) {
                int take = Math.min(b.getQuantity(), remaining);
                b.reduceQuantity(take);
                remaining -= take;
                if (remaining == 0) break;
            }
        }

        double totalAmount = medicine.getUnitPrice() * req.getQuantity();
        Invoice invoice = new Invoice("INV-" + (1000 + invoices.size() + 1), medicine.getName(), req.getQuantity(), totalAmount);
        invoices.add(invoice);
        return invoice;
    }

    public List<Invoice> getAllInvoices() {
        return Collections.unmodifiableList(invoices);
    }

    public Invoice getInvoiceById(String id) {
        return invoices.stream()
                .filter(inv -> inv.getInvoiceId().equalsIgnoreCase(id.trim()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", id));
    }

    // Role-Based Dashboards
    public AdminDashboardDTO getAdminDashboard() {
        AdminDashboardDTO dto = new AdminDashboardDTO();
        dto.setTotalActiveMedicines(medicines.size());
        dto.setTotalSuppliers(suppliers.size());

        // Total available stock across non-expired batches
        long availableUnits = batches.stream()
                .filter(b -> !b.isExpired())
                .mapToLong(Batch::getQuantity)
                .sum();
        dto.setTotalAvailableStock(availableUnits);
        dto.setStockUnitMeasure("Units (Tablets / Bottles)");

        LocalDate today = LocalDate.now();
        LocalDate in30Days = today.plusDays(30);

        long expiringCount = batches.stream()
                .filter(b -> !b.isExpired() && !b.getExpiryDate().isAfter(in30Days))
                .count();
        dto.setMedicinesExpiringWithin30Days(expiringCount);

        // Today's invoices
        String todayPrefix = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        List<Invoice> todayInvoices = invoices.stream()
                .filter(inv -> inv.getTimestamp() != null && inv.getTimestamp().startsWith(todayPrefix))
                .toList();

        double todaySales = todayInvoices.stream().mapToDouble(Invoice::getTotalAmount).sum();
        dto.setTodaySalesTotal(todaySales);
        dto.setTodayInvoiceCount(todayInvoices.size());

        dto.setRecentPurchases(purchaseLogs.stream().limit(5).toList());
        dto.setRecentInvoices(invoices.stream().sorted((a, b) -> b.getInvoiceId().compareTo(a.getInvoiceId())).limit(5).toList());
        dto.setQuickActions(List.of("Manage Medicines", "Manage Suppliers", "Manage Staff", "View Sales Reports"));
        return dto;
    }

    public PharmacistDashboardDTO getPharmacistDashboard(int lowStockThreshold) {
        PharmacistDashboardDTO dto = new PharmacistDashboardDTO();
        dto.setTotalCatalogMedicines(medicines.size());
        dto.setLowStockThreshold(lowStockThreshold);

        LocalDate today = LocalDate.now();
        LocalDate in30Days = today.plusDays(30);

        // Low stock medicines (only count non-expired stock as saleable)
        List<Map<String, Object>> lowStockList = new ArrayList<>();
        for (Medicine m : medicines) {
            int saleableStock = getSaleableStock(m.getId());
            if (saleableStock <= lowStockThreshold) {
                Map<String, Object> item = new HashMap<>();
                item.put("medicineId", m.getId());
                item.put("medicineName", m.getName());
                item.put("category", m.getCategory());
                item.put("unitPrice", m.getUnitPrice());
                item.put("saleableStock", saleableStock);
                item.put("status", saleableStock == 0 ? "OUT_OF_STOCK" : "LOW_STOCK");
                lowStockList.add(item);
            }
        }
        dto.setLowStockMedicines(lowStockList);

        // Batches expiring within 30 days
        List<Batch> expiringBatches = batches.stream()
                .filter(b -> !b.isExpired() && !b.getExpiryDate().isAfter(in30Days) && b.getQuantity() > 0)
                .sorted(Comparator.comparing(Batch::getExpiryDate))
                .toList();
        dto.setBatchesExpiringWithin30Days(expiringBatches);

        // Expired batches requiring quarantine/return
        List<Batch> expiredBatches = batches.stream()
                .filter(Batch::isExpired)
                .toList();
        dto.setExpiredBatchesRequiringAttention(expiredBatches);

        long totalSaleable = batches.stream().filter(b -> !b.isExpired()).mapToLong(Batch::getQuantity).sum();
        long totalQuarantined = batches.stream().filter(Batch::isExpired).mapToLong(Batch::getQuantity).sum();
        dto.setTotalSaleableStock(totalSaleable);
        dto.setTotalQuarantinedExpiredStock(totalQuarantined);

        dto.setRecentPurchases(purchaseLogs.stream().limit(5).toList());
        dto.setQuickActions(List.of("Record Incoming Purchase", "View Batch Inventory", "Quarantine Expired Batches"));
        return dto;
    }

    public CashierDashboardDTO getCashierDashboard() {
        CashierDashboardDTO dto = new CashierDashboardDTO();
        LocalDate today = LocalDate.now();
        String todayPrefix = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        List<Invoice> todayInvoices = invoices.stream()
                .filter(inv -> inv.getTimestamp() != null && inv.getTimestamp().startsWith(todayPrefix))
                .toList();

        double todaySales = todayInvoices.stream().mapToDouble(Invoice::getTotalAmount).sum();
        dto.setTodaySalesTotal(todaySales);
        dto.setTodayInvoiceCount(todayInvoices.size());
        dto.setRecentInvoices(invoices.stream().sorted((a, b) -> b.getInvoiceId().compareTo(a.getInvoiceId())).limit(5).toList());
        dto.setQuickSearchMedicines(medicines);
        dto.setQuickActions(List.of("Start New Bill", "Search Medicines", "View Recent Invoices"));
        return dto;
    }
}
