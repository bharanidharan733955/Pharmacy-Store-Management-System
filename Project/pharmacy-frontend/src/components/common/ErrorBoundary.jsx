import React from 'react';
import { AlertOctagon, RotateCcw } from 'lucide-react';

export class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    console.error('ErrorBoundary caught rendering error:', error, errorInfo);
  }

  handleRetry = () => {
    this.setState({ hasError: false, error: null });
    if (this.props.onReset) {
      this.props.onReset();
    }
  };

  render() {
    if (this.state.hasError) {
      return (
        <div data-testid="error-boundary-fallback" className="p-6 bg-rose-50 border border-rose-200 rounded-2xl shadow-sm text-center my-4">
          <div className="w-12 h-12 mx-auto bg-rose-100 text-rose-600 rounded-full flex items-center justify-center mb-3">
            <AlertOctagon className="w-6 h-6" />
          </div>
          <h3 className="text-lg font-semibold text-rose-900 mb-1">
            {this.props.title || 'Widget Encountered a Problem'}
          </h3>
          <p className="text-sm text-rose-700 max-w-md mx-auto mb-4">
            {this.state.error?.message || 'An unexpected rendering error occurred in this section. The rest of the dashboard and navigation remains operational.'}
          </p>
          <button
            onClick={this.handleRetry}
            className="inline-flex items-center gap-2 px-4 py-2 bg-rose-600 text-white text-sm font-medium rounded-lg hover:bg-rose-700 transition shadow-sm"
          >
            <RotateCcw className="w-4 h-4" />
            Retry Section
          </button>
        </div>
      );
    }

    return this.props.children;
  }
}
