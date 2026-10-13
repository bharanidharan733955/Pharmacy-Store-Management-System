package com.pharmacy.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class CreateInvoiceRequest {

    @NotBlank(message = "Medicine ID is required")
    private String medicineId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    private String prescriptionReference;

    private String batchNumber;

    public CreateInvoiceRequest() {
    }

    public CreateInvoiceRequest(String medicineId, int quantity, String prescriptionReference, String batchNumber) {
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.prescriptionReference = prescriptionReference;
        this.batchNumber = batchNumber;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getPrescriptionReference() {
        return prescriptionReference;
    }

    public void setPrescriptionReference(String prescriptionReference) {
        this.prescriptionReference = prescriptionReference;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }
}
