package com.pharmacy.order.service;

import com.pharmacy.order.exception.InsufficientStockException;
import com.pharmacy.order.exception.InventorySyncException;
import com.pharmacy.order.model.Medicine;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service managing medicine stock levels in the warehouse.
 * Demonstrates stock reservation and chained exceptions when warehouse backend sync fails.
 */
public class InventoryService {
    private final Map<String, Medicine> inventory = new ConcurrentHashMap<>();
    private boolean simulateWarehouseSyncFailure = false;

    public void addMedicine(Medicine medicine) {
        inventory.put(medicine.getId(), medicine);
    }

    public Optional<Medicine> getMedicine(String medicineId) {
        return Optional.ofNullable(inventory.get(medicineId));
    }

    public void setSimulateWarehouseSyncFailure(boolean simulate) {
        this.simulateWarehouseSyncFailure = simulate;
    }

    /**
     * Checks stock and reserves items.
     *
     * @param medicineId        the medicine identifier
     * @param requestedQuantity quantity to reserve
     * @throws InsufficientStockException if inventory is insufficient or warehouse sync fails (chained cause)
     */
    public synchronized void checkAndReserveStock(String medicineId, int requestedQuantity)
            throws InsufficientStockException {
        // 1. Check for simulated low-level infrastructure/sync failure (Chained Cause demonstration)
        if (simulateWarehouseSyncFailure || medicineId.startsWith("ERR_SYNC_")) {
            InventorySyncException syncCause = new InventorySyncException(
                    "Database connection timed out after 3000ms while locking SKU " + medicineId,
                    "WAREHOUSE_CLUSTER_NODE_04"
            );
            // Chain the low-level sync error inside a checked InsufficientStockException
            throw new InsufficientStockException(
                    medicineId,
                    inventory.containsKey(medicineId) ? inventory.get(medicineId).getName() : "Unknown SKU",
                    requestedQuantity,
                    0,
                    "Failed to reserve stock for SKU [" + medicineId + "] due to warehouse sync failure",
                    syncCause
            );
        }

        // 2. Lookup medicine in catalog
        Medicine medicine = inventory.get(medicineId);
        if (medicine == null) {
            throw new InsufficientStockException(
                    medicineId,
                    "Non-Existent Medicine",
                    requestedQuantity,
                    0
            );
        }

        // 3. Check available stock
        int currentStock = medicine.getStockQuantity();
        if (requestedQuantity > currentStock) {
            throw new InsufficientStockException(
                    medicine.getId(),
                    medicine.getName(),
                    requestedQuantity,
                    currentStock
            );
        }

        // 4. Atomically reserve
        medicine.reduceStock(requestedQuantity);
    }

    /**
     * Releases reserved stock in case of transaction rollbacks.
     */
    public synchronized void releaseStock(String medicineId, int quantity) {
        Medicine medicine = inventory.get(medicineId);
        if (medicine != null) {
            medicine.addStock(quantity);
        }
    }
}
