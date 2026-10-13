import React, { useState } from 'react';
import { Sidebar } from './Sidebar';
import { Navbar } from './Navbar';
import { ErrorBoundary } from '../common/ErrorBoundary';

export function AppLayout({ children, currentView, setCurrentView, viewTitle }) {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);

  return (
    <div className="min-h-screen bg-slate-50 flex">
      {/* Reusable Sidebar (Role-filtered) */}
      <Sidebar
        currentView={currentView}
        setCurrentView={setCurrentView}
        isOpen={isSidebarOpen}
        setIsOpen={setIsSidebarOpen}
      />

      {/* Main Content Area */}
      <div className="flex-1 flex flex-col min-w-0">
        <Navbar
          onMenuToggle={() => setIsSidebarOpen((prev) => !prev)}
          currentViewTitle={viewTitle}
        />

        <main className="flex-1 p-4 sm:p-6 lg:p-8 max-w-7xl w-full mx-auto">
          {/* Sectional ErrorBoundary protecting main content without crashing sidebar */}
          <ErrorBoundary title="Content Rendering Issue">
            {children}
          </ErrorBoundary>
        </main>
      </div>
    </div>
  );
}
