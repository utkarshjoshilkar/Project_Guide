/**
 * Purpose: Project Header Component.
 * Responsibilities: Reusable page header for the Projects module.
 * Dependencies: react, lucide-react
 * Future extensibility: Add secondary actions if needed.
 */

import React, { ReactNode } from 'react';

interface ProjectHeaderProps {
  title: string;
  subtitle?: string;
  action?: ReactNode;
}

export const ProjectHeader = ({ title, subtitle, action }: ProjectHeaderProps) => {
  return (
    <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-8">
      <div>
        <h1 className="text-3xl font-bold text-text-main mb-1">{title}</h1>
        {subtitle && <p className="text-text-muted">{subtitle}</p>}
      </div>
      {action && (
        <div>{action}</div>
      )}
    </div>
  );
};
