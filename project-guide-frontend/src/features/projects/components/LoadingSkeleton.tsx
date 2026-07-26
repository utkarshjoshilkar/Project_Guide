/**
 * Purpose: Project List Loading Skeleton.
 * Responsibilities: Prevents layout shift while fetching projects.
 * Dependencies: react
 * Future extensibility: N/A
 */

import React from 'react';

export const LoadingSkeleton = () => {
  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      {[...Array(6)].map((_, i) => (
        <div key={i} className="animate-pulse glass-card p-5 h-48 flex flex-col justify-between">
          <div className="flex justify-between">
            <div className="h-6 w-20 bg-surface/80 rounded"></div>
            <div className="h-6 w-24 bg-surface/80 rounded"></div>
          </div>
          <div className="space-y-2 mt-4">
            <div className="h-5 w-3/4 bg-surface/80 rounded"></div>
            <div className="h-4 w-full bg-surface/80 rounded"></div>
            <div className="h-4 w-5/6 bg-surface/80 rounded"></div>
          </div>
          <div className="flex justify-between mt-4">
            <div className="h-4 w-1/3 bg-surface/80 rounded"></div>
            <div className="h-4 w-1/4 bg-surface/80 rounded"></div>
          </div>
        </div>
      ))}
    </div>
  );
};
