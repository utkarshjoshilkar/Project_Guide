import React from 'react';
import { ProgressIndicator } from './ProgressIndicator';

interface ProgressSummaryProps {
  completedTasks: number;
  totalTasks: number;
  title?: string;
}

export const ProgressSummary = ({ completedTasks, totalTasks, title = 'Task Progress' }: ProgressSummaryProps) => {
  const percentage = totalTasks > 0 ? (completedTasks / totalTasks) * 100 : 0;

  return (
    <div className="glass-card p-4 rounded-xl border border-white/10 bg-white/5">
      <div className="flex justify-between items-end mb-3">
        <h4 className="font-semibold text-text-main text-sm">{title}</h4>
        <div className="text-xs font-medium px-2 py-1 bg-surface rounded-md text-text-muted">
          {completedTasks} / {totalTasks} Completed
        </div>
      </div>
      <ProgressIndicator percentage={percentage} size="md" showLabel={true} />
    </div>
  );
};
