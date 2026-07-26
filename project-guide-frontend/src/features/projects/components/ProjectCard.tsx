/**
 * Purpose: Project Card Component.
 * Responsibilities: Renders a single project summary in a grid.
 * Dependencies: react, react-router-dom, project types, badges
 * Future extensibility: Add context menu for quick edit/delete.
 */

import React from 'react';
import { useNavigate } from 'react-router-dom';
import { ProjectResponse } from '@/types/project';
import { ProjectStatusBadge } from './ProjectStatusBadge';
import { DifficultyBadge } from './DifficultyBadge';
import { Calendar, Layers } from 'lucide-react';

interface ProjectCardProps {
  project: ProjectResponse;
}

export const ProjectCard = ({ project }: ProjectCardProps) => {
  const navigate = useNavigate();

  return (
    <div 
      onClick={() => navigate(`/projects/${project.id}`)}
      className="glass-card p-5 flex flex-col h-full hover:-translate-y-1 transition-transform duration-300 cursor-pointer group hover:border-primary/50"
    >
      <div className="flex justify-between items-start mb-3">
        <DifficultyBadge difficulty={project.skillLevel} />
        <ProjectStatusBadge status={project.status} />
      </div>
      
      <h3 className="text-lg font-bold text-text-main mb-2 line-clamp-1 group-hover:text-primary-light transition-colors">
        {project.title}
      </h3>
      
      <p className="text-sm text-text-muted mb-4 line-clamp-2 flex-1">
        {project.description}
      </p>
      
      <div className="pt-4 border-t border-white/5 flex items-center justify-between text-xs text-text-muted">
        <div className="flex items-center gap-1.5">
          <Layers size={14} className="text-primary" />
          <span className="truncate max-w-[100px]">{project.domain}</span>
        </div>
        <div className="flex items-center gap-1.5">
          <Calendar size={14} className="text-accent" />
          <span>{project.deadline}</span>
        </div>
      </div>
    </div>
  );
};
