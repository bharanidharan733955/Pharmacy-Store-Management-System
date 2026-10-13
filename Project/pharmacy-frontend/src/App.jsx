import React, { useState } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import { ToastProvider } from './context/ToastContext';
import { AppLayout } from './components/layout/AppLayout';
import { LoginPage } from './pages/LoginPage';
import { AdminDashboard } from './pages/AdminDashboard';
import { PharmacistDashboard } from './pages/PharmacistDashboard';
import { CashierDashboard } from './pages/CashierDashboard';
import { MedicinesPage } from './pages/MedicinesPage';
import { SuppliersPage } from './pages/SuppliersPage';
import { PurchasesPage } from './pages/PurchasesPage';
import { InvoicesPage } from './pages/InvoicesPage';
import { ShieldAlert } from 'lucide-react';

function DashboardRouter() {
  const { user, role, isAuthenticated, loading } = useAuth();
  const [currentView, setCurrentView] = useState('dashboard');

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-900 flex items-center justify-center text-white text-sm font-semibold">
        Initializing Pharmacy ERP...
      </div>
    );
  }

  if (!isAuthenticated) {
    return <LoginPage onLoginSuccess={() => setCurrentView('dashboard')} />;
  }

  // Derive role-specific dashboard
  const renderDashboardByRole = () => {
    switch (role ? role.toUpperCase() : '') {
      case 'ADMIN':
        return <AdminDashboard onNavigate={(view) => setCurrentView(view)} />;
      case 'PHARMACIST':
        return <PharmacistDashboard onNavigate={(view) => setCurrentView(view)} />;
      case 'CASHIER':
      default:
        return <CashierDashboard onNavigate={(view) => setCurrentView(view)} />;
    }
  };

  // View titles
  const viewTitles = {
    dashboard: `${role} Dashboard`,
    medicines: 'Medicines Catalog',
    billing: 'POS Billing Counter',
    invoices: 'Sales Invoices',
    purchases: 'Purchase Entry & Batches',
    suppliers: 'Suppliers Management',
    expiry: 'Expiry & Quarantine Management',
    reports: 'Sales & Inventory Reports',
  };

  // Route authorization checks (Server-identity claims)
  const renderContent = () => {
    if (currentView === 'dashboard') {
      return renderDashboardByRole();
    }
    if (currentView === 'medicines') {
      return <MedicinesPage />;
    }
    if (currentView === 'billing') {
      return <CashierDashboard onNavigate={(view) => setCurrentView(view)} />;
    }
    if (currentView === 'invoices') {
      return <InvoicesPage />;
    }
    if (currentView === 'purchases') {
      if (role !== 'ADMIN' && role !== 'PHARMACIST') {
        return (
          <div data-testid="access-denied-view" className="p-8 bg-rose-50 border border-rose-200 rounded-2xl text-center max-w-lg mx-auto">
            <ShieldAlert className="w-10 h-10 text-rose-600 mx-auto mb-2" />
            <h3 className="text-base font-bold text-rose-900">Access Restricted</h3>
            <p className="text-xs text-rose-700 mt-1">
              Purchase entry and inventory receipts require Pharmacist or Administrator credentials.
            </p>
          </div>
        );
      }
      return <PurchasesPage />;
    }
    if (currentView === 'suppliers') {
      if (role !== 'ADMIN') {
        return (
          <div data-testid="access-denied-view" className="p-8 bg-rose-50 border border-rose-200 rounded-2xl text-center max-w-lg mx-auto">
            <ShieldAlert className="w-10 h-10 text-rose-600 mx-auto mb-2" />
            <h3 className="text-base font-bold text-rose-900">Access Restricted</h3>
            <p className="text-xs text-rose-700 mt-1">
              Supplier registration is restricted to Administrator role.
            </p>
          </div>
        );
      }
      return <SuppliersPage />;
    }
    if (currentView === 'expiry') {
      return <PharmacistDashboard onNavigate={(view) => setCurrentView(view)} />;
    }
    if (currentView === 'reports') {
      return <AdminDashboard onNavigate={(view) => setCurrentView(view)} />;
    }
    return renderDashboardByRole();
  };

  return (
    <AppLayout
      currentView={currentView}
      setCurrentView={setCurrentView}
      viewTitle={viewTitles[currentView] || 'PharmaCare System'}
    >
      {renderContent()}
    </AppLayout>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <ToastProvider>
        <DashboardRouter />
      </ToastProvider>
    </AuthProvider>
  );
}
