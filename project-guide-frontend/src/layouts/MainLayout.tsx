/**
 * Purpose: Layout for authenticated pages.
 * Responsibilities: Provides Sidebar, Navbar, and Content area.
 * Dependencies: react-router-dom
 * Future extensibility: Implement mobile sidebar toggle state, breadcrumbs in navbar.
 */

import React from 'react';
import { Outlet } from 'react-router-dom';

const MainLayout = () => {
  return (
    <div className="flex min-h-screen bg-background">
      {/* Sidebar Placeholder */}
      <aside className="w-64 bg-surface hidden md:block border-r border-white/5">
        <div className="p-4 font-bold text-lg text-primary-light">Project Guide</div>
        {/* Navigation links will go here */}
      </aside>

      <div className="flex-1 flex flex-col">
        {/* Navbar Placeholder */}
        <header className="h-16 bg-surface/50 backdrop-blur-md border-b border-white/5 flex items-center px-6 justify-between">
          <div className="font-medium text-text-muted">Dashboard</div>
          <div className="flex items-center gap-4">
            {/* Theme Toggle & Profile Avatar will go here */}
            <div className="w-8 h-8 rounded-full bg-accent/20 flex items-center justify-center text-accent font-bold">U</div>
          </div>
        </header>

        {/* Main Content */}
        <main className="flex-1 p-6 overflow-auto">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default MainLayout;
