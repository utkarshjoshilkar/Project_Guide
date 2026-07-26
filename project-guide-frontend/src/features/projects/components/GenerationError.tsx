/**
 * Purpose: Roadmap Generation Error Component.
 * Responsibilities: Displays friendly error states and a retry button.
 * Dependencies: react, lucide-react
 * Future extensibility: N/A
 */

import React from 'react';
import { AlertTriangle, RefreshCw } from 'lucide-react';

interface GenerationErrorProps {
  error: string;
  onRetry: () => void;
}

export const GenerationError = ({ error, onRetry }: GenerationErrorProps) => {
  return (
    <div className="flex flex-col items-center justify-center py-8 text-center animate-in fade-in duration-300">
      <div className="w-16 h-16 bg-red-500/10 rounded-full flex items-center justify-center text-red-400 mb-6 border border-red-500/20">
        <AlertTriangle size={32} />
      </div>
      
      <h3 className="text-xl font-bold text-text-main mb-2">Generation Failed</h3>
      <p className="text-text-muted mb-6 max-w-md">
        {error}
      </p>
      
      <button 
        onClick={onRetry}
        className="px-6 py-2 bg-surface hover:bg-surface-light border border-white/10 text-white font-medium rounded-lg transition-colors flex items-center gap-2"
      >
        <RefreshCw size={18} />
        Try Again
      </button>
    </div>
  );
};
