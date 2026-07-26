/**
 * Purpose: Reusable Statistic Card Component.
 * Responsibilities: Displays a single metric clearly.
 * Dependencies: react, lucide-react (for icons)
 * Future extensibility: Add trend arrows (up/down) and percentage changes.
 */

import React, { ReactNode } from 'react';

interface StatCardProps {
  title: string;
  value: string | number;
  icon?: ReactNode;
  description?: string;
  colorClass?: string;
}

export const StatCard = ({ title, value, icon, description, colorClass = "text-primary-light" }: StatCardProps) => {
  return (
    <div className="glass-card p-6 flex flex-col justify-between">
      <div className="flex justify-between items-start mb-4">
        <h3 className="text-sm font-medium text-text-muted">{title}</h3>
        {icon && <div className={`p-2 rounded-lg bg-surface/50 border border-white/5 ${colorClass}`}>{icon}</div>}
      </div>
      <div>
        <p className={`text-3xl font-bold ${colorClass}`}>{value}</p>
        {description && <p className="text-xs text-text-muted mt-2">{description}</p>}
      </div>
    </div>
  );
};
