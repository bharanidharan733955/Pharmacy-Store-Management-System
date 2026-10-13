package com.pharmacy.web.controller;

import com.pharmacy.web.dto.AdminDashboardDTO;
import com.pharmacy.web.dto.CashierDashboardDTO;
import com.pharmacy.web.dto.PharmacistDashboardDTO;
import com.pharmacy.web.service.PharmacyWebService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final PharmacyWebService pharmacyWebService;

    public DashboardController(PharmacyWebService pharmacyWebService) {
        this.pharmacyWebService = pharmacyWebService;
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDashboardDTO> getAdminDashboard() {
        return ResponseEntity.ok(pharmacyWebService.getAdminDashboard());
    }

    @GetMapping("/pharmacist")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'ADMIN')")
    public ResponseEntity<PharmacistDashboardDTO> getPharmacistDashboard(
            @RequestParam(name = "threshold", defaultValue = "20") int threshold) {
        return ResponseEntity.ok(pharmacyWebService.getPharmacistDashboard(threshold));
    }

    @GetMapping("/cashier")
    @PreAuthorize("hasAnyRole('CASHIER', 'ADMIN', 'PHARMACIST')")
    public ResponseEntity<CashierDashboardDTO> getCashierDashboard() {
        return ResponseEntity.ok(pharmacyWebService.getCashierDashboard());
    }
}
