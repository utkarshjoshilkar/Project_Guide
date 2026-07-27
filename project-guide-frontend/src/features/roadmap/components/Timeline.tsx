import React from 'react';
import { MilestoneWithTasks } from '../hooks/useMilestones';
import { TaskResponse } from '@/types/roadmap';
import { MilestoneCard } from './MilestoneCard';

interface TimelineProps {
  milestones: MilestoneWithTasks[];
  onExpandMilestone: (milestoneId: number) => void;
  onExpandTask: (milestoneId: number, taskId: number) => void;
  onUpdateTask: (milestoneId: number, taskId: number, updatedTask: TaskResponse) => void;
}

export const Timeline = ({ milestones, onExpandMilestone, onExpandTask, onUpdateTask }: TimelineProps) => {
  if (milestones.length === 0) {
    return null;
  }

  return (
    <div className="relative pl-10 md:pl-12 my-8">
      {/* Vertical Timeline Line */}
      <div className="absolute left-[15px] top-8 bottom-8 w-[2px] bg-white/10 hidden sm:block"></div>
      
      <div className="space-y-6">
        {milestones.map(milestone => (
          <MilestoneCard 
            key={milestone.id} 
            milestone={milestone} 
            onExpandMilestone={onExpandMilestone}
            onExpandTask={onExpandTask}
            onUpdateTask={onUpdateTask}
          />
        ))}
      </div>
    </div>
  );
};
