import React, { useState, useEffect } from 'react';
import { Pill, Plus, Search, ShieldCheck, AlertCircle } from 'lucide-react';
import { api } from '../services/api';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { ErrorBanner } from '../components/common/ErrorBanner';
import { EmptyState } from '../components/common/EmptyState';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';

export function MedicinesPage() {
  const { role } = useAuth();
  const { addToast } = useToast();
  const [medicines, setMedicines] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [search, setSearch] = useState('');
  const [showAddModal, setShowAddModal] = useState(false);
  const [form, setForm] = useState({
    id: '',
    name: '',
    category: 'Analgesic',
    unitPrice: 20.0,
    requiresPrescription: false,
  });
  const [submitting, setSubmitting] = useState(false);

  const fetchMedicines = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await api.getMedicines();
      setMedicines(data);
    } catch (err) {
      setError(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMedicines();
  }, []);

  const handleAddMedicine = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      await api.addMedicine(form);
      addToast({ type: 'success', message: `Medicine '${form.name}' added successfully.` });
      setShowAddModal(false);
      setForm({ id: '', name: '', category: 'Analgesic', unitPrice: 20.0, requiresPrescription: false });
      fetchMedicines();
    } catch (err) {
      addToast({ type: 'error', errorCode: err.errorCode, message: err.message });
    } finally {
      setSubmitting(false);
    }
  };

  const filtered = medicines.filter(
    (m) =>
      m.name.toLowerCase().includes(search.toLowerCase()) ||
      m.id.toLowerCase().includes(search.toLowerCase()) ||
      m.category.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
        <div>
          <h2 className="text-xl sm:text-2xl font-bold text-slate-900 tracking-tight">Medicine Inventory Catalog</h2>
          <p className="text-sm text-slate-500">Formulation master list, pricing, and prescription requirements.</p>
        </div>
        {(role === 'ADMIN' || role === 'PHARMACIST') && (
          <button
            onClick={() => setShowAddModal(true)}
            data-testid="add-medicine-btn"
            className="inline-flex items-center gap-2 px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-xl shadow-xs transition"
          >
            <Plus className="w-4 h-4" />
            Add Medicine
          </button>
        )}
      </div>

      {error && <ErrorBanner title="Failed to Load Medicines" message={error.message} onRetry={fetchMedicines} />}

      <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs space-y-4">
        <div className="relative max-w-sm">
          <Search className="w-4 h-4 text-slate-400 absolute left-3 top-2.5" />
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search catalog..."
            className="w-full pl-9 pr-3 py-1.5 text-xs bg-slate-50 border border-slate-200 rounded-xl focus:bg-white focus:outline-none"
          />
        </div>

        {loading ? (
          <LoadingSpinner text="Loading catalog..." />
        ) : filtered.length === 0 ? (
          <EmptyState title="No medicines match" message="No medicines in catalogue match your search filter." />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="border-b border-slate-100 text-slate-400 font-medium">
                <tr>
                  <th className="pb-2">Medicine ID</th>
                  <th className="pb-2">Name</th>
                  <th className="pb-2">Category</th>
                  <th className="pb-2 text-right">Unit Price</th>
                  <th className="pb-2 text-center">Prescription Rule</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-50">
                {filtered.map((m) => (
                  <tr key={m.id} className="hover:bg-slate-50/70">
                    <td className="py-2.5 font-mono font-medium text-slate-600">{m.id}</td>
                    <td className="py-2.5 font-semibold text-slate-800">{m.name}</td>
                    <td className="py-2.5 text-slate-500">{m.category}</td>
                    <td className="py-2.5 text-right font-bold text-slate-900">₹{Number(m.unitPrice).toFixed(2)}</td>
                    <td className="py-2.5 text-center">
                      {m.requiresPrescription ? (
                        <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-amber-100 text-amber-800 border border-amber-200">
                          Prescription Required
                        </span>
                      ) : (
                        <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-emerald-100 text-emerald-800">
                          OTC General
                        </span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {showAddModal && (
        <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-xl border border-slate-200">
            <h3 className="text-base font-bold text-slate-900 mb-1">Add New Medicine</h3>
            <p className="text-xs text-slate-500 mb-4">Register a medicine into the pharmaceutical catalog.</p>

            <form onSubmit={handleAddMedicine} className="space-y-3 text-xs">
              <div>
                <label className="font-semibold text-slate-700 block mb-1">Medicine ID</label>
                <input
                  type="text"
                  required
                  value={form.id}
                  onChange={(e) => setForm({ ...form, id: e.target.value })}
                  placeholder="e.g. M105"
                  className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none"
                />
              </div>
              <div>
                <label className="font-semibold text-slate-700 block mb-1">Medicine Name</label>
                <input
                  type="text"
                  required
                  value={form.name}
                  onChange={(e) => setForm({ ...form, name: e.target.value })}
                  placeholder="e.g. Ibuprofen 400mg"
                  className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none"
                />
              </div>
              <div>
                <label className="font-semibold text-slate-700 block mb-1">Category</label>
                <input
                  type="text"
                  required
                  value={form.category}
                  onChange={(e) => setForm({ ...form, category: e.target.value })}
                  placeholder="e.g. Analgesic, Antibiotic"
                  className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none"
                />
              </div>
              <div>
                <label className="font-semibold text-slate-700 block mb-1">Unit Price (₹)</label>
                <input
                  type="number"
                  step="0.01"
                  required
                  min="0"
                  value={form.unitPrice}
                  onChange={(e) => setForm({ ...form, unitPrice: parseFloat(e.target.value) || 0 })}
                  className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none"
                />
              </div>
              <div className="flex items-center gap-2 pt-1">
                <input
                  type="checkbox"
                  id="rx-req"
                  checked={form.requiresPrescription}
                  onChange={(e) => setForm({ ...form, requiresPrescription: e.target.checked })}
                  className="w-4 h-4 text-emerald-600 rounded border-slate-300 focus:ring-emerald-500"
                />
                <label htmlFor="rx-req" className="font-semibold text-slate-700">
                  Requires Doctor Prescription (Scheduled Drug)
                </label>
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
                  {submitting ? 'Saving...' : 'Save Medicine'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
