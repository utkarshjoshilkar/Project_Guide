/**
 * Purpose: Dashboard Empty State Component.
 * Responsibilities: Prompts the user to create their first project if they have none.
 * Dependencies: react, react-router-dom, lucide-react
 * Future extensibility: Add more specific empty states (no milestones, no tasks).
 */

import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Rocket } from 'lucide-react';

export const EmptyState = () => {
  const navigate = useNavigate();

  return (
    <div className="glass-card p-12 flex flex-col items-center justify-center text-center mt-8">
      <div className="w-20 h-20 bg-accent/20 rounded-full flex items-center justify-center mb-6 text-accent">
        <Rocket size={40} />
      </div>
      <h2 className="text-2xl font-bold mb-4 text-text-main">Ready to start building?</h2>
      <p className="text-text-muted mb-8 max-w-md mx-auto">
        You don't have any projects yet. Create your first project to let the AI generate a personalized roadmap and task list.
      </p>
      <button 
        onClick={() => navigate('/projects/new')}
        className="px-6 py-3 bg-primary hover:bg-primary-light text-white font-medium rounded-lg transition-colors shadow-lg shadow-primary/20"
      >
        Create your first project
      </button>
    </div>
  );
};
