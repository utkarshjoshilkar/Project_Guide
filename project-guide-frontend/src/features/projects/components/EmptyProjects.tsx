/**
 * Purpose: Empty Projects State Component.
 * Responsibilities: Prompts the user to create their first project.
 * Dependencies: react, react-router-dom, lucide-react
 * Future extensibility: N/A
 */

import React from 'react';
import { useNavigate } from 'react-router-dom';
import { FolderKanban } from 'lucide-react';

export const EmptyProjects = () => {
  const navigate = useNavigate();

  return (
    <div className="flex flex-col items-center justify-center py-20 text-center border border-dashed border-white/10 rounded-xl bg-surface/20">
      <div className="w-16 h-16 bg-surface rounded-full flex items-center justify-center mb-6 text-text-muted">
        <FolderKanban size={32} />
      </div>
      <h2 className="text-xl font-bold mb-2 text-text-main">No projects found</h2>
      <p className="text-text-muted mb-6 max-w-md mx-auto">
        Get started by creating a new project. We'll help you break it down into milestones and tasks.
      </p>
      <button 
        onClick={() => navigate('/projects/new')}
        className="px-6 py-2 bg-primary hover:bg-primary-light text-white font-medium rounded-lg transition-colors"
      >
        Create Project
      </button>
    </div>
  );
};
