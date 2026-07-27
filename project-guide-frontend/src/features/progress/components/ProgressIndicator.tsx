import React from 'react';

interface ProgressIndicatorProps {
  percentage: number;
  size?: 'sm' | 'md' | 'lg';
  showLabel?: boolean;
}

export const ProgressIndicator = ({ percentage, size = 'md', showLabel = false }: ProgressIndicatorProps) => {
  const heights = {
    sm: 'h-1.5',
    md: 'h-2',
    lg: 'h-3'
  };

  return (
    <div className="w-full flex flex-col gap-1">
      {showLabel && (
        <div className="flex justify-between items-center text-xs font-medium text-text-muted">
          <span>Progress</span>
          <span>{percentage.toFixed(0)}%</span>
        </div>
      )}
      <div className={`w-full bg-surface rounded-full overflow-hidden ${heights[size]}`}>
        <div 
          className="bg-primary h-full rounded-full transition-all duration-1000 ease-out"
          style={{ width: `${Math.max(0, Math.min(100, percentage))}%` }}
        />
      </div>
    </div>
  );
};
