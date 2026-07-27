import React from 'react';

export const LoadingSkeleton: React.FC = () => {
  return (
    <div className="flex flex-col gap-3 mt-3">
      {[1, 2].map((i) => (
        <div key={i} className="animate-pulse bg-background border border-border rounded-lg p-3 flex gap-3">
          <div className="w-10 h-10 bg-border-light rounded-md"></div>
          <div className="flex-1 space-y-2">
            <div className="h-4 bg-border-light rounded w-1/3"></div>
            <div className="h-3 bg-border-light rounded w-3/4"></div>
          </div>
        </div>
      ))}
    </div>
  );
};
