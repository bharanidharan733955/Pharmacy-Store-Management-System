import React, { useState, useEffect } from 'react';
import { Receipt, Search, FileText } from 'lucide-react';
import { api } from '../services/api';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { ErrorBanner } from '../components/common/ErrorBanner';
import { EmptyState } from '../components/common/EmptyState';

export function InvoicesPage() {
  const [invoices, setInvoices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [search, setSearch] = useState('');

  const fetchInvoices = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await api.getInvoices();
      setInvoices(data);
    } catch (err) {
      setError(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchInvoices();
  }, []);

  const filtered = invoices.filter(
    (inv) =>
      inv.invoiceId.toLowerCase().includes(search.toLowerCase()) ||
      inv.medicineName.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-xl sm:text-2xl font-bold text-slate-900 tracking-tight">Sales Invoices & Receipts</h2>
        <p className="text-sm text-slate-500">Historical billing ledger with audit trail and totals.</p>
      </div>

      {error && <ErrorBanner title="Failed to Load Invoices" message={error.message} onRetry={fetchInvoices} />}

      <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs space-y-4">
        <div className="relative max-w-sm">
          <Search className="w-4 h-4 text-slate-400 absolute left-3 top-2.5" />
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search invoice # or medicine..."
            className="w-full pl-9 pr-3 py-1.5 text-xs bg-slate-50 border border-slate-200 rounded-xl focus:bg-white focus:outline-none"
          />
        </div>

        {loading ? (
          <LoadingSpinner text="Loading invoices ledger..." />
        ) : filtered.length === 0 ? (
          <EmptyState title="No invoices found" message="No customer sales invoices match." />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="border-b border-slate-100 text-slate-400 font-medium">
                <tr>
                  <th className="pb-2">Invoice #</th>
                  <th className="pb-2">Medicine Sold</th>
                  <th className="pb-2 text-right">Quantity</th>
                  <th className="pb-2 text-right">Total Amount</th>
                  <th className="pb-2 text-right">Timestamp</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-50">
                {filtered.map((inv) => (
                  <tr key={inv.invoiceId} className="hover:bg-slate-50/70">
                    <td className="py-2.5 font-mono font-bold text-blue-600">{inv.invoiceId}</td>
                    <td className="py-2.5 font-medium text-slate-800">{inv.medicineName}</td>
                    <td className="py-2.5 text-right font-medium text-slate-600">{inv.quantity}</td>
                    <td className="py-2.5 text-right font-bold text-emerald-700">₹{Number(inv.totalAmount).toFixed(2)}</td>
                    <td className="py-2.5 text-right text-slate-500 font-mono text-[11px]">{inv.timestamp}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
