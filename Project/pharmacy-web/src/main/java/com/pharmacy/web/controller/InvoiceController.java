package com.pharmacy.web.controller;

import com.pharmacy.core.model.Invoice;
import com.pharmacy.web.dto.CreateInvoiceRequest;
import com.pharmacy.web.service.PharmacyWebService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final PharmacyWebService pharmacyWebService;

    public InvoiceController(PharmacyWebService pharmacyWebService) {
        this.pharmacyWebService = pharmacyWebService;
    }

    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        return ResponseEntity.ok(pharmacyWebService.getAllInvoices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable String id) {
        return ResponseEntity.ok(pharmacyWebService.getInvoiceById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CASHIER', 'PHARMACIST', 'ADMIN')")
    public ResponseEntity<Invoice> createInvoice(@Valid @RequestBody CreateInvoiceRequest request) {
        Invoice invoice = pharmacyWebService.createInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(invoice);
    }
}
