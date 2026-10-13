import React from 'react';
import { AlertCircle, RotateCcw } from 'lucide-react';

export function ErrorBanner({ message, errorCode, correlationId, onRetry, title = 'Operation Failed' }) {
  return (
    <div data-testid="error-banner" className="p-4 bg-rose-50 border border-rose-200 rounded-xl my-3 text-left">
      <div className="flex items-start gap-3">
        <AlertCircle className="w-5 h-5 text-rose-600 shrink-0 mt-0.5" />
        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-2 mb-1 flex-wrap">
            <h4 className="text-sm font-semibold text-rose-900">{title}</h4>
            {errorCode && (
              <span className="px-2 py-0.5 text-xs font-mono font-bold bg-rose-200/80 text-rose-800 rounded">
                {errorCode}
              </span>
            )}
          </div>
          <p className="text-sm text-rose-700 leading-relaxed mb-2">{message}</p>
          {correlationId && (
            <p className="text-xs text-rose-500 font-mono">Trace ID: {correlationId}</p>
          )}
          {onRetry && (
            <button
              onClick={onRetry}
              data-testid="error-retry-btn"
              className="mt-2 inline-flex items-center gap-1.5 px-3 py-1.5 bg-rose-600 hover:bg-rose-700 text-white text-xs font-medium rounded-lg transition shadow-xs"
            >
              <RotateCcw className="w-3.5 h-3.5" />
              Retry Request
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
