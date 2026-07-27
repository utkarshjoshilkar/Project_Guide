import React from 'react';

export const LoadingSkeleton = () => {
  return (
    <div className="space-y-6 animate-pulse">
      {/* Header Skeleton */}
      <div className="glass-card p-6">
        <div className="h-8 bg-surface rounded w-1/3 mb-4"></div>
        <div className="h-4 bg-surface rounded w-1/4 mb-6"></div>
        <div className="flex gap-4">
          <div className="h-20 bg-surface rounded flex-1"></div>
          <div className="h-20 bg-surface rounded flex-1"></div>
          <div className="h-20 bg-surface rounded flex-1"></div>
        </div>
      </div>

      {/* Timeline Skeleton */}
      <div className="space-y-4 relative pl-8">
        <div className="absolute left-[15px] top-4 bottom-4 w-[2px] bg-surface"></div>
        {[1, 2, 3].map(i => (
          <div key={i} className="glass-card p-6 relative">
            <div className="absolute -left-[37px] top-6 w-6 h-6 rounded-full bg-surface border-4 border-background"></div>
            <div className="h-6 bg-surface rounded w-1/2 mb-3"></div>
            <div className="h-4 bg-surface rounded w-full mb-2"></div>
            <div className="h-4 bg-surface rounded w-3/4"></div>
          </div>
        ))}
      </div>
    </div>
  );
};
