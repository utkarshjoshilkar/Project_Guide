import React from 'react';
import { AlertCircle } from 'lucide-react';

interface ErrorStateProps {
  error: string;
  onRetry?: () => void;
}

export const ErrorState = ({ error, onRetry }: ErrorStateProps) => {
  return (
    <div className="text-center py-12 px-4 bg-red-500/10 rounded-xl border border-red-500/20 max-w-2xl mx-auto">
      <AlertCircle className="mx-auto text-red-400 mb-4" size={40} />
      <h3 className="text-lg font-medium text-red-400 mb-2">Error Loading Roadmap</h3>
      <p className="text-red-400/80 mb-6">{error}</p>
      {onRetry && (
        <button 
          onClick={onRetry}
          className="px-6 py-2 bg-surface hover:bg-surface-light border border-red-500/30 text-white rounded-lg transition-colors"
        >
          Try Again
        </button>
      )}
    </div>
  );
};
