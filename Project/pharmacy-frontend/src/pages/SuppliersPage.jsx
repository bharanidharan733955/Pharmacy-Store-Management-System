import React, { useState, useEffect } from 'react';
import { Users, Plus, Phone, UserCheck } from 'lucide-react';
import { api } from '../services/api';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { ErrorBanner } from '../components/common/ErrorBanner';
import { EmptyState } from '../components/common/EmptyState';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';

export function SuppliersPage() {
  const { role } = useAuth();
  const { addToast } = useToast();
  const [suppliers, setSuppliers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showAddModal, setShowAddModal] = useState(false);
  const [form, setForm] = useState({ id: '', name: '', contactPerson: '', phone: '' });
  const [submitting, setSubmitting] = useState(false);

  const fetchSuppliers = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await api.getSuppliers();
      setSuppliers(data);
    } catch (err) {
      setError(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSuppliers();
  }, []);

  const handleAddSupplier = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      await api.addSupplier(form);
      addToast({ type: 'success', message: `Supplier '${form.name}' registered.` });
      setShowAddModal(false);
      setForm({ id: '', name: '', contactPerson: '', phone: '' });
      fetchSuppliers();
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
          <h2 className="text-xl sm:text-2xl font-bold text-slate-900 tracking-tight">Suppliers & Distributors</h2>
          <p className="text-sm text-slate-500">Authorized medical distributors and vendor logistics.</p>
        </div>
        {role === 'ADMIN' && (
          <button
            onClick={() => setShowAddModal(true)}
            data-testid="add-supplier-btn"
            className="inline-flex items-center gap-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white text-xs font-bold rounded-xl shadow-xs transition"
          >
            <Plus className="w-4 h-4" />
            Add Supplier
          </button>
        )}
      </div>

      {error && <ErrorBanner title="Failed to Load Suppliers" message={error.message} onRetry={fetchSuppliers} />}

      <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs">
        {loading ? (
          <LoadingSpinner text="Loading suppliers..." />
        ) : suppliers.length === 0 ? (
          <EmptyState title="No suppliers registered" message="No distributor records found in the database." />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="border-b border-slate-100 text-slate-400 font-medium">
                <tr>
                  <th className="pb-2">Supplier ID</th>
                  <th className="pb-2">Company Name</th>
                  <th className="pb-2">Contact Person</th>
                  <th className="pb-2">Phone</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-50">
                {suppliers.map((s) => (
                  <tr key={s.id} className="hover:bg-slate-50/70">
                    <td className="py-2.5 font-mono font-medium text-slate-600">{s.id}</td>
                    <td className="py-2.5 font-semibold text-slate-800">{s.name}</td>
                    <td className="py-2.5 text-slate-600 flex items-center gap-1.5">
                      <UserCheck className="w-3.5 h-3.5 text-slate-400" />
                      {s.contactPerson}
                    </td>
                    <td className="py-2.5 text-slate-600 font-mono">
                      <span className="inline-flex items-center gap-1">
                        <Phone className="w-3 h-3 text-slate-400" />
                        {s.phone}
                      </span>
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
            <h3 className="text-base font-bold text-slate-900 mb-1">Add Supplier</h3>
            <p className="text-xs text-slate-500 mb-4">Register an authorized pharmaceutical distributor.</p>

            <form onSubmit={handleAddSupplier} className="space-y-3 text-xs">
              <div>
                <label className="font-semibold text-slate-700 block mb-1">Supplier ID</label>
                <input
                  type="text"
                  required
                  value={form.id}
                  onChange={(e) => setForm({ ...form, id: e.target.value })}
                  placeholder="e.g. S103"
                  className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none"
                />
              </div>
              <div>
                <label className="font-semibold text-slate-700 block mb-1">Company Name</label>
                <input
                  type="text"
                  required
                  value={form.name}
                  onChange={(e) => setForm({ ...form, name: e.target.value })}
                  placeholder="e.g. Sun Pharma Logistics"
                  className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none"
                />
              </div>
              <div>
                <label className="font-semibold text-slate-700 block mb-1">Contact Person</label>
                <input
                  type="text"
                  required
                  value={form.contactPerson}
                  onChange={(e) => setForm({ ...form, contactPerson: e.target.value })}
                  placeholder="e.g. Vikram Sharma"
                  className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none"
                />
              </div>
              <div>
                <label className="font-semibold text-slate-700 block mb-1">Phone Number</label>
                <input
                  type="text"
                  required
                  value={form.phone}
                  onChange={(e) => setForm({ ...form, phone: e.target.value })}
                  placeholder="e.g. +91 9988776655"
                  className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none"
                />
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
                  className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white font-bold rounded-lg disabled:opacity-50"
                >
                  {submitting ? 'Saving...' : 'Save Supplier'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
