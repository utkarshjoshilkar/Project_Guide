import React from 'react';
import { ExpandCollapse } from './ExpandCollapse';
import { ResourceCard } from './ResourceCard';
import { TaskWithResources } from '../hooks/useMilestones';
import { TaskResponse } from '@/types/roadmap';
import { Clock, AlertCircle } from 'lucide-react';
import { TaskProgressCheckbox } from '@/features/progress/components/TaskProgressCheckbox';

interface TaskCardProps {
  task: TaskWithResources;
  milestoneId: number;
  onExpand: (milestoneId: number, taskId: number) => void;
  onUpdateTask: (milestoneId: number, taskId: number, updatedTask: TaskResponse) => void;
}

export const TaskCard = ({ task, milestoneId, onExpand, onUpdateTask }: TaskCardProps) => {
  const handleExpand = () => {
    if (!task.resources && !task.loadingResources) {
      onExpand(milestoneId, task.id);
    }
  };

  const getPriorityColor = (priority?: string) => {
    switch (priority) {
      case 'HIGH': return 'text-red-400 bg-red-400/10 border-red-400/20';
      case 'MEDIUM': return 'text-yellow-400 bg-yellow-400/10 border-yellow-400/20';
      case 'LOW': return 'text-green-400 bg-green-400/10 border-green-400/20';
      default: return 'text-text-muted bg-surface border-white/10';
    }
  };

  const isCompleted = task.status === 'DONE';

  const TaskTitle = (
    <div className="flex items-start gap-4 py-1">
      <div className="mt-1">
        <TaskProgressCheckbox 
          task={task} 
          onUpdate={(updatedTask) => onUpdateTask(milestoneId, task.id, updatedTask)} 
        />
      </div>
      <div className="flex-1">
        <div className="flex items-center justify-between gap-4 flex-wrap mb-1">
          <h4 className={`text-base font-medium ${isCompleted ? 'line-through text-text-muted' : 'text-text-main'}`}>
            {task.title}
          </h4>
          <div className="flex items-center gap-2">
            {task.priority && (
              <span className={`px-2 py-0.5 text-xs rounded-full border ${getPriorityColor(task.priority)}`}>
                {task.priority}
              </span>
            )}
            <span className="flex items-center gap-1 text-xs text-text-muted bg-background/50 px-2 py-1 rounded-full border border-white/5">
              <Clock size={12} />
              {task.estimatedHours}h
            </span>
          </div>
        </div>
        <p className="text-sm text-text-muted leading-relaxed">
          {task.description}
        </p>
      </div>
    </div>
  );

  return (
    <ExpandCollapse title={TaskTitle} onExpand={handleExpand}>
      <div className="pl-9 space-y-3">
        <h5 className="text-sm font-medium text-text-main mb-3">Learning Resources</h5>
        
        {task.loadingResources && (
          <div className="animate-pulse space-y-2">
            <div className="h-14 bg-surface rounded-lg"></div>
            <div className="h-14 bg-surface rounded-lg"></div>
          </div>
        )}

        {task.errorResources && (
          <div className="flex items-center gap-2 text-red-400 text-sm bg-red-400/10 p-3 rounded-lg border border-red-400/20">
            <AlertCircle size={16} />
            {task.errorResources}
          </div>
        )}

        {!task.loadingResources && !task.errorResources && (!task.resources || task.resources.length === 0) && (
          <p className="text-sm text-text-muted italic">No specific resources provided for this task.</p>
        )}

        {task.resources && task.resources.length > 0 && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
            {task.resources.map(resource => (
              <ResourceCard key={resource.id} resource={resource} />
            ))}
          </div>
        )}
      </div>
    </ExpandCollapse>
  );
};
