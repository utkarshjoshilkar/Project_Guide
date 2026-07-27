import React from 'react';
import { FileQuestion } from 'lucide-react';

interface EmptyStateProps {
  message?: string;
}

export const EmptyState = ({ message = "No roadmap found." }: EmptyStateProps) => {
  return (
    <div className="flex flex-col items-center justify-center py-16 px-4 text-center glass-card">
      <div className="w-16 h-16 bg-surface/50 rounded-full flex items-center justify-center text-text-muted mb-4">
        <FileQuestion size={32} />
      </div>
      <h3 className="text-xl font-medium text-text-main mb-2">Roadmap Not Available</h3>
      <p className="text-text-muted max-w-sm">
        {message}
      </p>
    </div>
  );
};
