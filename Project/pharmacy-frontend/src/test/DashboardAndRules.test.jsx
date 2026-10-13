import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import App from '../App';
import { api, ApiError } from '../services/api';
import { ErrorBoundary } from '../components/common/ErrorBoundary';

describe('Frontend Role Dashboards, Business Rules & Navigation Recovery Tests', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.restoreAllMocks();
  });

  // Helper to set authenticated user in localStorage
  const setAuthUser = (username, role, fullName) => {
    localStorage.setItem('pharmacy_token', 'mock-valid-jwt-token');
    localStorage.setItem(
      'pharmacy_user',
      JSON.stringify({
        username,
        role,
        fullName,
        permissions: ['ALL'],
      })
    );
  };

  it('Test 1: ADMIN sees the Admin dashboard and permitted menu', async () => {
    setAuthUser('admin', 'ADMIN', 'Dr. Alice Morgan');

    vi.spyOn(api, 'getAdminDashboard').mockResolvedValue({
      totalActiveMedicines: 10,
      totalSuppliers: 5,
      totalAvailableStock: 500,
      stockUnitMeasure: 'Units (Tablets / Bottles)',
      medicinesExpiringWithin30Days: 2,
      todaySalesTotal: 1550.0,
      todayInvoiceCount: 8,
      recentPurchases: [],
      recentInvoices: [],
      quickActions: ['Manage Medicines', 'Manage Suppliers'],
    });

    render(<App />);

    // Admin Dashboard is rendered
    await waitFor(() => {
      expect(screen.getByTestId('admin-dashboard')).toBeInTheDocument();
    });

    // Permitted Admin Menu items
    expect(screen.getByTestId('menu-item-dashboard')).toBeInTheDocument();
    expect(screen.getByTestId('menu-item-medicines')).toBeInTheDocument();
    expect(screen.getByTestId('menu-item-suppliers')).toBeInTheDocument();
    expect(screen.getByTestId('menu-item-purchases')).toBeInTheDocument();
    expect(screen.getByTestId('menu-item-reports')).toBeInTheDocument();

    // Verify stock measure unit is clearly defined
    expect(screen.getByText(/Units \(Tablets \/ Bottles\)/i)).toBeInTheDocument();
  });

  it('Test 2: PHARMACIST sees the Pharmacist dashboard and permitted menu', async () => {
    setAuthUser('pharmacist', 'PHARMACIST', 'John Doe');

    vi.spyOn(api, 'getPharmacistDashboard').mockResolvedValue({
      totalCatalogMedicines: 12,
      lowStockThreshold: 20,
      lowStockMedicines: [],
      batchesExpiringWithin30Days: [],
      expiredBatchesRequiringAttention: [],
      totalSaleableStock: 450,
      totalQuarantinedExpiredStock: 30,
      recentPurchases: [],
      quickActions: ['Record Incoming Purchase'],
    });

    render(<App />);

    await waitFor(() => {
      expect(screen.getByTestId('pharmacist-dashboard')).toBeInTheDocument();
    });

    // Pharmacist permitted menu items
    expect(screen.getByTestId('menu-item-dashboard')).toBeInTheDocument();
    expect(screen.getByTestId('menu-item-medicines')).toBeInTheDocument();
    expect(screen.getByTestId('menu-item-purchases')).toBeInTheDocument();
    expect(screen.getByTestId('menu-item-expiry')).toBeInTheDocument();

    // Pharmacist must NOT see admin-only suppliers and reports menus
    expect(screen.queryByTestId('menu-item-suppliers')).not.toBeInTheDocument();
    expect(screen.queryByTestId('menu-item-reports')).not.toBeInTheDocument();
  });

  it('Test 3: CASHIER sees the Cashier dashboard and permitted menu', async () => {
    setAuthUser('cashier', 'CASHIER', 'Emily Watson');

    vi.spyOn(api, 'getCashierDashboard').mockResolvedValue({
      todaySalesTotal: 450.0,
      todayInvoiceCount: 3,
      recentInvoices: [],
      quickSearchMedicines: [
        { id: 'M101', name: 'Paracetamol 500mg', category: 'Analgesic', unitPrice: 15.5, requiresPrescription: false },
      ],
      quickActions: ['Start New Bill'],
    });

    render(<App />);

    await waitFor(() => {
      expect(screen.getByTestId('cashier-dashboard')).toBeInTheDocument();
    });

    // Cashier permitted menus
    expect(screen.getByTestId('menu-item-dashboard')).toBeInTheDocument();
    expect(screen.getByTestId('menu-item-medicines')).toBeInTheDocument();
    expect(screen.getByTestId('menu-item-billing')).toBeInTheDocument();
    expect(screen.getByTestId('menu-item-invoices')).toBeInTheDocument();

    // Cashier must NOT see admin-only suppliers, reports, or purchases
    expect(screen.queryByTestId('menu-item-suppliers')).not.toBeInTheDocument();
    expect(screen.queryByTestId('menu-item-purchases')).not.toBeInTheDocument();
    expect(screen.queryByTestId('menu-item-reports')).not.toBeInTheDocument();
  });

  it('Test 4: An expired-stock error is shown without hiding the menu', async () => {
    setAuthUser('cashier', 'CASHIER', 'Emily Watson');

    vi.spyOn(api, 'getCashierDashboard').mockResolvedValue({
      todaySalesTotal: 0,
      todayInvoiceCount: 0,
      recentInvoices: [],
      quickSearchMedicines: [
        { id: 'M103', name: 'Cetirizine 10mg', category: 'Antihistamine', unitPrice: 12.0, requiresPrescription: false },
      ],
      quickActions: [],
    });

    // Simulate backend throwing EXPIRED_BATCH error on billing attempt
    vi.spyOn(api, 'createInvoice').mockRejectedValue(
      new ApiError(
        409,
        'EXPIRED_BATCH',
        "Cannot dispense expired medicine. Batch 'B2024-EXP' for medicine 'M103' expired on 2026-10-02."
      )
    );

    render(<App />);

    await waitFor(() => {
      expect(screen.getByTestId('cashier-dashboard')).toBeInTheDocument();
    });

    // Open Billing Modal
    fireEvent.click(screen.getByTestId('cashier-start-bill-btn'));
    expect(screen.getByTestId('billing-modal')).toBeInTheDocument();

    // Set batch to expired batch and submit
    fireEvent.change(screen.getByTestId('billing-batch-input'), {
      target: { value: 'B2024-EXP' },
    });
    fireEvent.click(screen.getByTestId('billing-submit-btn'));

    // Wait for business rule error banner
    await waitFor(() => {
      const errorBanner = screen.getByTestId('billing-error-banner');
      expect(errorBanner).toBeInTheDocument();
      expect(errorBanner).toHaveTextContent(/Cannot dispense expired medicine/i);
      expect(errorBanner).toHaveTextContent('EXPIRED_BATCH');
    });

    // CRITICAL: Sidebar and navigation must remain visible and fully functional!
    expect(screen.getByTestId('sidebar')).toBeInTheDocument();
    expect(screen.getByTestId('menu-item-dashboard')).toBeInTheDocument();
    expect(screen.getByTestId('menu-item-billing')).toBeInTheDocument();
  });

  it('Test 5: A missing-prescription error is shown without hiding the menu', async () => {
    setAuthUser('cashier', 'CASHIER', 'Emily Watson');

    vi.spyOn(api, 'getCashierDashboard').mockResolvedValue({
      todaySalesTotal: 0,
      todayInvoiceCount: 0,
      recentInvoices: [],
      quickSearchMedicines: [
        { id: 'M102', name: 'Amoxicillin 250mg', category: 'Antibiotic', unitPrice: 45.0, requiresPrescription: true },
      ],
      quickActions: [],
    });

    // Simulate backend rejecting sale due to missing prescription reference
    vi.spyOn(api, 'createInvoice').mockRejectedValue(
      new ApiError(
        409,
        'PRESCRIPTION_REFERENCE_REQUIRED',
        "Medicine 'Amoxicillin 250mg' (M102) is a scheduled drug requiring a valid doctor prescription reference."
      )
    );

    render(<App />);

    await waitFor(() => {
      expect(screen.getByTestId('cashier-dashboard')).toBeInTheDocument();
    });

    // Click Bill Item on M102
    fireEvent.click(screen.getByTestId('bill-med-btn-M102'));
    expect(screen.getByTestId('billing-modal')).toBeInTheDocument();

    // Submit without prescription reference
    fireEvent.click(screen.getByTestId('billing-submit-btn'));

    // Error must appear in both banner and inline validation
    await waitFor(() => {
      const errorBanner = screen.getByTestId('billing-error-banner');
      expect(errorBanner).toBeInTheDocument();
      expect(errorBanner).toHaveTextContent('PRESCRIPTION_REFERENCE_REQUIRED');
      expect(screen.getByTestId('prescription-inline-error')).toBeInTheDocument();
    });

    // CRITICAL: Sidebar and navigation remain visible!
    expect(screen.getByTestId('sidebar')).toBeInTheDocument();
    expect(screen.getByTestId('menu-item-invoices')).toBeInTheDocument();
  });

  it('Test 6: A failed API request clears the loading state and allows retry', async () => {
    setAuthUser('admin', 'ADMIN', 'Dr. Alice Morgan');

    const apiSpy = vi
      .spyOn(api, 'getAdminDashboard')
      .mockRejectedValueOnce(new ApiError(500, 'INTERNAL_SERVER_ERROR', 'Database connection timeout.'))
      .mockResolvedValueOnce({
        totalActiveMedicines: 8,
        totalSuppliers: 3,
        totalAvailableStock: 300,
        stockUnitMeasure: 'Units (Tablets / Bottles)',
        medicinesExpiringWithin30Days: 1,
        todaySalesTotal: 800.0,
        todayInvoiceCount: 4,
        recentPurchases: [],
        recentInvoices: [],
        quickActions: [],
      });

    render(<App />);

    // 1. Loading state should appear and then clear
    await waitFor(() => {
      expect(screen.queryByTestId('loading-spinner')).not.toBeInTheDocument();
      expect(screen.getByTestId('error-banner')).toBeInTheDocument();
    });

    // 2. Refresh button is re-enabled (not permanently loading or disabled)
    const refreshBtn = screen.getByTestId('admin-refresh-btn');
    expect(refreshBtn).not.toBeDisabled();

    // 3. Retry option works
    const retryBtn = screen.getByTestId('error-retry-btn');
    fireEvent.click(retryBtn);

    // 4. Successful recovery after retry
    await waitFor(() => {
      expect(screen.queryByTestId('error-banner')).not.toBeInTheDocument();
      expect(screen.getByText('300 Units')).toBeInTheDocument();
    });
  });

  it('Test 7: A dashboard widget error does not crash the complete dashboard', () => {
    const ProblematicWidget = () => {
      throw new Error('Crash inside isolated chart widget');
    };

    render(
      <div>
        <div data-testid="dashboard-header">Main Dashboard Header</div>
        <ErrorBoundary title="Widget Error">
          <ProblematicWidget />
        </ErrorBoundary>
        <div data-testid="dashboard-footer">Operational Navigation & Controls</div>
      </div>
    );

    // The header and footer outside the error boundary remain fully operational!
    expect(screen.getByTestId('dashboard-header')).toBeInTheDocument();
    expect(screen.getByTestId('dashboard-footer')).toBeInTheDocument();
    expect(screen.getByTestId('error-boundary-fallback')).toBeInTheDocument();
    expect(screen.getByText(/Crash inside isolated chart widget/i)).toBeInTheDocument();
  });
});
