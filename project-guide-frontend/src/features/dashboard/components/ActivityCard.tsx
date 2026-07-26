/**
 * Purpose: Dashboard Activity Component.
 * Responsibilities: Lists recent projects with their status.
 * Dependencies: react, react-router-dom, dashboard types, lucide-react
 * Future extensibility: Link directly to the project's roadmap view.
 */

import React from 'react';
import { ProjectResponse } from '@/types/dashboard';
import { useNavigate } from 'react-router-dom';
import { ArrowRight, CircleDashed, CheckCircle, Code } from 'lucide-react';

interface ActivityCardProps {
  recentProjects: ProjectResponse[];
}

export const ActivityCard = ({ recentProjects }: ActivityCardProps) => {
  const navigate = useNavigate();

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'COMPLETED': return <CheckCircle className="text-emerald-400" size={20} />;
      case 'IN_PROGRESS': return <CircleDashed className="text-accent-light" size={20} />;
      default: return <Code className="text-primary-light" size={20} />;
    }
  };

  const getStatusLabel = (status: string) => {
    return status.replace('_', ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase());
  };

  if (recentProjects.length === 0) return null;

  return (
    <div className="glass-card p-6 flex flex-col">
      <h2 className="text-xl font-bold text-text-main mb-6">Recent Activity</h2>
      <div className="space-y-4 flex-1">
        {recentProjects.map((project) => (
          <div 
            key={project.id}
            onClick={() => navigate(`/projects/${project.id}`)}
            className="group flex items-center justify-between p-4 rounded-lg bg-surface/30 border border-white/5 hover:bg-surface/60 hover:border-primary/50 cursor-pointer transition-all duration-200"
          >
            <div className="flex items-center gap-4">
              <div className="p-2 rounded-full bg-surface">
                {getStatusIcon(project.status)}
              </div>
              <div>
                <h3 className="font-semibold text-text-main group-hover:text-primary-light transition-colors">
                  {project.title}
                </h3>
                <p className="text-sm text-text-muted">{getStatusLabel(project.status)}</p>
              </div>
            </div>
            <ArrowRight size={18} className="text-text-muted opacity-0 group-hover:opacity-100 group-hover:text-primary-light transition-all transform translate-x-[-10px] group-hover:translate-x-0" />
          </div>
        ))}
      </div>
      <div className="mt-6 pt-4 border-t border-white/5 flex justify-center">
        <button 
          onClick={() => navigate('/projects')}
          className="text-sm text-text-muted hover:text-white transition-colors"
        >
          View all projects
        </button>
      </div>
    </div>
  );
};
