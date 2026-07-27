import React from 'react';
import { BookOpen } from 'lucide-react';

interface EmptyResourcesProps {
  onAdd: () => void;
}

export const EmptyResources: React.FC<EmptyResourcesProps> = ({ onAdd }) => {
  return (
    <div className="flex flex-col items-center justify-center py-6 px-4 bg-background-light/30 border border-dashed border-border-light rounded-lg text-center">
      <div className="w-10 h-10 rounded-full bg-accent/10 flex items-center justify-center mb-3">
        <BookOpen className="w-5 h-5 text-accent" />
      </div>
      <h3 className="text-sm font-medium text-text-primary mb-1">No resources yet</h3>
      <p className="text-xs text-text-muted mb-4 max-w-[200px]">
        Add links, videos, or articles to help complete this task.
      </p>
      <button
        onClick={onAdd}
        className="text-xs font-medium bg-accent hover:bg-accent-light text-white px-3 py-1.5 rounded-md transition-colors"
      >
        Add Resource
      </button>
    </div>
  );
};
