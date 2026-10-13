package com.pharmacy.web.controller;

import com.pharmacy.core.model.Batch;
import com.pharmacy.web.dto.PurchaseEntryRequest;
import com.pharmacy.web.service.PharmacyWebService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/batches")
public class BatchController {

    private final PharmacyWebService pharmacyWebService;

    public BatchController(PharmacyWebService pharmacyWebService) {
        this.pharmacyWebService = pharmacyWebService;
    }

    @GetMapping
    public ResponseEntity<List<Batch>> getAllBatches() {
        return ResponseEntity.ok(pharmacyWebService.getAllBatches());
    }

    @GetMapping("/medicine/{medicineId}")
    public ResponseEntity<List<Batch>> getBatchesForMedicine(@PathVariable String medicineId) {
        return ResponseEntity.ok(pharmacyWebService.getBatchesForMedicine(medicineId));
    }

    @PostMapping("/purchase")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> recordPurchase(@Valid @RequestBody PurchaseEntryRequest request) {
        Map<String, Object> log = pharmacyWebService.recordPurchase(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(log);
    }
}
