package com.pharmacy.core.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Medicine Model Tests - Chained Constructors, Static Counter & Validation")
class MedicineTest {

    @BeforeEach
    void setUp() {
        Medicine.resetMedicineCounter();
    }

    @Test
    @DisplayName("Test Constructor 1 (Chained) - Default Category and Price")
    void testConstructor1() {
        Medicine med = new Medicine("Aspirin");

        assertEquals("MED-0001", med.getId());
        assertEquals("Aspirin", med.getName());
        assertEquals("GENERAL", med.getCategory());
        assertEquals(10.00, med.getUnitPrice(), 0.001);
        assertFalse(med.isRequiresPrescription());
        assertEquals(1, Medicine.getMedicineCounter());
    }

    @Test
    @DisplayName("Test Constructor 2 (Chained) - Default Prescription Flag")
    void testConstructor2() {
        Medicine med = new Medicine("Ibuprofen", "Painkiller", 25.50);

        assertEquals("MED-0001", med.getId());
        assertEquals("Ibuprofen", med.getName());
        assertEquals("Painkiller", med.getCategory());
        assertEquals(25.50, med.getUnitPrice(), 0.001);
        assertFalse(med.isRequiresPrescription());
    }

    @Test
    @DisplayName("Test Constructor 3 (Master) - Custom ID and Rx Requirement")
    void testConstructor3Master() {
        Medicine med = new Medicine("M101", "Amoxicillin", "Antibiotic", 45.00, true);

        assertEquals("M101", med.getId());
        assertEquals("Amoxicillin", med.getName());
        assertEquals("Antibiotic", med.getCategory());
        assertEquals(45.00, med.getUnitPrice(), 0.001);
        assertTrue(med.isRequiresPrescription());
    }

    @Test
    @DisplayName("Test Static Counter Increment")
    void testStaticCounter() {
        new Medicine("Med 1");
        new Medicine("Med 2");
        new Medicine("Med 3");

        assertEquals(3, Medicine.getMedicineCounter());
    }

    @Test
    @DisplayName("Test Validation Constraints")
    void testValidationConstraints() {
        // Invalid name
        assertThrows(IllegalArgumentException.class, () -> new Medicine(""));
        assertThrows(IllegalArgumentException.class, () -> new Medicine(null));

        // Invalid unit price
        assertThrows(IllegalArgumentException.class, () -> new Medicine("Test", "Category", -5.00));
    }

    @Test
    @DisplayName("Test Equals and HashCode Contract")
    void testEqualsAndHashCode() {
        Medicine med1 = new Medicine("M101", "Paracetamol", "Analgesic", 15.00, false);
        Medicine med2 = new Medicine("M101", "Paracetamol Extra", "Analgesic", 20.00, false);
        Medicine med3 = new Medicine("M102", "Paracetamol", "Analgesic", 15.00, false);

        assertEquals(med1, med2);
        assertEquals(med1.hashCode(), med2.hashCode());
        assertNotEquals(med1, med3);
    }
}
