import React from 'react';
import { RoadmapResponse } from '@/types/roadmap';
import { Calendar, Clock, Activity, Target } from 'lucide-react';
import { ProgressBadge } from './ProgressBadge';

interface RoadmapSummaryCardProps {
  roadmap: RoadmapResponse;
}

export const RoadmapSummaryCard = ({ roadmap }: RoadmapSummaryCardProps) => {
  const generatedDate = new Date(roadmap.generatedAt).toLocaleDateString('en-US', {
    month: 'long',
    day: 'numeric',
    year: 'numeric'
  });

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
      
      <div className="glass-card p-5 flex flex-col justify-center">
        <div className="flex items-center gap-3 text-text-muted mb-2">
          <Calendar size={18} />
          <span className="text-sm font-medium">Generated On</span>
        </div>
        <div className="text-lg font-semibold text-text-main">
          {generatedDate}
        </div>
      </div>

      <div className="glass-card p-5 flex flex-col justify-center">
        <div className="flex items-center gap-3 text-text-muted mb-2">
          <Clock size={18} />
          <span className="text-sm font-medium">Est. Duration</span>
        </div>
        <div className="text-lg font-semibold text-text-main">
          {roadmap.estimatedDurationWeeks} Weeks
        </div>
      </div>

      <div className="glass-card p-5 flex flex-col justify-center">
        <div className="flex items-center gap-3 text-text-muted mb-2">
          <Activity size={18} />
          <span className="text-sm font-medium">Status</span>
        </div>
        <div className="text-lg font-semibold text-text-main capitalize">
          {roadmap.status.toLowerCase()}
        </div>
      </div>

      <div className="glass-card p-5 flex flex-col justify-center bg-primary/5 border-primary/20">
        <div className="flex items-center justify-between mb-2">
          <div className="flex items-center gap-3 text-primary-light">
            <Target size={18} />
            <span className="text-sm font-medium">Overall Progress</span>
          </div>
          <ProgressBadge percentage={roadmap.progressPercentage} />
        </div>
        <div className="w-full bg-surface h-2 rounded-full overflow-hidden mt-2">
          <div 
            className="bg-primary h-full rounded-full transition-all duration-1000 ease-out"
            style={{ width: `${roadmap.progressPercentage}%` }}
          />
        </div>
      </div>

    </div>
  );
};
