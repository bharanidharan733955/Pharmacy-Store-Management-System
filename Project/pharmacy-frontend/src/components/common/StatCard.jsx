import React from 'react';
import { Loader2, AlertCircle, RotateCcw } from 'lucide-react';

export function StatCard({
  title,
  value,
  subtitle,
  icon: Icon,
  color = 'emerald',
  loading = false,
  error = null,
  onRetry,
}) {
  const colorMap = {
    emerald: {
      bg: 'bg-emerald-50 text-emerald-600',
      border: 'hover:border-emerald-200',
      badge: 'bg-emerald-100 text-emerald-800',
    },
    blue: {
      bg: 'bg-blue-50 text-blue-600',
      border: 'hover:border-blue-200',
      badge: 'bg-blue-100 text-blue-800',
    },
    amber: {
      bg: 'bg-amber-50 text-amber-600',
      border: 'hover:border-amber-200',
      badge: 'bg-amber-100 text-amber-800',
    },
    rose: {
      bg: 'bg-rose-50 text-rose-600',
      border: 'hover:border-rose-200',
      badge: 'bg-rose-100 text-rose-800',
    },
    indigo: {
      bg: 'bg-indigo-50 text-indigo-600',
      border: 'hover:border-indigo-200',
      badge: 'bg-indigo-100 text-indigo-800',
    },
  };

  const currentTheme = colorMap[color] || colorMap.emerald;

  if (loading) {
    return (
      <div data-testid="stat-card-loading" className="bg-white p-5 rounded-2xl border border-slate-100 shadow-xs flex items-center justify-between">
        <div className="space-y-2 flex-1">
          <div className="h-4 bg-slate-200 rounded w-24 animate-pulse" />
          <div className="h-7 bg-slate-200 rounded w-16 animate-pulse" />
          <div className="h-3 bg-slate-100 rounded w-32 animate-pulse" />
        </div>
        <div className="w-12 h-12 rounded-xl bg-slate-100 flex items-center justify-center">
          <Loader2 className="w-5 h-5 text-slate-400 animate-spin" />
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div data-testid="stat-card-error" className="bg-rose-50/60 p-5 rounded-2xl border border-rose-200 shadow-xs flex items-start justify-between gap-3">
        <div>
          <span className="text-xs font-semibold text-rose-700 block uppercase tracking-wider">{title}</span>
          <p className="text-xs text-rose-600 mt-1 mb-2">Unavailable</p>
          {onRetry && (
            <button
              onClick={onRetry}
              className="inline-flex items-center gap-1 text-xs font-medium text-rose-700 hover:text-rose-900 underline"
            >
              <RotateCcw className="w-3 h-3" /> Retry
            </button>
          )}
        </div>
        <div className="w-10 h-10 rounded-xl bg-rose-100 text-rose-600 flex items-center justify-center shrink-0">
          <AlertCircle className="w-5 h-5" />
        </div>
      </div>
    );
  }

  return (
    <div
      data-testid="stat-card"
      className={`bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs transition-all duration-200 ${currentTheme.border} hover:shadow-sm`}
    >
      <div className="flex items-center justify-between">
        <div>
          <span className="text-xs font-medium text-slate-500 uppercase tracking-wider">{title}</span>
          <h3 className="text-2xl font-bold text-slate-800 mt-1 tracking-tight">{value}</h3>
          {subtitle && (
            <p className="text-xs text-slate-500 mt-1">{subtitle}</p>
          )}
        </div>
        {Icon && (
          <div className={`w-12 h-12 rounded-xl flex items-center justify-center ${currentTheme.bg}`}>
            <Icon className="w-6 h-6" />
          </div>
        )}
      </div>
    </div>
  );
}
