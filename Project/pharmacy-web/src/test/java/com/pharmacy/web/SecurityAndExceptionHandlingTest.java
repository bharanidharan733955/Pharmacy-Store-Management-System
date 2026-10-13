package com.pharmacy.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmacy.web.dto.AuthRequest;
import com.pharmacy.web.dto.CreateInvoiceRequest;
import com.pharmacy.web.dto.CreateSupplierRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityAndExceptionHandlingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test 8: Validation failures return the expected structured error")
    @WithMockUser(username = "cashier", roles = {"CASHIER"})
    public void testValidationFailuresReturnStructuredError() throws Exception {
        // Quantity <= 0 and empty medicine ID violates bean validation
        CreateInvoiceRequest invalidRequest = new CreateInvoiceRequest("", -5, null, null);

        mockMvc.perform(post("/api/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.correlationId", notNullValue()))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("Test 9a: Unauthenticated access returns HTTP 401 with structured JSON")
    public void testUnauthenticatedAccessReturns401() throws Exception {
        mockMvc.perform(get("/api/dashboard/admin"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.errorCode").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.correlationId", notNullValue()));
    }

    @Test
    @DisplayName("Test 9b: Role-protected APIs reject unauthorized access with HTTP 403")
    @WithMockUser(username = "cashier", roles = {"CASHIER"})
    public void testCashierForbiddenFromAdminDashboard() throws Exception {
        mockMvc.perform(get("/api/dashboard/admin"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.errorCode").value("FORBIDDEN"))
                .andExpect(jsonPath("$.correlationId", notNullValue()));
    }

    @Test
    @DisplayName("Test 9c: Cashier forbidden from supplier creation")
    @WithMockUser(username = "cashier", roles = {"CASHIER"})
    public void testCashierForbiddenFromSupplierCreation() throws Exception {
        CreateSupplierRequest request = new CreateSupplierRequest("S999", "Illegal Supplies", "Bad Actor", "0000000000");

        mockMvc.perform(post("/api/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.errorCode").value("FORBIDDEN"));
    }

    @Test
    @DisplayName("Test 9d: Admin can access Admin dashboard")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testAdminCanAccessAdminDashboard() throws Exception {
        mockMvc.perform(get("/api/dashboard/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalActiveMedicines", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.stockUnitMeasure", notNullValue()));
    }

    @Test
    @DisplayName("Test 9e: Pharmacist can access Pharmacist dashboard")
    @WithMockUser(username = "pharmacist", roles = {"PHARMACIST"})
    public void testPharmacistCanAccessPharmacistDashboard() throws Exception {
        mockMvc.perform(get("/api/dashboard/pharmacist?threshold=25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCatalogMedicines", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.lowStockThreshold").value(25));
    }
}
