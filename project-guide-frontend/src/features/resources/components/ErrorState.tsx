import React from 'react';
import { AlertCircle, RefreshCw } from 'lucide-react';

interface ErrorStateProps {
  message: string;
  onRetry: () => void;
}

export const ErrorState: React.FC<ErrorStateProps> = ({ message, onRetry }) => {
  return (
    <div className="flex items-start gap-3 p-3 mt-3 bg-red-500/10 border border-red-500/20 rounded-lg text-red-400">
      <AlertCircle className="w-5 h-5 shrink-0 mt-0.5" />
      <div className="flex-1">
        <h4 className="text-sm font-medium mb-1">Failed to load resources</h4>
        <p className="text-xs text-red-400/80 mb-2">{message}</p>
        <button
          onClick={onRetry}
          className="flex items-center gap-1.5 text-xs font-medium text-red-400 hover:text-red-300 transition-colors"
        >
          <RefreshCw className="w-3.5 h-3.5" />
          Retry
        </button>
      </div>
    </div>
  );
};
