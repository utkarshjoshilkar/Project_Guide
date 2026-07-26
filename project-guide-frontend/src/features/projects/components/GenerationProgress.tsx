/**
 * Purpose: Roadmap Generation Progress UI.
 * Responsibilities: Displays the rotating loading messages and animation.
 * Dependencies: react, lucide-react
 * Future extensibility: Implement actual progress percentage if backend streams it.
 */

import React from 'react';
import { Bot, Sparkles } from 'lucide-react';

interface GenerationProgressProps {
  message: string;
}

export const GenerationProgress = ({ message }: GenerationProgressProps) => {
  return (
    <div className="flex flex-col items-center justify-center py-8 text-center animate-in fade-in zoom-in duration-500">
      
      <div className="relative mb-6">
        {/* Pulsing rings */}
        <div className="absolute inset-0 rounded-full bg-primary/20 animate-ping opacity-75"></div>
        <div className="absolute inset-[-10px] rounded-full bg-accent/20 animate-pulse delay-150"></div>
        
        {/* Core Icon */}
        <div className="relative w-16 h-16 bg-surface border border-primary/50 rounded-full flex items-center justify-center text-primary-light z-10 shadow-[0_0_30px_rgba(var(--color-primary),0.3)]">
          <Bot size={32} />
          <Sparkles size={16} className="absolute -top-1 -right-1 text-accent animate-spin-slow" />
        </div>
      </div>

      <h3 className="text-xl font-bold text-text-main mb-2">AI is working...</h3>
      <p className="text-text-muted h-6 transition-all duration-300">{message}</p>
      
      {/* Progress Bar (Indeterminate) */}
      <div className="w-64 h-1.5 bg-surface rounded-full mt-6 overflow-hidden relative">
        <div className="absolute top-0 left-0 h-full bg-gradient-to-r from-primary to-accent w-1/3 animate-indeterminate rounded-full"></div>
      </div>
    </div>
  );
};
