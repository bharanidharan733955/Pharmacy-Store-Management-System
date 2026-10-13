import React, { useState, useEffect } from 'react';
import {
  Pill,
  Users,
  Package,
  Calendar,
  IndianRupee,
  FileCheck2,
  TrendingUp,
  Clock,
  ExternalLink,
  ShieldAlert,
  RotateCcw
} from 'lucide-react';
import { api } from '../services/api';
import { StatCard } from '../components/common/StatCard';
import { ErrorBanner } from '../components/common/ErrorBanner';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { EmptyState } from '../components/common/EmptyState';
import { ErrorBoundary } from '../components/common/ErrorBoundary';

export function AdminDashboard({ onNavigate }) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchDashboardData = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await api.getAdminDashboard();
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

  return (
    <div data-testid="admin-dashboard" className="space-y-6">
      {/* Page Title & Refresh */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
        <div>
          <h2 className="text-xl sm:text-2xl font-bold text-slate-900 tracking-tight">
            Administrator Overview
          </h2>
          <p className="text-sm text-slate-500">
            System-wide inventory measures, revenue metrics, and compliance controls.
          </p>
        </div>
        <button
          onClick={fetchDashboardData}
          disabled={loading}
          data-testid="admin-refresh-btn"
          className="inline-flex items-center gap-2 px-3.5 py-2 bg-white border border-slate-200 hover:bg-slate-50 text-slate-700 text-xs font-semibold rounded-xl shadow-xs transition disabled:opacity-50"
        >
          <RotateCcw className={`w-3.5 h-3.5 ${loading ? 'animate-spin' : ''}`} />
          Refresh Stats
        </button>
      </div>

      {/* Global Error Banner if API fails */}
      {error && (
        <ErrorBanner
          title="Admin Dashboard Unavailable"
          message={error.message || 'Unable to retrieve administrator metrics.'}
          errorCode={error.errorCode}
          correlationId={error.correlationId}
          onRetry={fetchDashboardData}
        />
      )}

      {/* 6 Key Stat Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        <StatCard
          title="Active Medicines"
          value={loading ? '...' : (data?.totalActiveMedicines ?? 'Unavailable')}
          subtitle="Registered in database catalog"
          icon={Pill}
          color="emerald"
          loading={loading}
          error={error}
          onRetry={fetchDashboardData}
        />
        <StatCard
          title="Registered Suppliers"
          value={loading ? '...' : (data?.totalSuppliers ?? 'Unavailable')}
          subtitle="Verified pharmaceutical distributors"
          icon={Users}
          color="blue"
          loading={loading}
          error={error}
          onRetry={fetchDashboardData}
        />
        <StatCard
          title="Total Available Stock"
          value={loading ? '...' : `${data?.totalAvailableStock ?? 0} Units`}
          subtitle={data?.stockUnitMeasure || 'Units (Tablets / Bottles)'}
          icon={Package}
          color="indigo"
          loading={loading}
          error={error}
          onRetry={fetchDashboardData}
        />
        <StatCard
          title="Expiring Within 30 Days"
          value={loading ? '...' : (data?.medicinesExpiringWithin30Days ?? '0')}
          subtitle="Batches approaching expiration"
          icon={Calendar}
          color="amber"
          loading={loading}
          error={error}
          onRetry={fetchDashboardData}
        />
        <StatCard
          title="Today's Sales Total"
          value={loading ? '...' : `₹${Number(data?.todaySalesTotal || 0).toFixed(2)}`}
          subtitle="Total POS revenue for today"
          icon={IndianRupee}
          color="emerald"
          loading={loading}
          error={error}
          onRetry={fetchDashboardData}
        />
        <StatCard
          title="Today's Invoices"
          value={loading ? '...' : (data?.todayInvoiceCount ?? '0')}
          subtitle="Completed billing transactions"
          icon={FileCheck2}
          color="blue"
          loading={loading}
          error={error}
          onRetry={fetchDashboardData}
        />
      </div>

      {/* Quick Actions Panel */}
      <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs">
        <h3 className="text-sm font-bold text-slate-800 uppercase tracking-wider mb-3">
          Quick Actions
        </h3>
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
          <button
            onClick={() => onNavigate && onNavigate('medicines')}
            data-testid="quick-action-medicines"
            className="p-3 text-left bg-slate-50 hover:bg-emerald-50 border border-slate-200/70 hover:border-emerald-200 rounded-xl transition group"
          >
            <Pill className="w-5 h-5 text-emerald-600 mb-2 group-hover:scale-110 transition" />
            <span className="text-xs font-semibold text-slate-800 block">Manage Medicines</span>
            <span className="text-[11px] text-slate-500 block">View & add items</span>
          </button>
          <button
            onClick={() => onNavigate && onNavigate('suppliers')}
            data-testid="quick-action-suppliers"
            className="p-3 text-left bg-slate-50 hover:bg-blue-50 border border-slate-200/70 hover:border-blue-200 rounded-xl transition group"
          >
            <Users className="w-5 h-5 text-blue-600 mb-2 group-hover:scale-110 transition" />
            <span className="text-xs font-semibold text-slate-800 block">Manage Suppliers</span>
            <span className="text-[11px] text-slate-500 block">Distributor contacts</span>
          </button>
          <button
            onClick={() => onNavigate && onNavigate('billing')}
            data-testid="quick-action-staff"
            className="p-3 text-left bg-slate-50 hover:bg-purple-50 border border-slate-200/70 hover:border-purple-200 rounded-xl transition group"
          >
            <ShieldAlert className="w-5 h-5 text-purple-600 mb-2 group-hover:scale-110 transition" />
            <span className="text-xs font-semibold text-slate-800 block">POS Counter</span>
            <span className="text-[11px] text-slate-500 block">Direct cashier screen</span>
          </button>
          <button
            onClick={() => onNavigate && onNavigate('reports')}
            data-testid="quick-action-reports"
            className="p-3 text-left bg-slate-50 hover:bg-amber-50 border border-slate-200/70 hover:border-amber-200 rounded-xl transition group"
          >
            <TrendingUp className="w-5 h-5 text-amber-600 mb-2 group-hover:scale-110 transition" />
            <span className="text-xs font-semibold text-slate-800 block">Audit & Reports</span>
            <span className="text-[11px] text-slate-500 block">Revenue summaries</span>
          </button>
        </div>
      </div>

      {/* Two-Column Tables: Recent Purchases & Recent Invoices */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Recent Purchases Widget */}
        <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-sm font-bold text-slate-800 uppercase tracking-wider flex items-center gap-2">
              <Clock className="w-4 h-4 text-emerald-600" />
              Recent Purchases
            </h3>
            <button
              onClick={() => onNavigate && onNavigate('purchases')}
              className="text-xs font-medium text-emerald-600 hover:text-emerald-700 inline-flex items-center gap-1"
            >
              View All <ExternalLink className="w-3 h-3" />
            </button>
          </div>

          <ErrorBoundary title="Recent Purchases Error">
            {loading ? (
              <LoadingSpinner text="Fetching purchases..." />
            ) : !data?.recentPurchases || data.recentPurchases.length === 0 ? (
              <EmptyState title="No purchases recorded" message="No incoming vendor purchases found in the log." />
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-left text-xs">
                  <thead className="border-b border-slate-100 text-slate-400 font-medium">
                    <tr>
                      <th className="pb-2">Purchase ID</th>
                      <th className="pb-2">Medicine</th>
                      <th className="pb-2">Batch</th>
                      <th className="pb-2 text-right">Qty</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-50">
                    {data.recentPurchases.map((p, idx) => (
                      <tr key={p.purchaseId || idx} className="hover:bg-slate-50/70">
                        <td className="py-2.5 font-mono font-medium text-slate-600">{p.purchaseId}</td>
                        <td className="py-2.5 font-semibold text-slate-800">{p.medicineName}</td>
                        <td className="py-2.5 font-mono text-slate-500">{p.batchNumber}</td>
                        <td className="py-2.5 text-right font-bold text-emerald-700">{p.quantity}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </ErrorBoundary>
        </div>

        {/* Recent Invoices Widget */}
        <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-sm font-bold text-slate-800 uppercase tracking-wider flex items-center gap-2">
              <FileCheck2 className="w-4 h-4 text-blue-600" />
              Recent Invoices
            </h3>
            <button
              onClick={() => onNavigate && onNavigate('invoices')}
              className="text-xs font-medium text-blue-600 hover:text-blue-700 inline-flex items-center gap-1"
            >
              View All <ExternalLink className="w-3 h-3" />
            </button>
          </div>

          <ErrorBoundary title="Recent Invoices Error">
            {loading ? (
              <LoadingSpinner text="Fetching invoices..." />
            ) : !data?.recentInvoices || data.recentInvoices.length === 0 ? (
              <EmptyState title="No invoices generated" message="No sales transactions recorded yet." />
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-left text-xs">
                  <thead className="border-b border-slate-100 text-slate-400 font-medium">
                    <tr>
                      <th className="pb-2">Invoice ID</th>
                      <th className="pb-2">Medicine</th>
                      <th className="pb-2 text-right">Qty</th>
                      <th className="pb-2 text-right">Total</th>
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
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </ErrorBoundary>
        </div>
      </div>
    </div>
  );
}
