/**
 * Purpose: Project Status Badge Component.
 * Responsibilities: Visually indicates the status of a project.
 * Dependencies: react
 * Future extensibility: Allow custom colors via props.
 */

import React from 'react';
import { CircleDashed, CheckCircle, Code, PauseCircle } from 'lucide-react';

interface ProjectStatusBadgeProps {
  status: string;
}

export const ProjectStatusBadge = ({ status }: ProjectStatusBadgeProps) => {
  let colorClass = '';
  let Icon = Code;
  let label = status.replace('_', ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase());

  switch (status) {
    case 'COMPLETED':
      colorClass = 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20';
      Icon = CheckCircle;
      break;
    case 'IN_PROGRESS':
      colorClass = 'bg-accent-light/10 text-accent-light border-accent-light/20';
      Icon = CircleDashed;
      break;
    case 'ON_HOLD':
      colorClass = 'bg-orange-500/10 text-orange-400 border-orange-500/20';
      Icon = PauseCircle;
      break;
    case 'IDEA_SUBMITTED':
    default:
      colorClass = 'bg-primary-light/10 text-primary-light border-primary-light/20';
      Icon = Code;
      break;
  }

  return (
    <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium border ${colorClass}`}>
      <Icon size={12} />
      {label}
    </span>
  );
};
