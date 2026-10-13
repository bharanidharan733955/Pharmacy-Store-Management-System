package com.pharmacy.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmacy.core.exception.ExpiredBatchException;
import com.pharmacy.core.exception.InvalidPrescriptionException;
import com.pharmacy.core.model.Batch;
import com.pharmacy.core.model.Invoice;
import com.pharmacy.web.dto.CreateInvoiceRequest;
import com.pharmacy.web.service.PharmacyWebService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BusinessRuleBillingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PharmacyWebService pharmacyWebService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test 1: An expired batch sale is rejected")
    @WithMockUser(username = "cashier", roles = {"CASHIER"})
    public void testExpiredBatchSaleIsRejected() throws Exception {
        // M103 has expired batch B2024-EXP (expired 10 days ago)
        CreateInvoiceRequest request = new CreateInvoiceRequest("M103", 5, null, "B2024-EXP");

        mockMvc.perform(post("/api/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.errorCode").value("EXPIRED_BATCH"))
                .andExpect(jsonPath("$.message", containsString("expired")));
    }

    @Test
    @DisplayName("Test 2: Expired stock is not decremented on rejection")
    @WithMockUser(username = "cashier", roles = {"CASHIER"})
    public void testExpiredStockIsNotDecremented() {
        Batch expiredBatch = pharmacyWebService.getAllBatches().stream()
                .filter(b -> b.getBatchNumber().equalsIgnoreCase("B2024-EXP"))
                .findFirst()
                .orElseThrow();
        int initialQuantity = expiredBatch.getQuantity();

        CreateInvoiceRequest request = new CreateInvoiceRequest("M103", 5, null, "B2024-EXP");
        assertThrows(ExpiredBatchException.class, () -> pharmacyWebService.createInvoice(request));

        // Verify stock remains exactly unchanged
        assertEquals(initialQuantity, expiredBatch.getQuantity(), "Expired stock must not be decremented");
    }

    @Test
    @DisplayName("Test 3: No successful invoice is persisted for a rejected expired-stock sale")
    @WithMockUser(username = "cashier", roles = {"CASHIER"})
    public void testNoInvoicePersistedOnExpiredSale() {
        int initialInvoiceCount = pharmacyWebService.getAllInvoices().size();

        CreateInvoiceRequest request = new CreateInvoiceRequest("M103", 5, null, "B2024-EXP");
        assertThrows(ExpiredBatchException.class, () -> pharmacyWebService.createInvoice(request));

        assertEquals(initialInvoiceCount, pharmacyWebService.getAllInvoices().size(),
                "Invoice count must remain unchanged when sale fails");
    }

    @Test
    @DisplayName("Test 4: A scheduled medicine without a prescription reference is rejected")
    @WithMockUser(username = "cashier", roles = {"CASHIER"})
    public void testScheduledMedicineWithoutPrescriptionIsRejected() throws Exception {
        // M102 requires prescription
        CreateInvoiceRequest requestWithoutRx = new CreateInvoiceRequest("M102", 5, null, null);

        mockMvc.perform(post("/api/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestWithoutRx)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.errorCode").value("PRESCRIPTION_REFERENCE_REQUIRED"))
                .andExpect(jsonPath("$.message", containsString("scheduled drug requiring a valid doctor prescription reference")));
    }

    @Test
    @DisplayName("Test 5: Stock is unchanged when prescription validation fails")
    @WithMockUser(username = "cashier", roles = {"CASHIER"})
    public void testStockUnchangedWhenPrescriptionFails() {
        int initialStock = pharmacyWebService.getSaleableStock("M102");

        CreateInvoiceRequest request = new CreateInvoiceRequest("M102", 10, "   ", null); // blank rx
        assertThrows(InvalidPrescriptionException.class, () -> pharmacyWebService.createInvoice(request));

        int afterStock = pharmacyWebService.getSaleableStock("M102");
        assertEquals(initialStock, afterStock, "Stock must remain unchanged when prescription validation fails");
    }

    @Test
    @DisplayName("Test 6: A valid sale using an eligible batch succeeds")
    @WithMockUser(username = "cashier", roles = {"CASHIER"})
    public void testValidSaleSucceeds() throws Exception {
        // M102 with valid prescription reference
        CreateInvoiceRequest validRequest = new CreateInvoiceRequest("M102", 5, "RX-DR-SMITH-9821", null);

        mockMvc.perform(post("/api/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.medicineName").value("Amoxicillin 250mg"))
                .andExpect(jsonPath("$.quantity").value(5))
                .andExpect(jsonPath("$.totalAmount").value(225.00));
    }

    @Test
    @DisplayName("Test 7: FEFO chooses the earliest-expiring eligible batch and skips expired batches")
    public void testFEFOChoosesEarliestEligibleBatch() {
        // M101 has B2025-X (expiry in 2 months, qty 50) and B2026-A (expiry in 6 months, qty 150)
        Batch earliestBatch = pharmacyWebService.getAllBatches().stream()
                .filter(b -> b.getBatchNumber().equalsIgnoreCase("B2025-X"))
                .findFirst()
                .orElseThrow();
        int initialB2025Qty = earliestBatch.getQuantity();

        // Bill 10 units of M101
        CreateInvoiceRequest request = new CreateInvoiceRequest("M101", 10, null, null);
        Invoice invoice = pharmacyWebService.createInvoice(request);

        assertNotNull(invoice);
        assertEquals(initialB2025Qty - 10, earliestBatch.getQuantity(),
                "FEFO must allocate from earliest-expiring batch B2025-X first");
    }
}
