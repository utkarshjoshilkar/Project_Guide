import React from 'react';
import { AlertCircle } from 'lucide-react';

interface ErrorStateProps {
  message: string;
  onRetry?: () => void;
}

export const ErrorState = ({ message, onRetry }: ErrorStateProps) => {
  return (
    <div className="flex items-center gap-3 p-4 bg-red-500/10 border border-red-500/20 rounded-xl text-red-400 animate-in fade-in duration-300">
      <AlertCircle size={20} className="shrink-0" />
      <span className="text-sm font-medium flex-1">{message}</span>
      {onRetry && (
        <button 
          onClick={onRetry}
          className="px-3 py-1.5 text-xs font-bold bg-red-500/20 hover:bg-red-500/30 rounded-lg transition-colors"
        >
          Retry
        </button>
      )}
    </div>
  );
};
