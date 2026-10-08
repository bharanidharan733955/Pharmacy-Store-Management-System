package com.pharmacy.core.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Batch Model Tests - Chained Constructors, Expiry & Stock Validation")
class BatchTest {

    @BeforeEach
    void setUp() {
        Batch.resetBatchCounter();
    }

    @Test
    @DisplayName("Test Constructor 1 (Chained) - Default Expiry Date (+1 Year)")
    void testConstructor1() {
        Batch batch = new Batch("M101", 100);

        assertEquals("BAT-0001", batch.getBatchNumber());
        assertEquals("M101", batch.getMedicineId());
        assertEquals(100, batch.getQuantity());
        assertEquals(LocalDate.now().plusYears(1), batch.getExpiryDate());
        assertEquals(1, Batch.getBatchCounter());
    }

    @Test
    @DisplayName("Test Constructor 2 (Chained) - Custom Expiry Date and Auto Batch Number")
    void testConstructor2() {
        LocalDate futureDate = LocalDate.of(2026, 12, 31);
        Batch batch = new Batch("M102", futureDate, 50);

        assertEquals("BAT-0001", batch.getBatchNumber());
        assertEquals("M102", batch.getMedicineId());
        assertEquals(futureDate, batch.getExpiryDate());
        assertEquals(50, batch.getQuantity());
    }

    @Test
    @DisplayName("Test Constructor 3 (Master) - Custom Batch Number")
    void testConstructor3Master() {
        LocalDate date = LocalDate.of(2025, 6, 30);
        Batch batch = new Batch("B2025-X", "M101", date, 75);

        assertEquals("B2025-X", batch.getBatchNumber());
        assertEquals("M101", batch.getMedicineId());
        assertEquals(date, batch.getExpiryDate());
        assertEquals(75, batch.getQuantity());
    }

    @Test
    @DisplayName("Test Reduce and Add Quantity Validation")
    void testQuantityModifications() {
        Batch batch = new Batch("M101", 50);

        batch.reduceQuantity(20);
        assertEquals(30, batch.getQuantity());

        batch.addQuantity(15);
        assertEquals(45, batch.getQuantity());

        assertThrows(IllegalArgumentException.class, () -> batch.reduceQuantity(50));
        assertThrows(IllegalArgumentException.class, () -> batch.reduceQuantity(-5));
        assertThrows(IllegalArgumentException.class, () -> batch.addQuantity(-10));
    }

    @Test
    @DisplayName("Test Expiry Detection")
    void testIsExpired() {
        Batch expiredBatch = new Batch("B-EXP", "M101", LocalDate.now().minusDays(1), 10);
        Batch activeBatch = new Batch("B-ACT", "M101", LocalDate.now().plusDays(10), 10);

        assertTrue(expiredBatch.isExpired());
        assertFalse(activeBatch.isExpired());
    }

    @Test
    @DisplayName("Test Equals and HashCode Contract")
    void testEqualsAndHashCode() {
        Batch b1 = new Batch("B100", "M101", LocalDate.now().plusYears(1), 50);
        Batch b2 = new Batch("B100", "M102", LocalDate.now().plusYears(2), 100);
        Batch b3 = new Batch("B101", "M101", LocalDate.now().plusYears(1), 50);

        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());
        assertNotEquals(b1, b3);
    }
}
