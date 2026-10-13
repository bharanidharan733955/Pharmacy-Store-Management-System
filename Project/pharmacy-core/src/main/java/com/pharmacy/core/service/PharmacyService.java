package com.pharmacy.core.service;

import com.pharmacy.core.model.*;
import com.pharmacy.core.strategy.DiscountStrategy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class PharmacyService {

    private final List<Medicine> medicines = new ArrayList<>();
    private final List<Supplier> suppliers = new ArrayList<>();
    private final List<Batch> batches = new ArrayList<>();
    private final List<Invoice> invoices = new ArrayList<>();
    private User currentUser = new AdminUser("admin01", "admin@pharmacy.com", "Default Administrator");
    private String currentRole = "Admin";

    public PharmacyService() {
        seedSampleData();
    }

    private void seedSampleData() {
        // Sample Medicines (FR-1)
        medicines.add(new Medicine("M101", "Paracetamol 500mg", "Analgesic", 15.50, false));
        medicines.add(new Medicine("M102", "Amoxicillin 250mg", "Antibiotic", 45.00, true));
        medicines.add(new Medicine("M103", "Cetirizine 10mg", "Antihistamine", 12.00, false));

        // Sample Suppliers (FR-2)
        suppliers.add(new Supplier("S101", "Apex Pharma Dist", "Rajesh Kumar", "+91 9876543210"));
        suppliers.add(new Supplier("S102", "Med Life Logistics", "Anitha Rao", "+91 9123456789"));

        // Sample Batches (FR-4 & FEFO)
        batches.add(new Batch("B2026-A", "M101", LocalDate.now().plusMonths(6), 150));
        batches.add(new Batch("B2025-X", "M101", LocalDate.now().plusMonths(2), 50)); // FEFO prior
        batches.add(new Batch("B2026-B", "M102", LocalDate.now().plusMonths(12), 200));
        batches.add(new Batch("B2024-EXP", "M103", LocalDate.now().minusDays(10), 30)); // Expired for FR-8 testing
    }

    // Current User Role Management (FR-9)
    public String getCurrentRole() {
        return currentRole;
    }

    public void setCurrentRole(String currentRole) {
        this.currentRole = currentRole;
    }

    // FR-1: Medicine Management
    public List<Medicine> getAllMedicines() {
        return medicines;
    }

    public void addMedicine(Medicine medicine) {
        medicines.add(medicine);
    }

    // FR-2: Supplier Management
    public List<Supplier> getAllSuppliers() {
        return suppliers;
    }

    public void addSupplier(Supplier supplier) {
        suppliers.add(supplier);
    }

    // FR-3: Purchase Entry
    public boolean recordPurchaseEntry(String supplierId, String medicineId, String batchNum, int qty, LocalDate expiry) {
        Optional<Medicine> med = medicines.stream().filter(m -> m.getId().equalsIgnoreCase(medicineId)).findFirst();
        if (med.isEmpty()) return false;

        batches.add(new Batch(batchNum, medicineId, expiry, qty));
        return true;
    }

    // FR-4: FEFO Batch Selection
    public List<Batch> getBatchesByFEFO(String medicineId) {
        return batches.stream()
                .filter(b -> b.getMedicineId().equalsIgnoreCase(medicineId) && b.getQuantity() > 0)
                .sorted(Comparator.comparing(Batch::getExpiryDate))
                .toList();
    }

    // FR-5: Inventory Management
    public List<Batch> getAllBatches() {
        return batches;
    }

    public int getTotalStock(String medicineId) {
        return batches.stream()
                .filter(b -> b.getMedicineId().equalsIgnoreCase(medicineId))
                .mapToInt(Batch::getQuantity)
                .sum();
    }

    // FR-6: POS Billing & Invoice Generation with Business Rules 1 & 2
    public Invoice processPOSBilling(String medicineId, int requestedQty) {
        return processPOSBilling(medicineId, requestedQty, null, null);
    }

    public Invoice processPOSBilling(String medicineId, int requestedQty, String prescriptionReference) {
        return processPOSBilling(medicineId, requestedQty, prescriptionReference, null);
    }

    public Invoice processPOSBilling(String medicineId, int requestedQty, String prescriptionReference, String specificBatchNumber) {
        if (medicineId == null || medicineId.trim().isEmpty()) {
            throw new IllegalArgumentException("Medicine ID cannot be null or empty.");
        }
        if (requestedQty <= 0) {
            throw new IllegalArgumentException("Requested quantity must be greater than zero.");
        }

        Optional<Medicine> medOpt = medicines.stream()
                .filter(m -> m.getId().equalsIgnoreCase(medicineId.trim()))
                .findFirst();
        if (medOpt.isEmpty()) {
            throw new com.pharmacy.core.exception.ResourceNotFoundException("Medicine", medicineId);
        }

        Medicine medicine = medOpt.get();

        // Business Rule 2: Scheduled medicines require a prescription reference
        if (medicine.isRequiresPrescription()) {
            if (prescriptionReference == null || prescriptionReference.trim().isEmpty()) {
                throw new com.pharmacy.core.exception.InvalidPrescriptionException(medicine.getId(), medicine.getName());
            }
        }

        // Business Rule 1: Expired medicine cannot be sold
        if (specificBatchNumber != null && !specificBatchNumber.trim().isEmpty()) {
            Optional<Batch> batchOpt = batches.stream()
                    .filter(b -> b.getBatchNumber().equalsIgnoreCase(specificBatchNumber.trim()))
                    .findFirst();
            if (batchOpt.isEmpty()) {
                throw new com.pharmacy.core.exception.ResourceNotFoundException("Batch", specificBatchNumber);
            }
            Batch targetBatch = batchOpt.get();
            if (targetBatch.isExpired()) {
                throw new com.pharmacy.core.exception.ExpiredBatchException(
                        targetBatch.getBatchNumber(), medicine.getId(), targetBatch.getExpiryDate());
            }
            if (targetBatch.getQuantity() < requestedQty) {
                throw new com.pharmacy.core.exception.InsufficientStockException(
                        medicine.getId(), requestedQty, targetBatch.getQuantity());
            }
            targetBatch.reduceQuantity(requestedQty);
        } else {
            // FEFO Allocation: Select earliest-expiring eligible batch first, skip expired batches
            List<Batch> allMedBatches = batches.stream()
                    .filter(b -> b.getMedicineId().equalsIgnoreCase(medicineId))
                    .toList();

            List<Batch> eligibleBatches = allMedBatches.stream()
                    .filter(b -> b.getQuantity() > 0 && !b.isExpired())
                    .sorted(Comparator.comparing(Batch::getExpiryDate))
                    .toList();

            int totalEligibleStock = eligibleBatches.stream().mapToInt(Batch::getQuantity).sum();

            if (totalEligibleStock == 0) {
                boolean hasExpiredBatches = allMedBatches.stream().anyMatch(Batch::isExpired);
                if (hasExpiredBatches) {
                    throw new com.pharmacy.core.exception.ExpiredBatchException(
                            "Cannot sell medicine '" + medicine.getName() + "': all existing stock batches have expired.");
                } else {
                    throw new com.pharmacy.core.exception.InsufficientStockException(medicine.getId(), requestedQty, 0);
                }
            }

            if (totalEligibleStock < requestedQty) {
                throw new com.pharmacy.core.exception.InsufficientStockException(
                        medicine.getId(), requestedQty, totalEligibleStock);
            }

            // Decrement eligible batches in FEFO order atomically
            int remainingToFulfill = requestedQty;
            for (Batch batch : eligibleBatches) {
                int take = Math.min(batch.getQuantity(), remainingToFulfill);
                batch.reduceQuantity(take);
                remainingToFulfill -= take;
                if (remainingToFulfill == 0) break;
            }
        }

        double totalAmount = medicine.getUnitPrice() * requestedQty;
        Invoice invoice = new Invoice("INV" + (1000 + invoices.size() + 1), medicine.getName(), requestedQty, totalAmount);
        invoices.add(invoice);
        return invoice;
    }

    // FR-7: Sales & Stock Reports
    public List<Invoice> getAllInvoices() {
        return invoices;
    }

    // FR-8: Expiry Management & Returns
    public List<Batch> getExpiredBatches() {
        return batches.stream()
                .filter(b -> b.getExpiryDate().isBefore(LocalDate.now()))
                .toList();
    }

    // Role Hierarchy & Strategy Pattern Helpers
    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        if (user != null) {
            this.currentUser = user;
            this.currentRole = user.getRole().getDisplayName();
        }
    }

    public double calculateDiscount(double subtotal, int itemQty, DiscountStrategy strategy) {
        if (strategy == null) return 0.0;
        return strategy.calculateDiscount(subtotal, itemQty, currentUser);
    }
}
