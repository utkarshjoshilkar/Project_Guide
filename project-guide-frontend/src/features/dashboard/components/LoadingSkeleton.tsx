/**
 * Purpose: Dashboard Loading Skeleton.
 * Responsibilities: Prevents layout shift while API is fetching.
 * Dependencies: react
 * Future extensibility: Adapt layout based on screen size dynamically.
 */

import React from 'react';

export const LoadingSkeleton = () => {
  return (
    <div className="animate-pulse space-y-8">
      <div className="h-12 w-1/3 bg-surface/50 rounded-lg"></div>
      
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {[...Array(4)].map((_, i) => (
          <div key={i} className="h-32 bg-surface/50 rounded-xl border border-white/5"></div>
        ))}
      </div>
      
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <div className="h-64 bg-surface/50 rounded-xl border border-white/5"></div>
        <div className="h-64 bg-surface/50 rounded-xl border border-white/5"></div>
      </div>
    </div>
  );
};
