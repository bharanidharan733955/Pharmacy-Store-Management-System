package com.pharmacy.web.dto;

import com.pharmacy.core.model.Batch;

import java.util.List;
import java.util.Map;

public class PharmacistDashboardDTO {

    private long totalCatalogMedicines;
    private int lowStockThreshold;
    private List<Map<String, Object>> lowStockMedicines;
    private List<Batch> batchesExpiringWithin30Days;
    private List<Batch> expiredBatchesRequiringAttention;
    private List<Map<String, Object>> recentPurchases;
    private List<String> quickActions;
    private long totalSaleableStock;
    private long totalQuarantinedExpiredStock;

    public PharmacistDashboardDTO() {
    }

    public long getTotalCatalogMedicines() {
        return totalCatalogMedicines;
    }

    public void setTotalCatalogMedicines(long totalCatalogMedicines) {
        this.totalCatalogMedicines = totalCatalogMedicines;
    }

    public int getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(int lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }

    public List<Map<String, Object>> getLowStockMedicines() {
        return lowStockMedicines;
    }

    public void setLowStockMedicines(List<Map<String, Object>> lowStockMedicines) {
        this.lowStockMedicines = lowStockMedicines;
    }

    public List<Batch> getBatchesExpiringWithin30Days() {
        return batchesExpiringWithin30Days;
    }

    public void setBatchesExpiringWithin30Days(List<Batch> batchesExpiringWithin30Days) {
        this.batchesExpiringWithin30Days = batchesExpiringWithin30Days;
    }

    public List<Batch> getExpiredBatchesRequiringAttention() {
        return expiredBatchesRequiringAttention;
    }

    public void setExpiredBatchesRequiringAttention(List<Batch> expiredBatchesRequiringAttention) {
        this.expiredBatchesRequiringAttention = expiredBatchesRequiringAttention;
    }

    public List<Map<String, Object>> getRecentPurchases() {
        return recentPurchases;
    }

    public void setRecentPurchases(List<Map<String, Object>> recentPurchases) {
        this.recentPurchases = recentPurchases;
    }

    public List<String> getQuickActions() {
        return quickActions;
    }

    public void setQuickActions(List<String> quickActions) {
        this.quickActions = quickActions;
    }

    public long getTotalSaleableStock() {
        return totalSaleableStock;
    }

    public void setTotalSaleableStock(long totalSaleableStock) {
        this.totalSaleableStock = totalSaleableStock;
    }

    public long getTotalQuarantinedExpiredStock() {
        return totalQuarantinedExpiredStock;
    }

    public void setTotalQuarantinedExpiredStock(long totalQuarantinedExpiredStock) {
        this.totalQuarantinedExpiredStock = totalQuarantinedExpiredStock;
    }
}
