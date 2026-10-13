package com.pharmacy.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

public class CreateMedicineRequest {

    private String id;

    @NotBlank(message = "Medicine name is required")
    private String name;

    @NotBlank(message = "Category is required")
    private String category;

    @DecimalMin(value = "0.0", inclusive = true, message = "Price cannot be negative")
    private double unitPrice;

    private boolean requiresPrescription;

    public CreateMedicineRequest() {
    }

    public CreateMedicineRequest(String id, String name, String category, double unitPrice, boolean requiresPrescription) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.unitPrice = unitPrice;
        this.requiresPrescription = requiresPrescription;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public boolean isRequiresPrescription() {
        return requiresPrescription;
    }

    public void setRequiresPrescription(boolean requiresPrescription) {
        this.requiresPrescription = requiresPrescription;
    }
}
