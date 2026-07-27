import React, { useState } from 'react';
import { ExpandCollapse } from './ExpandCollapse';
import { ResourceList } from '@/features/resources/components/ResourceList';
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
  const [hasExpanded, setHasExpanded] = useState(false);

  const handleExpand = () => {
    setHasExpanded(true);
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
      <div className="pl-9 pb-2">
        {hasExpanded && <ResourceList taskId={task.id} />}
      </div>
    </ExpandCollapse>
  );
};
