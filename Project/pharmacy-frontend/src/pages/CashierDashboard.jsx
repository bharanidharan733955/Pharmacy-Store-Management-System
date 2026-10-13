import React, { useState, useEffect } from 'react';
import {
  IndianRupee,
  FileCheck2,
  ShoppingCart,
  Search,
  AlertCircle,
  FileText,
  RotateCcw,
  CheckCircle2,
  Plus
} from 'lucide-react';
import { api } from '../services/api';
import { StatCard } from '../components/common/StatCard';
import { ErrorBanner } from '../components/common/ErrorBanner';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { EmptyState } from '../components/common/EmptyState';
import { ErrorBoundary } from '../components/common/ErrorBoundary';
import { useToast } from '../context/ToastContext';

export function CashierDashboard({ onNavigate }) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const { addToast } = useToast();

  // POS Billing Modal State
  const [showBillModal, setShowBillModal] = useState(false);
  const [billForm, setBillForm] = useState({
    medicineId: 'M101',
    quantity: 1,
    prescriptionReference: '',
    batchNumber: '',
  });
  const [billingSubmitting, setBillingSubmitting] = useState(false);
  const [billingError, setBillingError] = useState(null);
  const [lastCreatedInvoice, setLastCreatedInvoice] = useState(null);

  const fetchDashboardData = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await api.getCashierDashboard();
      setData(res);
    } catch (err) {
      setError(err);
    } finally {
      // Guaranteed loading cleanup
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const openBillForMedicine = (med) => {
    setBillingError(null);
    setLastCreatedInvoice(null);
    setBillForm({
      medicineId: med.id,
      quantity: 1,
      prescriptionReference: '',
      batchNumber: '',
    });
    setShowBillModal(true);
  };

  const selectedMed = data?.quickSearchMedicines?.find((m) => m.id === billForm.medicineId);

  const handleCreateInvoice = async (e) => {
    if (e && e.preventDefault) e.preventDefault();
    console.log('[DEBUG] handleCreateInvoice CALLED with:', billForm);
    setBillingSubmitting(true);
    setBillingError(null);
    setLastCreatedInvoice(null);

    try {
      const invoice = await api.createInvoice({
        medicineId: billForm.medicineId,
        quantity: parseInt(billForm.quantity) || 1,
        prescriptionReference: billForm.prescriptionReference,
        batchNumber: billForm.batchNumber || null,
      });

      setLastCreatedInvoice(invoice);
      addToast({
        type: 'success',
        message: `Sale successful! Invoice #${invoice.invoiceId} generated for ₹${Number(invoice.totalAmount).toFixed(2)}.`,
      });

      // Refresh dashboard stats
      fetchDashboardData();
    } catch (err) {
      console.error('[DEBUG CATCH ERROR]:', err);
      setBillingError(err);
      addToast({
        type: 'error',
        errorCode: err.errorCode,
        message: err.message || 'Billing transaction failed.',
      });
    } finally {
      // Guaranteed loading state cleanup
      setBillingSubmitting(false);
    }
  };

  // Filter medicines by search term
  const filteredMedicines = (data?.quickSearchMedicines || []).filter((m) =>
    m.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    m.id.toLowerCase().includes(searchTerm.toLowerCase()) ||
    m.category.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div data-testid="cashier-dashboard" className="space-y-6">
      {/* Title & Quick Action */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
        <div>
          <h2 className="text-xl sm:text-2xl font-bold text-slate-900 tracking-tight">
            Cashier POS Counter & Billing
          </h2>
          <p className="text-sm text-slate-500">
            Process prescription and general medicine sales using automated FEFO allocation.
          </p>
        </div>
        <div className="flex items-center gap-2">
          <button
            onClick={() => {
              setBillingError(null);
              setLastCreatedInvoice(null);
              setShowBillModal(true);
            }}
            data-testid="cashier-start-bill-btn"
            className="inline-flex items-center gap-2 px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-xl shadow-xs transition"
          >
            <Plus className="w-4 h-4" />
            Start New Bill
          </button>
          <button
            onClick={fetchDashboardData}
            disabled={loading}
            data-testid="cashier-refresh-btn"
            className="p-2 bg-white border border-slate-200 hover:bg-slate-50 text-slate-700 rounded-xl shadow-xs transition disabled:opacity-50"
            title="Refresh dashboard"
          >
            <RotateCcw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
          </button>
        </div>
      </div>

      {/* Global Error Banner */}
      {error && (
        <ErrorBanner
          title="Cashier Dashboard Unavailable"
          message={error.message || 'Unable to retrieve cashier sales metrics.'}
          errorCode={error.errorCode}
          correlationId={error.correlationId}
          onRetry={fetchDashboardData}
        />
      )}

      {/* 2 Primary Metric Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <StatCard
          title="Today's Sales Total"
          value={loading ? '...' : `₹${Number(data?.todaySalesTotal || 0).toFixed(2)}`}
          subtitle="Cumulative daily revenue"
          icon={IndianRupee}
          color="emerald"
          loading={loading}
          error={error}
          onRetry={fetchDashboardData}
        />
        <StatCard
          title="Invoices Generated Today"
          value={loading ? '...' : (data?.todayInvoiceCount ?? '0')}
          subtitle="Customer receipts issued"
          icon={FileCheck2}
          color="blue"
          loading={loading}
          error={error}
          onRetry={fetchDashboardData}
        />
      </div>

      {/* Medicine Search & Fast Dispense Table */}
      <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs space-y-4">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
          <div>
            <h3 className="text-sm font-bold text-slate-800 uppercase tracking-wider">
              Quick Medicine Search & Dispense
            </h3>
            <p className="text-xs text-slate-500">
              Select an item to initiate billing with automatic batch verification.
            </p>
          </div>
          <div className="relative max-w-xs w-full">
            <Search className="w-4 h-4 text-slate-400 absolute left-3 top-2.5" />
            <input
              type="text"
              data-testid="medicine-search-input"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder="Search by name, ID, or category..."
              className="w-full pl-9 pr-3 py-1.5 text-xs bg-slate-50 border border-slate-200 rounded-xl focus:bg-white focus:ring-1 focus:ring-emerald-500 focus:outline-none"
            />
          </div>
        </div>

        <ErrorBoundary title="Medicine Search Error">
          {loading ? (
            <LoadingSpinner text="Searching catalogue..." />
          ) : filteredMedicines.length === 0 ? (
            <EmptyState title="No medicines match search" message="Try searching with a different name or category." />
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs" data-testid="medicine-table">
                <thead className="border-b border-slate-100 text-slate-400 font-medium">
                  <tr>
                    <th className="pb-2">Code</th>
                    <th className="pb-2">Name</th>
                    <th className="pb-2">Category</th>
                    <th className="pb-2 text-right">Unit Price</th>
                    <th className="pb-2 text-center">Rx Policy</th>
                    <th className="pb-2 text-right">Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-50">
                  {filteredMedicines.map((med) => (
                    <tr key={med.id} className="hover:bg-slate-50/70">
                      <td className="py-2.5 font-mono font-medium text-slate-600">{med.id}</td>
                      <td className="py-2.5 font-semibold text-slate-800">{med.name}</td>
                      <td className="py-2.5 text-slate-500">{med.category}</td>
                      <td className="py-2.5 text-right font-bold text-slate-900">
                        ₹{Number(med.unitPrice).toFixed(2)}
                      </td>
                      <td className="py-2.5 text-center">
                        {med.requiresPrescription ? (
                          <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-amber-100 text-amber-800 border border-amber-200">
                            Prescription Required
                          </span>
                        ) : (
                          <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-emerald-100 text-emerald-800">
                            OTC General
                          </span>
                        )}
                      </td>
                      <td className="py-2.5 text-right">
                        <button
                          onClick={() => openBillForMedicine(med)}
                          data-testid={`bill-med-btn-${med.id}`}
                          className="px-3 py-1 bg-emerald-50 hover:bg-emerald-600 text-emerald-700 hover:text-white rounded-lg font-bold text-xs transition"
                        >
                          Bill Item
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </ErrorBoundary>
      </div>

      {/* Recent Invoices Log */}
      <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs">
        <h3 className="text-sm font-bold text-slate-800 uppercase tracking-wider mb-3 flex items-center gap-2">
          <FileCheck2 className="w-4 h-4 text-blue-600" />
          Recent Counter Invoices
        </h3>

        <ErrorBoundary title="Recent Invoices Error">
          {loading ? (
            <LoadingSpinner text="Loading invoices..." />
          ) : !data?.recentInvoices || data.recentInvoices.length === 0 ? (
            <EmptyState title="No invoices yet" message="Invoices will appear here as you bill sales." />
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs">
                <thead className="border-b border-slate-100 text-slate-400 font-medium">
                  <tr>
                    <th className="pb-2">Invoice #</th>
                    <th className="pb-2">Medicine Sold</th>
                    <th className="pb-2 text-right">Quantity</th>
                    <th className="pb-2 text-right">Amount</th>
                    <th className="pb-2 text-right">Timestamp</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-50">
                  {data.recentInvoices.map((inv) => (
                    <tr key={inv.invoiceId} className="hover:bg-slate-50/70">
                      <td className="py-2.5 font-mono font-semibold text-blue-600">{inv.invoiceId}</td>
                      <td className="py-2.5 font-medium text-slate-800">{inv.medicineName}</td>
                      <td className="py-2.5 text-right font-medium text-slate-600">{inv.quantity}</td>
                      <td className="py-2.5 text-right font-bold text-slate-900">
                        ₹{Number(inv.totalAmount).toFixed(2)}
                      </td>
                      <td className="py-2.5 text-right text-slate-500 font-mono text-[11px]">{inv.timestamp}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </ErrorBoundary>
      </div>

      {/* POS Billing Modal */}
      {showBillModal && (
        <div data-testid="billing-modal" className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-xl border border-slate-200">
            <div className="flex items-center justify-between mb-2">
              <h3 className="text-base font-bold text-slate-900">Create POS Invoice</h3>
              <span className="text-xs font-mono px-2 py-0.5 rounded bg-slate-100 text-slate-700">
                FEFO Enabled
              </span>
            </div>
            <p className="text-xs text-slate-500 mb-4">
              Select items, apply prescription validation, and verify non-expired stock.
            </p>

            {/* Error Feedback Banner */}
            {billingError && (
              <div data-testid="billing-error-banner" className="mb-4">
                <ErrorBanner
                  title="Sale Transaction Rejected"
                  message={billingError.message}
                  errorCode={billingError.errorCode}
                  correlationId={billingError.correlationId}
                />
              </div>
            )}

            {/* Success Invoice Receipt preview */}
            {lastCreatedInvoice && (
              <div data-testid="billing-success-card" className="p-3.5 mb-4 bg-emerald-50 border border-emerald-200 rounded-xl text-xs space-y-1">
                <div className="flex items-center gap-1.5 text-emerald-800 font-bold">
                  <CheckCircle2 className="w-4 h-4 text-emerald-600" />
                  Invoice Persisted Successfully!
                </div>
                <div className="text-slate-600">Invoice ID: <span className="font-mono font-bold text-slate-800">{lastCreatedInvoice.invoiceId}</span></div>
                <div className="text-slate-600">Item: <span className="font-semibold text-slate-800">{lastCreatedInvoice.medicineName}</span> (Qty: {lastCreatedInvoice.quantity})</div>
                <div className="text-slate-600">Total Billed: <span className="font-bold text-emerald-700">₹{Number(lastCreatedInvoice.totalAmount).toFixed(2)}</span></div>
              </div>
            )}

            <form onSubmit={handleCreateInvoice} className="space-y-3.5 text-xs">
              <div>
                <label className="font-semibold text-slate-700 block mb-1">Selected Medicine</label>
                <select
                  data-testid="billing-medicine-select"
                  value={billForm.medicineId}
                  onChange={(e) => {
                    setBillForm({ ...billForm, medicineId: e.target.value });
                    setBillingError(null);
                  }}
                  className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:ring-1 focus:ring-emerald-500 focus:outline-none"
                >
                  {(data?.quickSearchMedicines || []).map((m) => (
                    <option key={m.id} value={m.id}>
                      {m.name} ({m.id}) - ₹{m.unitPrice} {m.requiresPrescription ? '[Rx Required]' : ''}
                    </option>
                  ))}
                </select>
              </div>

              {/* Business Rule 2: Prescription Reference Input */}
              {selectedMed?.requiresPrescription && (
                <div data-testid="prescription-field-container" className="p-3 bg-amber-50/80 border border-amber-200 rounded-xl space-y-1.5">
                  <div className="flex items-center gap-1.5 text-amber-800 font-bold">
                    <FileText className="w-4 h-4 text-amber-600" />
                    Doctor Prescription Reference Required
                  </div>
                  <p className="text-[11px] text-amber-700">
                    Mandatory: This is a scheduled pharmaceutical product. Sales without a prescription reference will be rejected.
                  </p>
                  <input
                    type="text"
                    data-testid="prescription-ref-input"
                    value={billForm.prescriptionReference}
                    onChange={(e) => {
                      setBillForm({ ...billForm, prescriptionReference: e.target.value });
                      setBillingError(null);
                    }}
                    placeholder="Enter Doctor / Rx Reference (e.g. RX-2026-904)..."
                    className="w-full px-3 py-1.5 bg-white border border-amber-300 rounded-lg text-slate-800 placeholder-slate-400 focus:outline-none focus:ring-1 focus:ring-amber-500"
                  />
                  {billingError?.errorCode === 'PRESCRIPTION_REFERENCE_REQUIRED' && (
                    <p data-testid="prescription-inline-error" className="text-[11px] text-rose-600 font-bold flex items-center gap-1">
                      <AlertCircle className="w-3.5 h-3.5 shrink-0" />
                      {billingError.message}
                    </p>
                  )}
                </div>
              )}

              {/* Quantity */}
              <div>
                <label className="font-semibold text-slate-700 block mb-1">Quantity to Dispense</label>
                <input
                  type="number"
                  data-testid="billing-quantity-input"
                  min="1"
                  required
                  value={billForm.quantity}
                  onChange={(e) => setBillForm({ ...billForm, quantity: e.target.value })}
                  className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:ring-1 focus:ring-emerald-500 focus:outline-none"
                />
              </div>

              {/* Optional Specific Batch (For testing BR-1 expired batch rejection) */}
              <div>
                <label className="font-semibold text-slate-700 block mb-1">
                  Specific Batch Override <span className="font-normal text-slate-400">(Optional - defaults to FEFO)</span>
                </label>
                <input
                  type="text"
                  data-testid="billing-batch-input"
                  value={billForm.batchNumber}
                  onChange={(e) => setBillForm({ ...billForm, batchNumber: e.target.value })}
                  placeholder="Leave empty for auto FEFO or enter batch (e.g. B2024-EXP)"
                  className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:ring-1 focus:ring-emerald-500 focus:outline-none font-mono text-[11px]"
                />
              </div>

              {/* Expired Batch Error Inline */}
              {billingError?.errorCode === 'EXPIRED_BATCH' && (
                <div data-testid="expired-batch-inline-error" className="p-2.5 bg-rose-50 border border-rose-200 rounded-lg text-rose-800 text-[11px] flex items-start gap-2">
                  <AlertCircle className="w-4 h-4 text-rose-600 shrink-0 mt-0.5" />
                  <div>
                    <span className="font-bold block">Business Rule 1 Violation:</span>
                    <span>{billingError.message}</span>
                  </div>
                </div>
              )}

              <div className="pt-3 flex items-center justify-end gap-2">
                <button
                  type="button"
                  onClick={() => setShowBillModal(false)}
                  className="px-3.5 py-2 text-slate-600 hover:bg-slate-100 rounded-lg transition"
                >
                  Close
                </button>
                <button
                  type="submit"
                  onClick={handleCreateInvoice}
                  disabled={billingSubmitting}
                  data-testid="billing-submit-btn"
                  className="px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white font-bold rounded-lg transition shadow-xs disabled:opacity-50 flex items-center gap-2"
                >
                  {billingSubmitting && <RotateCcw className="w-3.5 h-3.5 animate-spin" />}
                  {billingSubmitting ? 'Verifying Sale...' : 'Process Sale & Bill'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
