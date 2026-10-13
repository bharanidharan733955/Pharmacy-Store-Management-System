package com.pharmacy.web.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateSupplierRequest {

    private String id;

    @NotBlank(message = "Supplier name is required")
    private String name;

    @NotBlank(message = "Contact person is required")
    private String contactPerson;

    @NotBlank(message = "Phone number is required")
    private String phone;

    public CreateSupplierRequest() {
    }

    public CreateSupplierRequest(String id, String name, String contactPerson, String phone) {
        this.id = id;
        this.name = name;
        this.contactPerson = contactPerson;
        this.phone = phone;
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

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
