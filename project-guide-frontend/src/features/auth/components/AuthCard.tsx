/**
 * Purpose: Reusable container for Auth forms.
 * Responsibilities: Provides glassmorphism styling and centers content.
 * Dependencies: react
 * Future extensibility: Accept custom padding or max-width props.
 */

import React, { ReactNode } from 'react';

interface AuthCardProps {
  children: ReactNode;
}

export const AuthCard = ({ children }: AuthCardProps) => {
  return (
    <div className="glass-card p-8 w-full max-w-md mx-auto relative overflow-hidden">
      {/* Decorative gradient blob */}
      <div className="absolute -top-20 -right-20 w-40 h-40 bg-accent/30 rounded-full blur-3xl pointer-events-none"></div>
      <div className="absolute -bottom-20 -left-20 w-40 h-40 bg-primary/30 rounded-full blur-3xl pointer-events-none"></div>
      
      <div className="relative z-10">
        {children}
      </div>
    </div>
  );
};
