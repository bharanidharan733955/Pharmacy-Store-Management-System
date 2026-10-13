package com.pharmacy.web.dto;

import com.pharmacy.core.model.Invoice;
import com.pharmacy.core.model.Medicine;

import java.util.List;

public class CashierDashboardDTO {

    private double todaySalesTotal;
    private long todayInvoiceCount;
    private List<Invoice> recentInvoices;
    private List<Medicine> quickSearchMedicines;
    private List<String> quickActions;

    public CashierDashboardDTO() {
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

    public List<Invoice> getRecentInvoices() {
        return recentInvoices;
    }

    public void setRecentInvoices(List<Invoice> recentInvoices) {
        this.recentInvoices = recentInvoices;
    }

    public List<Medicine> getQuickSearchMedicines() {
        return quickSearchMedicines;
    }

    public void setQuickSearchMedicines(List<Medicine> quickSearchMedicines) {
        this.quickSearchMedicines = quickSearchMedicines;
    }

    public List<String> getQuickActions() {
        return quickActions;
    }

    public void setQuickActions(List<String> quickActions) {
        this.quickActions = quickActions;
    }
}
