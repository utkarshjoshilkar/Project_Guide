import React from 'react';

interface ProgressBadgeProps {
  percentage: number;
}

export const ProgressBadge = ({ percentage }: ProgressBadgeProps) => {
  let colorClass = "text-text-muted bg-surface";
  if (percentage > 0 && percentage < 100) colorClass = "text-yellow-400 bg-yellow-400/10";
  if (percentage === 100) colorClass = "text-green-400 bg-green-400/10";

  return (
    <div className={`px-2 py-1 rounded-full text-xs font-semibold ${colorClass}`}>
      {percentage}%
    </div>
  );
};
