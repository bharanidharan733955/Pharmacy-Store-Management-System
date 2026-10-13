import React, { useState, useEffect } from 'react';
import { Truck, Plus, Calendar } from 'lucide-react';
import { api } from '../services/api';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { ErrorBanner } from '../components/common/ErrorBanner';
import { EmptyState } from '../components/common/EmptyState';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';

export function PurchasesPage() {
  const { role } = useAuth();
  const { addToast } = useToast();
  const [batches, setBatches] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showAddModal, setShowAddModal] = useState(false);
  const [form, setForm] = useState({
    supplierId: 'S101',
    medicineId: 'M101',
    batchNumber: '',
    quantity: 100,
    expiryDate: new Date(Date.now() + 365 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
  });
  const [submitting, setSubmitting] = useState(false);

  const fetchBatches = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await api.getBatches();
      setBatches(data);
    } catch (err) {
      setError(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBatches();
  }, []);

  const handleRecordPurchase = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      await api.recordPurchase(form);
      addToast({ type: 'success', message: `Batch '${form.batchNumber}' successfully received.` });
      setShowAddModal(false);
      setForm({
        supplierId: 'S101',
        medicineId: 'M101',
        batchNumber: '',
        quantity: 100,
        expiryDate: new Date(Date.now() + 365 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
      });
      fetchBatches();
    } catch (err) {
      addToast({ type: 'error', errorCode: err.errorCode, message: err.message });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
        <div>
          <h2 className="text-xl sm:text-2xl font-bold text-slate-900 tracking-tight">Stock Batches & Purchase Records</h2>
          <p className="text-sm text-slate-500">FEFO-tracked batch inventory and receipt logs.</p>
        </div>
        {(role === 'ADMIN' || role === 'PHARMACIST') && (
          <button
            onClick={() => setShowAddModal(true)}
            data-testid="record-purchase-btn"
            className="inline-flex items-center gap-2 px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-xl shadow-xs transition"
          >
            <Plus className="w-4 h-4" />
            Record Purchase
          </button>
        )}
      </div>

      {error && <ErrorBanner title="Failed to Load Stock Batches" message={error.message} onRetry={fetchBatches} />}

      <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs">
        {loading ? (
          <LoadingSpinner text="Loading inventory batches..." />
        ) : batches.length === 0 ? (
          <EmptyState title="No batches recorded" message="No stock batches found in inventory." />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="border-b border-slate-100 text-slate-400 font-medium">
                <tr>
                  <th className="pb-2">Batch Number</th>
                  <th className="pb-2">Medicine ID</th>
                  <th className="pb-2">Expiry Date</th>
                  <th className="pb-2 text-right">Available Qty</th>
                  <th className="pb-2 text-center">Batch Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-50">
                {batches.map((b) => {
                  const isExp = new Date(b.expiryDate) < new Date();
                  return (
                    <tr key={b.batchNumber} className="hover:bg-slate-50/70">
                      <td className="py-2.5 font-mono font-bold text-slate-800">{b.batchNumber}</td>
                      <td className="py-2.5 font-mono text-slate-600">{b.medicineId}</td>
                      <td className="py-2.5 text-slate-600 font-medium">{b.expiryDate}</td>
                      <td className="py-2.5 text-right font-bold text-slate-900">{b.quantity}</td>
                      <td className="py-2.5 text-center">
                        {isExp ? (
                          <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-rose-100 text-rose-800">
                            EXPIRED (Quarantine)
                          </span>
                        ) : (
                          <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-emerald-100 text-emerald-800">
                            Saleable Active
                          </span>
                        )}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {showAddModal && (
        <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-xl border border-slate-200">
            <h3 className="text-base font-bold text-slate-900 mb-1">Record Inward Stock Batch</h3>
            <p className="text-xs text-slate-500 mb-4">Enter purchase receipt from supplier.</p>

            <form onSubmit={handleRecordPurchase} className="space-y-3 text-xs">
              <div>
                <label className="font-semibold text-slate-700 block mb-1">Supplier ID</label>
                <input
                  type="text"
                  required
                  value={form.supplierId}
                  onChange={(e) => setForm({ ...form, supplierId: e.target.value })}
                  className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none"
                />
              </div>
              <div>
                <label className="font-semibold text-slate-700 block mb-1">Medicine ID</label>
                <input
                  type="text"
                  required
                  value={form.medicineId}
                  onChange={(e) => setForm({ ...form, medicineId: e.target.value })}
                  className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none"
                />
              </div>
              <div>
                <label className="font-semibold text-slate-700 block mb-1">Batch Number</label>
                <input
                  type="text"
                  required
                  value={form.batchNumber}
                  onChange={(e) => setForm({ ...form, batchNumber: e.target.value })}
                  placeholder="e.g. B2027-C"
                  className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none"
                />
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="font-semibold text-slate-700 block mb-1">Quantity</label>
                  <input
                    type="number"
                    min="1"
                    required
                    value={form.quantity}
                    onChange={(e) => setForm({ ...form, quantity: parseInt(e.target.value) || 1 })}
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none"
                  />
                </div>
                <div>
                  <label className="font-semibold text-slate-700 block mb-1">Expiry Date</label>
                  <input
                    type="date"
                    required
                    value={form.expiryDate}
                    onChange={(e) => setForm({ ...form, expiryDate: e.target.value })}
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none"
                  />
                </div>
              </div>

              <div className="pt-3 flex items-center justify-end gap-2">
                <button
                  type="button"
                  onClick={() => setShowAddModal(false)}
                  className="px-3 py-2 text-slate-600 hover:bg-slate-100 rounded-lg"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={submitting}
                  className="px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white font-bold rounded-lg disabled:opacity-50"
                >
                  {submitting ? 'Recording...' : 'Save Inward Batch'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
