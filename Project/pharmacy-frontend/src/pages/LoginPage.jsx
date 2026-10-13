import React, { useState } from 'react';
import { Pill, Shield, Lock, User, AlertCircle, ArrowRight } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';

export function LoginPage({ onLoginSuccess }) {
  const { login } = useAuth();
  const { addToast } = useToast();
  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('admin123');
  const [submitting, setSubmitting] = useState(false);
  const [loginError, setLoginError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setLoginError(null);

    try {
      const user = await login(username, password);
      addToast({
        type: 'success',
        message: `Welcome back, ${user.fullName || user.username}!`,
      });
      if (onLoginSuccess) {
        onLoginSuccess(user.role);
      }
    } catch (err) {
      setLoginError(err.message || 'Invalid username or password.');
      addToast({
        type: 'error',
        message: err.message || 'Login failed. Please check credentials.',
      });
    } finally {
      // Guaranteed loading cleanup
      setSubmitting(false);
    }
  };

  const setDemoRole = (u, p) => {
    setUsername(u);
    setPassword(p);
    setLoginError(null);
  };

  return (
    <div data-testid="login-page" className="min-h-screen bg-slate-900 flex items-center justify-center p-4">
      <div className="max-w-md w-full bg-white rounded-3xl p-8 shadow-2xl border border-slate-100">
        {/* Brand Header */}
        <div className="text-center mb-6">
          <div className="w-14 h-14 bg-emerald-600 rounded-2xl mx-auto flex items-center justify-center text-white mb-3 shadow-md">
            <Pill className="w-8 h-8" />
          </div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">PharmaCare System</h1>
          <p className="text-xs text-slate-500 mt-1">Role-Based Pharmacy Store Management</p>
        </div>

        {/* Error Feedback */}
        {loginError && (
          <div data-testid="login-error-banner" className="mb-4 p-3.5 bg-rose-50 border border-rose-200 rounded-xl text-rose-800 text-xs flex items-center gap-2">
            <AlertCircle className="w-4 h-4 text-rose-600 shrink-0" />
            <span>{loginError}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="text-xs font-semibold text-slate-700 block mb-1">Username</label>
            <div className="relative">
              <User className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
              <input
                type="text"
                required
                data-testid="username-input"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="Enter username"
                className="w-full pl-9 pr-3 py-2 text-sm border border-slate-200 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:outline-none"
              />
            </div>
          </div>

          <div>
            <label className="text-xs font-semibold text-slate-700 block mb-1">Password</label>
            <div className="relative">
              <Lock className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
              <input
                type="password"
                required
                data-testid="password-input"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter password"
                className="w-full pl-9 pr-3 py-2 text-sm border border-slate-200 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:outline-none"
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={submitting}
            data-testid="login-submit-btn"
            className="w-full py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-sm rounded-xl transition shadow-md flex items-center justify-center gap-2 disabled:opacity-50"
          >
            {submitting ? 'Authenticating...' : 'Sign In'}
            {!submitting && <ArrowRight className="w-4 h-4" />}
          </button>
        </form>

        {/* Demo Fast Logins for 3 Roles */}
        <div className="mt-8 pt-6 border-t border-slate-100">
          <p className="text-[11px] font-bold uppercase tracking-wider text-slate-400 text-center mb-3">
            Quick 1-Click Role Accounts
          </p>
          <div className="grid grid-cols-3 gap-2 text-xs font-semibold">
            <button
              type="button"
              data-testid="demo-admin-btn"
              onClick={() => setDemoRole('admin', 'admin123')}
              className="py-2 px-1 rounded-xl bg-purple-50 hover:bg-purple-100 text-purple-700 border border-purple-200 text-center transition"
            >
              ADMIN
            </button>
            <button
              type="button"
              data-testid="demo-pharmacist-btn"
              onClick={() => setDemoRole('pharmacist', 'pharma123')}
              className="py-2 px-1 rounded-xl bg-emerald-50 hover:bg-emerald-100 text-emerald-700 border border-emerald-200 text-center transition"
            >
              PHARMACIST
            </button>
            <button
              type="button"
              data-testid="demo-cashier-btn"
              onClick={() => setDemoRole('cashier', 'cashier123')}
              className="py-2 px-1 rounded-xl bg-blue-50 hover:bg-blue-100 text-blue-700 border border-blue-200 text-center transition"
            >
              CASHIER
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
