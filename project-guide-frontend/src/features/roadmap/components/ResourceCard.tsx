import React from 'react';
import { ResourceResponse } from '@/types/roadmap';
import { ExternalLink, BookOpen, Video, Code, FileText, Link } from 'lucide-react';

interface ResourceCardProps {
  resource: ResourceResponse;
}

const getIconForType = (type: string) => {
  switch (type) {
    case 'ARTICLE': return <FileText size={16} />;
    case 'VIDEO': return <Video size={16} />;
    case 'COURSE': return <BookOpen size={16} />;
    case 'DOCUMENTATION': return <Code size={16} />;
    default: return <Link size={16} />;
  }
};

export const ResourceCard = ({ resource }: ResourceCardProps) => {
  return (
    <a 
      href={resource.link} 
      target="_blank" 
      rel="noopener noreferrer"
      className="flex items-center justify-between p-3 rounded-lg border border-white/5 bg-background/50 hover:bg-surface/50 hover:border-primary/30 transition-all group"
    >
      <div className="flex items-center gap-3">
        <div className="p-2 bg-surface rounded-md text-primary-light group-hover:text-primary transition-colors">
          {getIconForType(resource.type)}
        </div>
        <div>
          <h4 className="text-sm font-medium text-text-main group-hover:text-primary-light transition-colors">
            {resource.title}
          </h4>
          <span className="text-xs text-text-muted capitalize">
            {resource.type.toLowerCase()}
          </span>
        </div>
      </div>
      <ExternalLink size={16} className="text-text-muted group-hover:text-primary-light transition-colors" />
    </a>
  );
};
