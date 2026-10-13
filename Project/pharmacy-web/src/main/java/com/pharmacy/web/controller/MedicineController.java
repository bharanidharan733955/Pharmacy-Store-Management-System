package com.pharmacy.web.controller;

import com.pharmacy.core.model.Medicine;
import com.pharmacy.web.dto.CreateMedicineRequest;
import com.pharmacy.web.service.PharmacyWebService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    private final PharmacyWebService pharmacyWebService;

    public MedicineController(PharmacyWebService pharmacyWebService) {
        this.pharmacyWebService = pharmacyWebService;
    }

    @GetMapping
    public ResponseEntity<List<Medicine>> getAllMedicines() {
        return ResponseEntity.ok(pharmacyWebService.getAllMedicines());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Medicine> getMedicineById(@PathVariable String id) {
        return ResponseEntity.ok(pharmacyWebService.getMedicineById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<Medicine> addMedicine(@Valid @RequestBody CreateMedicineRequest request) {
        Medicine created = pharmacyWebService.addMedicine(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
