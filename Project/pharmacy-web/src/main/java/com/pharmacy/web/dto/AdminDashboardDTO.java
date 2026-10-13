package com.pharmacy.web.dto;

import com.pharmacy.core.model.Invoice;

import java.util.List;
import java.util.Map;

public class AdminDashboardDTO {

    private long totalActiveMedicines;
    private long totalSuppliers;
    private long totalAvailableStock;
    private String stockUnitMeasure;
    private long medicinesExpiringWithin30Days;
    private double todaySalesTotal;
    private long todayInvoiceCount;
    private List<Map<String, Object>> recentPurchases;
    private List<Invoice> recentInvoices;
    private List<String> quickActions;

    public AdminDashboardDTO() {
        this.stockUnitMeasure = "Units (Tablets / Bottles)";
    }

    public long getTotalActiveMedicines() {
        return totalActiveMedicines;
    }

    public void setTotalActiveMedicines(long totalActiveMedicines) {
        this.totalActiveMedicines = totalActiveMedicines;
    }

    public long getTotalSuppliers() {
        return totalSuppliers;
    }

    public void setTotalSuppliers(long totalSuppliers) {
        this.totalSuppliers = totalSuppliers;
    }

    public long getTotalAvailableStock() {
        return totalAvailableStock;
    }

    public void setTotalAvailableStock(long totalAvailableStock) {
        this.totalAvailableStock = totalAvailableStock;
    }

    public String getStockUnitMeasure() {
        return stockUnitMeasure;
    }

    public void setStockUnitMeasure(String stockUnitMeasure) {
        this.stockUnitMeasure = stockUnitMeasure;
    }

    public long getMedicinesExpiringWithin30Days() {
        return medicinesExpiringWithin30Days;
    }

    public void setMedicinesExpiringWithin30Days(long medicinesExpiringWithin30Days) {
        this.medicinesExpiringWithin30Days = medicinesExpiringWithin30Days;
    }

    public double getTodaySalesTotal() {
        return todaySalesTotal;
    }

    public void setTodaySalesTotal(double todaySalesTotal) {
        this.todaySalesTotal = todaySalesTotal;
    }

    public long getTodayInvoiceCount() {
        return todayInvoiceCount;
    }

    public void setTodayInvoiceCount(long todayInvoiceCount) {
        this.todayInvoiceCount = todayInvoiceCount;
    }

    public List<Map<String, Object>> getRecentPurchases() {
        return recentPurchases;
    }

    public void setRecentPurchases(List<Map<String, Object>> recentPurchases) {
        this.recentPurchases = recentPurchases;
    }

    public List<Invoice> getRecentInvoices() {
        return recentInvoices;
    }

    public void setRecentInvoices(List<Invoice> recentInvoices) {
        this.recentInvoices = recentInvoices;
    }

    public List<String> getQuickActions() {
        return quickActions;
    }

    public void setQuickActions(List<String> quickActions) {
        this.quickActions = quickActions;
    }
}
