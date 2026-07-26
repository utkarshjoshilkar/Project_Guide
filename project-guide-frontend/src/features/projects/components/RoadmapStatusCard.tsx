/**
 * Purpose: Roadmap Generation Controller Card.
 * Responsibilities: Orchestrates the generation states (IDLE, GENERATING, SUCCESS, ERROR) inside the Project Details view.
 * Dependencies: react, useRoadmapGeneration hook, Generation UI components
 * Future extensibility: Link directly to the actual roadmap milestones view.
 */

import React from 'react';
import { useRoadmapGeneration } from '@/features/projects/hooks/useRoadmapGeneration';
import { GenerationProgress } from './GenerationProgress';
import { GenerationSuccess } from './GenerationSuccess';
import { GenerationError } from './GenerationError';
import { Bot, Sparkles } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

interface RoadmapStatusCardProps {
  projectId: number;
}

export const RoadmapStatusCard = ({ projectId }: RoadmapStatusCardProps) => {
  const { 
    status, 
    roadmap, 
    error, 
    currentLoadingMessage, 
    generateRoadmap, 
    retry 
  } = useRoadmapGeneration(projectId);
  
  const navigate = useNavigate();

  const handleViewRoadmap = () => {
    // In Phase 7, this will route to `/roadmaps/${roadmap?.id}` or `/projects/${projectId}/roadmap`
    // For now, it's just a placeholder route.
    navigate(`/dashboard`);
  };

  return (
    <div className="glass-card p-6 md:p-8 relative overflow-hidden min-h-[250px] flex flex-col justify-center transition-all duration-500">
      
      {status === 'INITIAL_LOADING' && (
        <div className="flex flex-col items-center justify-center text-text-muted">
          <div className="w-8 h-8 border-2 border-primary/50 border-t-transparent rounded-full animate-spin mb-4"></div>
          <p>Checking roadmap status...</p>
        </div>
      )}

      {status === 'IDLE' && (
        <div className="flex flex-col items-center justify-center text-center animate-in fade-in duration-300">
          <div className="w-16 h-16 bg-primary/10 rounded-full flex items-center justify-center text-primary-light mb-6">
            <Bot size={32} />
          </div>
          <h2 className="text-xl font-bold text-text-main mb-2">Ready to generate Roadmap?</h2>
          <p className="text-text-muted mb-6 max-w-md">
            Our AI will analyze your project scope, tech stack, and difficulty level to instantly create a step-by-step milestone plan.
          </p>
          <button 
            onClick={generateRoadmap}
            className="px-6 py-3 bg-primary hover:bg-primary-light text-white font-medium rounded-lg shadow-lg shadow-primary/20 transition-transform hover:scale-105 flex items-center gap-2 group"
          >
            <Sparkles size={18} className="group-hover:animate-pulse" />
            Generate AI Roadmap
          </button>
        </div>
      )}

      {status === 'GENERATING' && (
        <GenerationProgress message={currentLoadingMessage} />
      )}

      {status === 'SUCCESS' && roadmap && (
        <GenerationSuccess roadmap={roadmap} onViewRoadmap={handleViewRoadmap} />
      )}

      {status === 'ERROR' && (
        <GenerationError error={error || 'An unknown error occurred'} onRetry={retry} />
      )}
      
    </div>
  );
};
