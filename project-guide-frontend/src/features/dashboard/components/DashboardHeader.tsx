/**
 * Purpose: Dashboard Header Component.
 * Responsibilities: Greets the user and shows the current date.
 * Dependencies: react
 * Future extensibility: Add quick action buttons here if needed.
 */

import React from 'react';

interface DashboardHeaderProps {
  fullName: string;
}

export const DashboardHeader = ({ fullName }: DashboardHeaderProps) => {
  const currentDate = new Date().toLocaleDateString('en-US', {
    weekday: 'long',
    month: 'long',
    day: 'numeric'
  });

  return (
    <div className="mb-8 flex flex-col md:flex-row md:items-end justify-between gap-4">
      <div>
        <h1 className="text-3xl font-bold text-text-main mb-1">
          Welcome back, <span className="text-primary-light">{fullName.split(' ')[0]}</span> 👋
        </h1>
        <p className="text-text-muted text-sm">{currentDate}</p>
      </div>
      <div>
        {/* Placeholder for future global action buttons (e.g. New Project) */}
      </div>
    </div>
  );
};
