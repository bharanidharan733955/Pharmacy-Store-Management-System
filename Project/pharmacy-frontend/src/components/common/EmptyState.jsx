import React from 'react';
import { PackageOpen } from 'lucide-react';

export function EmptyState({ title = 'No records found', message = 'There is currently no data available for this section.', icon: Icon = PackageOpen }) {
  return (
    <div data-testid="empty-state" className="flex flex-col items-center justify-center p-8 text-center text-slate-500 bg-white/60 rounded-xl border border-dashed border-slate-200">
      <div className="w-12 h-12 rounded-full bg-slate-100 flex items-center justify-center text-slate-400 mb-3">
        <Icon className="w-6 h-6" />
      </div>
      <h4 className="text-sm font-semibold text-slate-700 mb-1">{title}</h4>
      <p className="text-xs text-slate-500 max-w-sm">{message}</p>
    </div>
  );
}
