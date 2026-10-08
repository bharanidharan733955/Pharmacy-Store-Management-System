package com.pharmacy.core.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Supplier Model Tests - Chained Constructors, Static Counter & Validation")
class SupplierTest {

    @BeforeEach
    void setUp() {
        Supplier.resetSupplierCounter();
    }

    @Test
    @DisplayName("Test Constructor 1 (Chained) - Default Contact Person and Phone")
    void testConstructor1() {
        Supplier sup = new Supplier("PharmaCorp");

        assertEquals("SUP-0001", sup.getId());
        assertEquals("PharmaCorp", sup.getName());
        assertEquals("N/A", sup.getContactPerson());
        assertEquals("N/A", sup.getPhone());
        assertEquals(1, Supplier.getSupplierCounter());
    }

    @Test
    @DisplayName("Test Constructor 2 (Chained) - Custom Details and Auto ID")
    void testConstructor2() {
        Supplier sup = new Supplier("Apex Distributors", "John Smith", "9876543210");

        assertEquals("SUP-0001", sup.getId());
        assertEquals("Apex Distributors", sup.getName());
        assertEquals("John Smith", sup.getContactPerson());
        assertEquals("9876543210", sup.getPhone());
    }

    @Test
    @DisplayName("Test Constructor 3 (Master) - Custom Supplier ID")
    void testConstructor3Master() {
        Supplier sup = new Supplier("S101", "Global Health Ltd", "Jane Doe", "1122334455");

        assertEquals("S101", sup.getId());
        assertEquals("Global Health Ltd", sup.getName());
    }

    @Test
    @DisplayName("Test Supplier Validation Constraints")
    void testValidationConstraints() {
        assertThrows(IllegalArgumentException.class, () -> new Supplier(""));
        assertThrows(IllegalArgumentException.class, () -> new Supplier("Name", "", "Phone"));
        assertThrows(IllegalArgumentException.class, () -> new Supplier("Name", "Contact", null));
    }

    @Test
    @DisplayName("Test Equals and HashCode Contract")
    void testEqualsAndHashCode() {
        Supplier sup1 = new Supplier("S101", "PharmaCorp", "Contact 1", "123");
        Supplier sup2 = new Supplier("S101", "PharmaCorp Alternate", "Contact 2", "456");
        Supplier sup3 = new Supplier("S102", "PharmaCorp", "Contact 1", "123");

        assertEquals(sup1, sup2);
        assertEquals(sup1.hashCode(), sup2.hashCode());
        assertNotEquals(sup1, sup3);
    }
}
