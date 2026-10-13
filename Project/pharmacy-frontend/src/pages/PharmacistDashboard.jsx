import React, { useState, useEffect } from 'react';
import {
  Pill,
  AlertTriangle,
  Calendar,
  ArchiveX,
  Truck,
  PlusCircle,
  Boxes,
  RotateCcw,
  SlidersHorizontal,
  ExternalLink,
  ShieldCheck
} from 'lucide-react';
import { api } from '../services/api';
import { StatCard } from '../components/common/StatCard';
import { ErrorBanner } from '../components/common/ErrorBanner';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { EmptyState } from '../components/common/EmptyState';
import { ErrorBoundary } from '../components/common/ErrorBoundary';
import { useToast } from '../context/ToastContext';

export function PharmacistDashboard({ onNavigate }) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [threshold, setThreshold] = useState(20);
  const [showPurchaseModal, setShowPurchaseModal] = useState(false);
  const { addToast } = useToast();

  // Purchase Form State
  const [purchaseForm, setPurchaseForm] = useState({
    supplierId: 'S101',
    medicineId: 'M101',
    batchNumber: '',
    quantity: 50,
    expiryDate: new Date(Date.now() + 180 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
  });
  const [savingPurchase, setSavingPurchase] = useState(false);
  const [purchaseError, setPurchaseError] = useState(null);

  const fetchDashboardData = async (thresh = threshold) => {
    setLoading(true);
    setError(null);
    try {
      const res = await api.getPharmacistDashboard(thresh);
      setData(res);
    } catch (err) {
      setError(err);
    } finally {
      // Guaranteed loading cleanup
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboardData(threshold);
  }, [threshold]);

  const handleRecordPurchase = async (e) => {
    e.preventDefault();
    setSavingPurchase(true);
    setPurchaseError(null);
    try {
      await api.recordPurchase(purchaseForm);
      addToast({
        type: 'success',
        message: `Successfully recorded batch ${purchaseForm.batchNumber} (${purchaseForm.quantity} units).`,
      });
      setShowPurchaseModal(false);
      setPurchaseForm({
        supplierId: 'S101',
        medicineId: 'M101',
        batchNumber: '',
        quantity: 50,
        expiryDate: new Date(Date.now() + 180 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
      });
      fetchDashboardData();
    } catch (err) {
      setPurchaseError(err);
      addToast({
        type: 'error',
        errorCode: err.errorCode,
        message: err.message || 'Failed to record purchase entry.',
      });
    } finally {
      setSavingPurchase(false);
    }
  };

  return (
    <div data-testid="pharmacist-dashboard" className="space-y-6">
      {/* Title & Controls */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
        <div>
          <h2 className="text-xl sm:text-2xl font-bold text-slate-900 tracking-tight">
            Pharmacist Dispensing & Inventory Dashboard
          </h2>
          <p className="text-sm text-slate-500">
            Monitor batch-wise inventory, expiry compliance, and record incoming stock.
          </p>
        </div>
        <div className="flex items-center gap-2">
          {/* Configurable Threshold */}
          <div className="flex items-center gap-1.5 bg-white border border-slate-200 px-3 py-1.5 rounded-xl shadow-xs text-xs">
            <SlidersHorizontal className="w-3.5 h-3.5 text-slate-500" />
            <span className="text-slate-500 font-medium">Low Stock Cutoff:</span>
            <input
              type="number"
              data-testid="threshold-input"
              value={threshold}
              onChange={(e) => setThreshold(Math.max(1, parseInt(e.target.value) || 1))}
              className="w-12 font-bold text-slate-800 text-center border-b border-emerald-500 focus:outline-none"
              min="1"
            />
          </div>

          <button
            onClick={() => fetchDashboardData()}
            disabled={loading}
            data-testid="pharmacist-refresh-btn"
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
          title="Pharmacist Dashboard Unavailable"
          message={error.message || 'Unable to load pharmacist stock metrics.'}
          errorCode={error.errorCode}
          correlationId={error.correlationId}
          onRetry={() => fetchDashboardData()}
        />
      )}

      {/* 4 Primary Metric Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Total Catalog Medicines"
          value={loading ? '...' : (data?.totalCatalogMedicines ?? 'Unavailable')}
          subtitle="Formulations available"
          icon={Pill}
          color="emerald"
          loading={loading}
          error={error}
          onRetry={() => fetchDashboardData()}
        />
        <StatCard
          title="Low Stock Items"
          value={loading ? '...' : (data?.lowStockMedicines?.length ?? '0')}
          subtitle={`Units ≤ ${threshold} cutoff`}
          icon={AlertTriangle}
          color="amber"
          loading={loading}
          error={error}
          onRetry={() => fetchDashboardData()}
        />
        <StatCard
          title="Expiring in 30 Days"
          value={loading ? '...' : (data?.batchesExpiringWithin30Days?.length ?? '0')}
          subtitle="Priority FEFO candidates"
          icon={Calendar}
          color="indigo"
          loading={loading}
          error={error}
          onRetry={() => fetchDashboardData()}
        />
        <StatCard
          title="Expired Batches"
          value={loading ? '...' : (data?.expiredBatchesRequiringAttention?.length ?? '0')}
          subtitle="Quarantined (Non-saleable)"
          icon={ArchiveX}
          color="rose"
          loading={loading}
          error={error}
          onRetry={() => fetchDashboardData()}
        />
      </div>

      {/* Stock Health Banner: Non-saleable vs Saleable Stock separation */}
      <div className="bg-white p-4 rounded-2xl border border-slate-200/80 shadow-xs flex flex-wrap items-center justify-between gap-4">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center">
            <ShieldCheck className="w-6 h-6" />
          </div>
          <div>
            <h4 className="text-xs font-bold text-slate-800 uppercase tracking-wider">
              Stock Compliance & Segregation Policy
            </h4>
            <p className="text-xs text-slate-500">
              Expired batches are strictly excluded from saleable inventory and POS billing.
            </p>
          </div>
        </div>
        <div className="flex items-center gap-4 text-xs font-semibold">
          <div className="px-3 py-1.5 rounded-lg bg-emerald-50 border border-emerald-200 text-emerald-800">
            Saleable Stock: <strong>{data?.totalSaleableStock ?? 0} Units</strong>
          </div>
          <div className="px-3 py-1.5 rounded-lg bg-rose-50 border border-rose-200 text-rose-800">
            Quarantined Expired: <strong>{data?.totalQuarantinedExpiredStock ?? 0} Units</strong>
          </div>
        </div>
      </div>

      {/* Quick Action Shortcuts */}
      <div className="flex flex-wrap gap-3">
        <button
          onClick={() => setShowPurchaseModal(true)}
          data-testid="pharmacist-record-purchase-btn"
          className="inline-flex items-center gap-2 px-4 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-xl shadow-xs transition"
        >
          <PlusCircle className="w-4 h-4" />
          Record Incoming Purchase
        </button>
        <button
          onClick={() => onNavigate && onNavigate('medicines')}
          data-testid="pharmacist-view-inventory-btn"
          className="inline-flex items-center gap-2 px-4 py-2.5 bg-white border border-slate-200 hover:bg-slate-50 text-slate-700 text-xs font-bold rounded-xl shadow-xs transition"
        >
          <Boxes className="w-4 h-4 text-slate-500" />
          View Batch Inventory
        </button>
      </div>

      {/* Two-Column Section: Low Stock Warnings & Expired Batches */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Low Stock Medicines */}
        <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs">
          <div className="flex items-center justify-between mb-3">
            <h3 className="text-sm font-bold text-slate-800 uppercase tracking-wider flex items-center gap-2">
              <AlertTriangle className="w-4 h-4 text-amber-500" />
              Low Stock Medicines (Cutoff: {threshold})
            </h3>
          </div>

          <ErrorBoundary title="Low Stock List Error">
            {loading ? (
              <LoadingSpinner text="Analyzing stock levels..." />
            ) : !data?.lowStockMedicines || data.lowStockMedicines.length === 0 ? (
              <EmptyState title="All stock levels healthy" message="No medicines currently fall below the threshold." />
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-left text-xs">
                  <thead className="border-b border-slate-100 text-slate-400 font-medium">
                    <tr>
                      <th className="pb-2">Medicine</th>
                      <th className="pb-2">Category</th>
                      <th className="pb-2 text-right">Saleable Stock</th>
                      <th className="pb-2 text-right">Status</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-50">
                    {data.lowStockMedicines.map((m) => (
                      <tr key={m.medicineId} className="hover:bg-slate-50/70">
                        <td className="py-2.5 font-semibold text-slate-800">{m.medicineName}</td>
                        <td className="py-2.5 text-slate-500">{m.category}</td>
                        <td className="py-2.5 text-right font-bold text-slate-900">{m.saleableStock}</td>
                        <td className="py-2.5 text-right">
                          <span
                            className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                              m.status === 'OUT_OF_STOCK'
                                ? 'bg-rose-100 text-rose-800'
                                : 'bg-amber-100 text-amber-800'
                            }`}
                          >
                            {m.status}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </ErrorBoundary>
        </div>

        {/* Expired Batches Requiring Attention */}
        <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs">
          <div className="flex items-center justify-between mb-3">
            <h3 className="text-sm font-bold text-slate-800 uppercase tracking-wider flex items-center gap-2">
              <ArchiveX className="w-4 h-4 text-rose-600" />
              Expired Batches Requiring Attention
            </h3>
          </div>

          <ErrorBoundary title="Expired Batches Error">
            {loading ? (
              <LoadingSpinner text="Checking batch expiries..." />
            ) : !data?.expiredBatchesRequiringAttention || data.expiredBatchesRequiringAttention.length === 0 ? (
              <EmptyState title="No expired batches" message="Zero batches are currently expired in the store." />
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-left text-xs">
                  <thead className="border-b border-slate-100 text-slate-400 font-medium">
                    <tr>
                      <th className="pb-2">Batch #</th>
                      <th className="pb-2">Med ID</th>
                      <th className="pb-2">Expired Date</th>
                      <th className="pb-2 text-right">Qty</th>
                      <th className="pb-2 text-right">Status</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-50">
                    {data.expiredBatchesRequiringAttention.map((b) => (
                      <tr key={b.batchNumber} className="hover:bg-slate-50/70">
                        <td className="py-2.5 font-mono font-bold text-rose-600">{b.batchNumber}</td>
                        <td className="py-2.5 font-mono text-slate-600">{b.medicineId}</td>
                        <td className="py-2.5 text-slate-700 font-medium">{b.expiryDate}</td>
                        <td className="py-2.5 text-right font-bold text-slate-900">{b.quantity}</td>
                        <td className="py-2.5 text-right">
                          <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-rose-100 text-rose-800">
                            EXPIRED
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </ErrorBoundary>
        </div>
      </div>

      {/* Record Purchase Modal */}
      {showPurchaseModal && (
        <div data-testid="purchase-modal" className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-xl border border-slate-200">
            <h3 className="text-base font-bold text-slate-900 mb-1">Record Incoming Purchase</h3>
            <p className="text-xs text-slate-500 mb-4">
              Enter supplier and batch information to receive stock into inventory.
            </p>

            {purchaseError && (
              <ErrorBanner
                title="Purchase Entry Failed"
                message={purchaseError.message}
                errorCode={purchaseError.errorCode}
              />
            )}

            <form onSubmit={handleRecordPurchase} className="space-y-3 text-xs">
              <div>
                <label className="font-semibold text-slate-700 block mb-1">Supplier ID</label>
                <input
                  type="text"
                  required
                  value={purchaseForm.supplierId}
                  onChange={(e) => setPurchaseForm({ ...purchaseForm, supplierId: e.target.value })}
                  placeholder="e.g. S101"
                  className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:ring-1 focus:ring-emerald-500 focus:outline-none"
                />
              </div>

              <div>
                <label className="font-semibold text-slate-700 block mb-1">Medicine ID</label>
                <input
                  type="text"
                  required
                  value={purchaseForm.medicineId}
                  onChange={(e) => setPurchaseForm({ ...purchaseForm, medicineId: e.target.value })}
                  placeholder="e.g. M101"
                  className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:ring-1 focus:ring-emerald-500 focus:outline-none"
                />
              </div>

              <div>
                <label className="font-semibold text-slate-700 block mb-1">Batch Number</label>
                <input
                  type="text"
                  required
                  value={purchaseForm.batchNumber}
                  onChange={(e) => setPurchaseForm({ ...purchaseForm, batchNumber: e.target.value })}
                  placeholder="e.g. B2026-NEW"
                  className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:ring-1 focus:ring-emerald-500 focus:outline-none"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="font-semibold text-slate-700 block mb-1">Quantity Received</label>
                  <input
                    type="number"
                    required
                    min="1"
                    value={purchaseForm.quantity}
                    onChange={(e) => setPurchaseForm({ ...purchaseForm, quantity: parseInt(e.target.value) || 1 })}
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:ring-1 focus:ring-emerald-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="font-semibold text-slate-700 block mb-1">Expiry Date</label>
                  <input
                    type="date"
                    required
                    value={purchaseForm.expiryDate}
                    onChange={(e) => setPurchaseForm({ ...purchaseForm, expiryDate: e.target.value })}
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:ring-1 focus:ring-emerald-500 focus:outline-none"
                  />
                </div>
              </div>

              <div className="pt-3 flex items-center justify-end gap-2">
                <button
                  type="button"
                  onClick={() => setShowPurchaseModal(false)}
                  className="px-3.5 py-2 text-slate-600 hover:bg-slate-100 rounded-lg transition"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={savingPurchase}
                  className="px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white font-bold rounded-lg transition shadow-xs disabled:opacity-50"
                >
                  {savingPurchase ? 'Saving...' : 'Confirm Receipt'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
