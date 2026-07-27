import React from 'react';
import { ExpandCollapse } from './ExpandCollapse';
import { TaskCard } from './TaskCard';
import { ProgressBadge } from './ProgressBadge';
import { MilestoneWithTasks } from '../hooks/useMilestones';
import { TaskResponse } from '@/types/roadmap';
import { Clock, AlertCircle } from 'lucide-react';

interface MilestoneCardProps {
  milestone: MilestoneWithTasks;
  onExpandMilestone: (milestoneId: number) => void;
  onExpandTask: (milestoneId: number, taskId: number) => void;
  onUpdateTask: (milestoneId: number, taskId: number, updatedTask: TaskResponse) => void;
}

export const MilestoneCard = ({ milestone, onExpandMilestone, onExpandTask, onUpdateTask }: MilestoneCardProps) => {
  const handleExpand = () => {
    if (!milestone.tasks && !milestone.loadingTasks) {
      onExpandMilestone(milestone.id);
    }
  };

  const isCompleted = milestone.status === 'COMPLETED';

  const MilestoneTitle = (
    <div className="flex items-center justify-between gap-4 py-2">
      <div className="flex-1">
        <h3 className={`text-lg font-bold mb-1 ${isCompleted ? 'text-text-muted' : 'text-text-main'}`}>
          {milestone.title}
        </h3>
        <p className="text-sm text-text-muted line-clamp-1">
          {milestone.description}
        </p>
      </div>
      <div className="flex items-center gap-4 flex-shrink-0">
        <span className="hidden sm:flex items-center gap-1 text-sm text-text-muted">
          <Clock size={14} />
          {milestone.estimatedDurationHours}h
        </span>
        <ProgressBadge percentage={milestone.completionPercentage || 0} />
      </div>
    </div>
  );

  return (
    <div className="relative">
      {/* Timeline connector dot */}
      <div className={`absolute -left-[45px] top-8 w-4 h-4 rounded-full border-4 border-background z-10 
        ${isCompleted ? 'bg-green-400' : 'bg-primary'}
      `} />
      
      <div className={`glass-card p-2 border-l-4 ${isCompleted ? 'border-l-green-400/50' : 'border-l-primary'}`}>
        <ExpandCollapse title={MilestoneTitle} onExpand={handleExpand}>
          <div className="pt-2 pb-4 px-2 space-y-4">
            
            <div className="mb-6">
              <h4 className="text-sm font-medium text-text-main mb-2">Milestone Description</h4>
              <p className="text-sm text-text-muted leading-relaxed">
                {milestone.description}
              </p>
            </div>

            <div className="space-y-3">
              <h4 className="text-sm font-medium text-text-main">Tasks</h4>
              
              {milestone.loadingTasks && (
                <div className="space-y-3 animate-pulse">
                  <div className="h-16 bg-surface rounded-xl"></div>
                  <div className="h-16 bg-surface rounded-xl"></div>
                  <div className="h-16 bg-surface rounded-xl"></div>
                </div>
              )}

              {milestone.errorTasks && (
                <div className="flex items-center gap-2 text-red-400 text-sm bg-red-400/10 p-4 rounded-xl border border-red-400/20">
                  <AlertCircle size={16} />
                  {milestone.errorTasks}
                </div>
              )}

              {!milestone.loadingTasks && !milestone.errorTasks && milestone.tasks?.length === 0 && (
                <p className="text-sm text-text-muted italic">No tasks found for this milestone.</p>
              )}

              {milestone.tasks && milestone.tasks.map(task => (
                <TaskCard 
                  key={task.id} 
                  task={task} 
                  milestoneId={milestone.id} 
                  onExpand={onExpandTask} 
                  onUpdateTask={onUpdateTask}
                />
              ))}
            </div>

          </div>
        </ExpandCollapse>
      </div>
    </div>
  );
};
