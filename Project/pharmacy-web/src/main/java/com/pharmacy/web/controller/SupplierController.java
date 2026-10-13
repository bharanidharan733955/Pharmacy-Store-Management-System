package com.pharmacy.web.controller;

import com.pharmacy.core.model.Supplier;
import com.pharmacy.web.dto.CreateSupplierRequest;
import com.pharmacy.web.service.PharmacyWebService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final PharmacyWebService pharmacyWebService;

    public SupplierController(PharmacyWebService pharmacyWebService) {
        this.pharmacyWebService = pharmacyWebService;
    }

    @GetMapping
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        return ResponseEntity.ok(pharmacyWebService.getAllSuppliers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable String id) {
        return ResponseEntity.ok(pharmacyWebService.getSupplierById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Supplier> addSupplier(@Valid @RequestBody CreateSupplierRequest request) {
        Supplier created = pharmacyWebService.addSupplier(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
