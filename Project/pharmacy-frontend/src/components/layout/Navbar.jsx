import React, { useState } from 'react';
import { Menu, LogOut, User, Shield, ChevronDown } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

export function Navbar({ onMenuToggle, currentViewTitle }) {
  const { user, role, logout } = useAuth();
  const [showProfileMenu, setShowProfileMenu] = useState(false);

  return (
    <header data-testid="navbar" className="h-16 bg-white border-b border-slate-200 sticky top-0 z-30 flex items-center justify-between px-4 sm:px-6">
      <div className="flex items-center gap-3">
        <button
          onClick={onMenuToggle}
          data-testid="navbar-menu-toggle"
          className="md:hidden p-2 rounded-lg text-slate-500 hover:text-slate-800 hover:bg-slate-100 transition"
          aria-label="Toggle navigation menu"
        >
          <Menu className="w-5 h-5" />
        </button>
        <div>
          <h1 className="text-base sm:text-lg font-bold text-slate-800 tracking-tight">
            {currentViewTitle || 'Dashboard'}
          </h1>
          <span className="text-xs text-slate-500 hidden sm:inline-block">
            Pharmacy Store Management System
          </span>
        </div>
      </div>

      {/* Right Controls: Profile & Logout */}
      <div className="flex items-center gap-3">
        <div className="relative">
          <button
            onClick={() => setShowProfileMenu((prev) => !prev)}
            data-testid="profile-menu-button"
            className="flex items-center gap-2.5 p-1.5 sm:px-3 sm:py-1.5 rounded-xl hover:bg-slate-100 border border-slate-200/80 transition"
          >
            <div className="w-8 h-8 rounded-lg bg-emerald-100 text-emerald-800 flex items-center justify-center font-bold text-sm">
              {user?.fullName ? user.fullName.charAt(0) : 'U'}
            </div>
            <div className="text-left hidden sm:block">
              <span className="text-xs font-semibold text-slate-800 block leading-tight truncate max-w-[120px]">
                {user?.fullName || user?.username || 'User'}
              </span>
              <span className="text-[10px] text-slate-500 font-medium block">
                {role}
              </span>
            </div>
            <ChevronDown className="w-4 h-4 text-slate-400 hidden sm:block" />
          </button>

          {/* Profile Dropdown */}
          {showProfileMenu && (
            <div
              data-testid="profile-dropdown"
              className="absolute right-0 mt-2 w-56 bg-white rounded-xl shadow-lg border border-slate-100 py-2 z-50 animate-fade-in"
            >
              <div className="px-4 py-2 border-b border-slate-100">
                <p className="text-xs text-slate-500">Signed in as</p>
                <p className="text-sm font-bold text-slate-900 truncate">{user?.username}</p>
                <p className="text-xs text-emerald-700 font-semibold">{user?.fullName}</p>
              </div>

              <div className="px-2 py-1">
                <div className="flex items-center gap-2 px-3 py-2 text-xs text-slate-600">
                  <Shield className="w-3.5 h-3.5 text-slate-400" />
                  <span>Role: <strong>{role}</strong></span>
                </div>
              </div>

              <div className="border-t border-slate-100 pt-1 px-2">
                <button
                  onClick={() => {
                    setShowProfileMenu(false);
                    logout();
                  }}
                  data-testid="logout-button"
                  className="w-full flex items-center gap-2 px-3 py-2 text-sm text-rose-600 hover:bg-rose-50 rounded-lg transition"
                >
                  <LogOut className="w-4 h-4" />
                  <span>Log out</span>
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </header>
  );
}
