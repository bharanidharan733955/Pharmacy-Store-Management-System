import React from 'react';
import {
  LayoutDashboard,
  Pill,
  Users,
  Truck,
  Receipt,
  ShoppingCart,
  AlertTriangle,
  FileText,
  ShieldCheck,
  ChevronLeft,
  ChevronRight,
  X
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

export function Sidebar({ currentView, setCurrentView, isOpen, setIsOpen }) {
  const { role } = useAuth();

  // Define menu items with permitted roles
  const menuItems = [
    {
      id: 'dashboard',
      label: 'Dashboard',
      icon: LayoutDashboard,
      roles: ['ADMIN', 'PHARMACIST', 'CASHIER'],
    },
    {
      id: 'medicines',
      label: 'Medicines Catalog',
      icon: Pill,
      roles: ['ADMIN', 'PHARMACIST', 'CASHIER'],
    },
    {
      id: 'billing',
      label: 'POS Billing',
      icon: ShoppingCart,
      roles: ['ADMIN', 'PHARMACIST', 'CASHIER'],
    },
    {
      id: 'invoices',
      label: 'Sales Invoices',
      icon: Receipt,
      roles: ['ADMIN', 'PHARMACIST', 'CASHIER'],
    },
    {
      id: 'purchases',
      label: 'Purchase Entry',
      icon: Truck,
      roles: ['ADMIN', 'PHARMACIST'], // Cashier forbidden
    },
    {
      id: 'suppliers',
      label: 'Suppliers',
      icon: Users,
      roles: ['ADMIN'], // Pharmacist & Cashier restricted
    },
    {
      id: 'expiry',
      label: 'Expiry Management',
      icon: AlertTriangle,
      roles: ['ADMIN', 'PHARMACIST'], // Cashier forbidden
    },
    {
      id: 'reports',
      label: 'Reports & Audit',
      icon: FileText,
      roles: ['ADMIN'], // Pharmacist & Cashier restricted
    },
  ];

  // Filter based on server-verified authenticated role
  const permittedItems = menuItems.filter((item) =>
    item.roles.includes(role ? role.toUpperCase() : '')
  );

  return (
    <>
      {/* Mobile Backdrop */}
      {isOpen && (
        <div
          data-testid="sidebar-backdrop"
          className="fixed inset-0 bg-slate-900/40 backdrop-blur-xs z-40 md:hidden"
          onClick={() => setIsOpen(false)}
        />
      )}

      {/* Sidebar Container */}
      <aside
        data-testid="sidebar"
        className={`fixed md:sticky top-0 left-0 z-50 h-screen bg-white border-r border-slate-200 transition-all duration-300 flex flex-col shrink-0 ${
          isOpen ? 'translate-x-0 w-64' : '-translate-x-full md:translate-x-0 md:w-64'
        }`}
      >
        {/* Brand Header */}
        <div className="h-16 flex items-center justify-between px-5 border-b border-slate-100">
          <div className="flex items-center gap-2.5">
            <div className="w-9 h-9 rounded-xl bg-emerald-600 text-white flex items-center justify-center shadow-sm">
              <Pill className="w-5 h-5" />
            </div>
            <div>
              <span className="font-bold text-slate-900 text-base tracking-tight leading-tight block">
                PharmaCare
              </span>
              <span className="text-[10px] text-emerald-700 font-semibold uppercase tracking-wider block">
                Store System
              </span>
            </div>
          </div>
          <button
            onClick={() => setIsOpen(false)}
            className="md:hidden p-1.5 text-slate-400 hover:text-slate-600 rounded-lg"
            aria-label="Close menu"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Current Role Banner */}
        <div className="px-4 py-3 bg-slate-50 border-b border-slate-100 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <ShieldCheck className="w-4 h-4 text-emerald-600" />
            <span className="text-xs text-slate-600 font-medium">Logged Role:</span>
          </div>
          <span
            data-testid="active-role-badge"
            className={`text-xs font-bold px-2 py-0.5 rounded-full ${
              role === 'ADMIN'
                ? 'bg-purple-100 text-purple-800 border border-purple-200'
                : role === 'PHARMACIST'
                ? 'bg-emerald-100 text-emerald-800 border border-emerald-200'
                : 'bg-blue-100 text-blue-800 border border-blue-200'
            }`}
          >
            {role}
          </span>
        </div>

        {/* Navigation Links */}
        <nav className="flex-1 overflow-y-auto p-3 space-y-1">
          {permittedItems.map((item) => {
            const Icon = item.icon;
            const isActive = currentView === item.id;
            return (
              <button
                key={item.id}
                data-testid={`menu-item-${item.id}`}
                onClick={() => {
                  setCurrentView(item.id);
                  setIsOpen(false);
                }}
                className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all duration-150 ${
                  isActive
                    ? 'bg-emerald-600 text-white shadow-xs font-semibold'
                    : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
                }`}
              >
                <Icon className={`w-4 h-4 shrink-0 ${isActive ? 'text-white' : 'text-slate-500'}`} />
                <span className="truncate">{item.label}</span>
              </button>
            );
          })}
        </nav>

        {/* Sidebar Footer */}
        <div className="p-4 border-t border-slate-100 text-center">
          <p className="text-[11px] text-slate-400 font-medium">
            PharmaCare ERP v2.4 • Active
          </p>
        </div>
      </aside>
    </>
  );
}
