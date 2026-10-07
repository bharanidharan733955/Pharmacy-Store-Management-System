package com.pharmacy.core;

import com.pharmacy.core.model.Batch;
import com.pharmacy.core.model.Invoice;
import com.pharmacy.core.service.PharmacyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PharmacyServiceTest {

    private PharmacyService pharmacyService;

    @BeforeEach
    public void setUp() {
        pharmacyService = new PharmacyService();
    }

    @Test
    @DisplayName("Test Initial Medicine Inventory Count")
    public void testInitialMedicines() {
        assertEquals(3, pharmacyService.getAllMedicines().size());
    }

    @Test
    @DisplayName("Test FEFO Batch Sorting Order")
    public void testFEFOBatchSorting() {
        List<Batch> fefoList = pharmacyService.getBatchesByFEFO("M101");
        assertFalse(fefoList.isEmpty());
        // First batch should be the one expiring earlier (B2025-X before B2026-A)
        assertEquals("B2025-X", fefoList.get(0).getBatchNumber());
    }

    @Test
    @DisplayName("Test POS Billing & Stock Deduction")
    public void testPOSBilling() {
        Invoice invoice = pharmacyService.processPOSBilling("M101", 10);
        assertNotNull(invoice);
        assertEquals(155.00, invoice.getTotalAmount(), 0.001);
    }

    @Test
    @DisplayName("Test Expired Batch Detection")
    public void testExpiredBatches() {
        List<Batch> expired = pharmacyService.getExpiredBatches();
        assertEquals(1, expired.size());
        assertEquals("B2024-EXP", expired.get(0).getBatchNumber());
    }
}
