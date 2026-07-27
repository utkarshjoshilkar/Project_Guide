import React from 'react';
import { ResourceResponse } from '../types/resource';
import { ResourceTypeBadge } from './ResourceTypeBadge';
import { ExternalLink, Edit2, Trash2, Calendar } from 'lucide-react';

interface ResourceCardProps {
  resource: ResourceResponse;
  onEdit: (resource: ResourceResponse) => void;
  onDelete: (resource: ResourceResponse) => void;
}

export const ResourceCard: React.FC<ResourceCardProps> = ({ resource, onEdit, onDelete }) => {
  const formattedDate = new Date(resource.createdAt).toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric'
  });

  return (
    <div className="group relative bg-background border border-border hover:border-accent/50 rounded-lg p-4 transition-all hover:shadow-md flex flex-col sm:flex-row sm:items-start gap-4">
      <div className="flex-1 min-w-0">
        <div className="flex flex-wrap items-center gap-2 mb-1">
          <ResourceTypeBadge type={resource.type} />
          <h4 className="text-sm font-semibold text-text-primary truncate">
            {resource.title}
          </h4>
        </div>
        
        {resource.description && (
          <p className="text-xs text-text-muted mt-2 line-clamp-2">
            {resource.description}
          </p>
        )}
        
        <div className="flex items-center gap-4 mt-3">
          <div className="flex items-center gap-1.5 text-xs text-text-muted">
            <Calendar className="w-3.5 h-3.5" />
            {formattedDate}
          </div>
          <a
            href={resource.url}
            target="_blank"
            rel="noopener noreferrer"
            className="flex items-center gap-1 text-xs font-medium text-accent hover:text-accent-light transition-colors"
          >
            Open Resource
            <ExternalLink className="w-3 h-3" />
          </a>
        </div>
      </div>

      <div className="flex items-center gap-2 sm:opacity-0 sm:group-hover:opacity-100 transition-opacity">
        <button
          onClick={() => onEdit(resource)}
          className="p-1.5 text-text-muted hover:text-text-primary hover:bg-background-light rounded-md transition-colors"
          title="Edit Resource"
        >
          <Edit2 className="w-4 h-4" />
        </button>
        <button
          onClick={() => onDelete(resource)}
          className="p-1.5 text-text-muted hover:text-red-400 hover:bg-red-400/10 rounded-md transition-colors"
          title="Delete Resource"
        >
          <Trash2 className="w-4 h-4" />
        </button>
      </div>
    </div>
  );
};
