/**
 * Purpose: Roadmap Generation Success Component.
 * Responsibilities: Displays the success summary of the generated roadmap.
 * Dependencies: react, lucide-react, roadmap types
 * Future extensibility: N/A
 */

import React from 'react';
import { RoadmapResponse } from '@/types/roadmap';
import { CheckCircle, Clock, Calendar } from 'lucide-react';

interface GenerationSuccessProps {
  roadmap: RoadmapResponse;
  onViewRoadmap: () => void;
}

export const GenerationSuccess = ({ roadmap, onViewRoadmap }: GenerationSuccessProps) => {
  return (
    <div className="flex flex-col items-center justify-center py-6 text-center animate-in fade-in zoom-in duration-500">
      <div className="w-16 h-16 bg-emerald-500/20 rounded-full flex items-center justify-center text-emerald-400 mb-6 ring-4 ring-emerald-500/10">
        <CheckCircle size={32} />
      </div>
      
      <h3 className="text-xl font-bold text-text-main mb-2">Roadmap Generated Successfully!</h3>
      <p className="text-text-muted mb-8 max-w-md">
        The AI has analyzed your profile and project requirements to construct a tailored learning path.
      </p>
      
      <div className="grid grid-cols-2 gap-4 w-full max-w-sm mb-8">
        <div className="bg-surface/50 border border-white/5 rounded-xl p-4 flex flex-col items-center">
          <Clock size={20} className="text-primary-light mb-2" />
          <p className="text-xs text-text-muted mb-1">Duration</p>
          <p className="font-bold text-white">{roadmap.estimatedDurationWeeks} Weeks</p>
        </div>
        
        <div className="bg-surface/50 border border-white/5 rounded-xl p-4 flex flex-col items-center">
          <Calendar size={20} className="text-accent-light mb-2" />
          <p className="text-xs text-text-muted mb-1">Generated</p>
          <p className="font-bold text-white">{new Date(roadmap.generatedAt).toLocaleDateString()}</p>
        </div>
      </div>
      
      <button 
        onClick={onViewRoadmap}
        className="px-8 py-3 bg-emerald-500 hover:bg-emerald-600 text-white font-medium rounded-lg shadow-lg shadow-emerald-500/20 transition-all hover:scale-105"
      >
        View Full Roadmap
      </button>
    </div>
  );
};
