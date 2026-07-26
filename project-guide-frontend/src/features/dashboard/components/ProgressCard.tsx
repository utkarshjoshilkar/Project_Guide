/**
 * Purpose: Dashboard Progress Component.
 * Responsibilities: Renders aggregate progress bars (Tasks, Milestones, Learning).
 * Dependencies: react, ProgressBar
 * Future extensibility: Swap progress bars for actual charts if data density increases.
 */

import React from 'react';
import { ProgressBar } from './ProgressBar';

interface ProgressCardProps {
  overallProgressPercentage: number;
  totalTasks: number;
  completedTasks: number;
  totalMilestones: number;
  completedMilestones: number;
  weeklyHours: number;
}

export const ProgressCard = ({
  overallProgressPercentage,
  totalTasks,
  completedTasks,
  totalMilestones,
  completedMilestones,
  weeklyHours
}: ProgressCardProps) => {
  
  const taskProgress = totalTasks > 0 ? (completedTasks / totalTasks) * 100 : 0;
  const milestoneProgress = totalMilestones > 0 ? (completedMilestones / totalMilestones) * 100 : 0;
  
  // Fake a learning hours progress based on a 20 hr/week theoretical max just for visuals
  const hourProgress = Math.min((weeklyHours / 20) * 100, 100);

  return (
    <div className="glass-card p-6 flex flex-col h-full">
      <h2 className="text-xl font-bold text-text-main mb-6">Learning Metrics</h2>
      
      <div className="space-y-6 flex-1 flex flex-col justify-center">
        <div>
          <ProgressBar 
            progress={overallProgressPercentage} 
            label="Overall Roadmap Progress" 
            colorClass="bg-primary-light"
          />
        </div>
        
        <div>
          <ProgressBar 
            progress={taskProgress} 
            label={`Tasks Completed (${completedTasks}/${totalTasks})`} 
            colorClass="bg-accent-light"
          />
        </div>

        <div>
          <ProgressBar 
            progress={milestoneProgress} 
            label={`Milestones Reached (${completedMilestones}/${totalMilestones})`} 
            colorClass="bg-emerald-400"
          />
        </div>

        <div>
          <ProgressBar 
            progress={hourProgress} 
            label={`Weekly Commitment (${weeklyHours} hrs)`} 
            colorClass="bg-purple-400"
            showValue={false}
          />
        </div>
      </div>
    </div>
  );
};
