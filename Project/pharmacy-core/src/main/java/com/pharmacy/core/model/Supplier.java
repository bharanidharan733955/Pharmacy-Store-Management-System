package com.pharmacy.core.model;

public class Supplier {
    private final String id;
    private final String name;
    private final String contactPerson;
    private final String phone;

    public Supplier(String id, String name, String contactPerson, String phone) {
        this.id = id;
        this.name = name;
        this.contactPerson = contactPerson;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-22s | Contact: %-15s | Phone: %s", id, name, contactPerson, phone);
    }
}
