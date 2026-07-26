/**
 * Purpose: Difficulty Badge Component.
 * Responsibilities: Visually indicates the difficulty/skill level of a project.
 * Dependencies: react
 * Future extensibility: N/A
 */

import React from 'react';

interface DifficultyBadgeProps {
  difficulty: string;
}

export const DifficultyBadge = ({ difficulty }: DifficultyBadgeProps) => {
  let colorClass = '';
  
  const level = difficulty.toUpperCase();
  
  if (level.includes('BEGINNER')) {
    colorClass = 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20';
  } else if (level.includes('INTERMEDIATE')) {
    colorClass = 'bg-accent-light/10 text-accent-light border-accent-light/20';
  } else if (level.includes('ADVANCED') || level.includes('EXPERT')) {
    colorClass = 'bg-orange-500/10 text-orange-400 border-orange-500/20';
  } else {
    colorClass = 'bg-surface-light/50 text-text-muted border-white/10';
  }

  return (
    <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium border ${colorClass}`}>
      {difficulty}
    </span>
  );
};
