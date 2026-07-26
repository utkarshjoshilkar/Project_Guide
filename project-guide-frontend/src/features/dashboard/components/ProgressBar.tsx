/**
 * Purpose: Reusable Progress Bar.
 * Responsibilities: Renders a lightweight, native progress bar using Tailwind.
 * Dependencies: react
 * Future extensibility: Add color variants (success, warning, error) based on percentage.
 */

import React from 'react';

interface ProgressBarProps {
  progress: number; // 0 to 100
  label?: string;
  showValue?: boolean;
  colorClass?: string;
}

export const ProgressBar = ({ progress, label, showValue = true, colorClass = "bg-primary" }: ProgressBarProps) => {
  const safeProgress = Math.min(Math.max(progress, 0), 100);

  return (
    <div className="w-full">
      {(label || showValue) && (
        <div className="flex justify-between items-center mb-1 text-sm">
          {label && <span className="font-medium text-text-main">{label}</span>}
          {showValue && <span className="text-text-muted">{safeProgress.toFixed(0)}%</span>}
        </div>
      )}
      <div className="w-full bg-surface/80 rounded-full h-2.5 overflow-hidden border border-white/5">
        <div 
          className={`h-2.5 rounded-full ${colorClass} transition-all duration-500 ease-out`} 
          style={{ width: `${safeProgress}%` }}
        ></div>
      </div>
    </div>
  );
};
