import React from 'react';
import { FileQuestion } from 'lucide-react';

interface EmptyStateProps {
  title: string;
  message: string;
  icon?: React.ReactNode;
  action?: React.ReactNode;
}

const EmptyState: React.FC<EmptyStateProps> = ({ title, message, icon, action }) => {
  return (
    <div className="flex flex-col items-center justify-center p-12 text-center h-full min-h-[300px] glass-card">
      <div className="text-accent mb-4">
        {icon || <FileQuestion size={48} />}
      </div>
      <h3 className="text-xl font-bold text-text-main mb-2">{title}</h3>
      <p className="text-text-muted mb-6 max-w-md">{message}</p>
      {action && <div>{action}</div>}
    </div>
  );
};

export default EmptyState;
