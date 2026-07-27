import React from 'react';
import { AlertTriangle } from 'lucide-react';

interface ErrorStateProps {
  title?: string;
  message: string;
  onRetry?: () => void;
}

const ErrorState: React.FC<ErrorStateProps> = ({ title = 'An error occurred', message, onRetry }) => {
  return (
    <div className="flex flex-col items-center justify-center p-8 text-center h-full min-h-[200px]">
      <div className="text-red-500 mb-4">
        <AlertTriangle size={48} />
      </div>
      <h3 className="text-xl font-bold text-text-main mb-2">{title}</h3>
      <p className="text-text-muted mb-6">{message}</p>
      {onRetry && (
        <button
          onClick={onRetry}
          className="px-6 py-2 bg-accent hover:bg-accent-light text-white rounded-lg transition-colors shadow-lg shadow-accent/20"
        >
          Try Again
        </button>
      )}
    </div>
  );
};

export default ErrorState;
