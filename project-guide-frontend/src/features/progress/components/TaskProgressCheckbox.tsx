import React from 'react';
import { useTaskProgress } from '../hooks/useTaskProgress';
import { TaskResponse } from '@/types/roadmap';
import { Check, Loader2 } from 'lucide-react';

interface TaskProgressCheckboxProps {
  task: TaskResponse;
  onUpdate: (updatedTask: TaskResponse) => void;
  disabled?: boolean;
}

export const TaskProgressCheckbox = ({ task, onUpdate, disabled = false }: TaskProgressCheckboxProps) => {
  const isDone = task.status === 'DONE';
  const { markComplete, loading } = useTaskProgress({
    onSuccess: (updatedTask) => {
      onUpdate(updatedTask);
    }
  });

  const handleClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    if (disabled || loading || isDone) return;
    markComplete(task.id);
  };

  return (
    <button
      onClick={handleClick}
      disabled={disabled || loading || isDone}
      className={`
        w-6 h-6 rounded-md border flex items-center justify-center transition-all duration-300
        ${isDone 
          ? 'bg-primary border-primary text-white scale-105 shadow-md shadow-primary/20' 
          : 'border-white/20 hover:border-primary hover:bg-primary/10'
        }
        ${(disabled || loading) ? 'opacity-50 cursor-not-allowed' : 'cursor-pointer'}
        ${loading ? 'animate-pulse' : ''}
      `}
      aria-label={isDone ? 'Completed' : 'Mark as complete'}
    >
      {loading ? (
        <Loader2 size={14} className="animate-spin text-primary" />
      ) : (
        <Check 
          size={14} 
          className={`transition-all duration-300 ${isDone ? 'scale-100 opacity-100' : 'scale-50 opacity-0 text-primary'}`}
          strokeWidth={isDone ? 3 : 2}
        />
      )}
    </button>
  );
};
