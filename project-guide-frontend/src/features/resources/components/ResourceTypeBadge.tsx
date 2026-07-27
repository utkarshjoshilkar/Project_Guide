import React from 'react';
import { ResourceType } from '../types/resource';
import { PlayCircle, FileText, BookOpen, Book, Code2, Lightbulb, Link } from 'lucide-react';

interface ResourceTypeBadgeProps {
  type: ResourceType;
}

export const ResourceTypeBadge: React.FC<ResourceTypeBadgeProps> = ({ type }) => {
  const getConfig = () => {
    switch (type) {
      case 'YOUTUBE':
        return { icon: PlayCircle, color: 'text-red-500', bg: 'bg-red-500/10', border: 'border-red-500/20', label: 'Video' };
      case 'ARTICLE':
        return { icon: FileText, color: 'text-blue-500', bg: 'bg-blue-500/10', border: 'border-blue-500/20', label: 'Article' };
      case 'DOCUMENTATION':
        return { icon: BookOpen, color: 'text-emerald-500', bg: 'bg-emerald-500/10', border: 'border-emerald-500/20', label: 'Docs' };
      case 'GITHUB':
        return { icon: Code2, color: 'text-gray-300', bg: 'bg-gray-700', border: 'border-gray-600', label: 'Repository' };
      case 'COURSE':
        return { icon: Lightbulb, color: 'text-purple-500', bg: 'bg-purple-500/10', border: 'border-purple-500/20', label: 'Course' };
      case 'BOOK':
        return { icon: Book, color: 'text-amber-500', bg: 'bg-amber-500/10', border: 'border-amber-500/20', label: 'Book' };
      default:
        return { icon: Link, color: 'text-accent', bg: 'bg-accent/10', border: 'border-accent/20', label: 'Link' };
    }
  };

  const { icon: Icon, color, bg, border, label } = getConfig();

  return (
    <div className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium border ${bg} ${border} ${color}`}>
      <Icon className="w-3.5 h-3.5" />
      {label}
    </div>
  );
};
