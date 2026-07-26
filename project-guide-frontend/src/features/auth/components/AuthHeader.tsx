/**
 * Purpose: Header for Auth forms.
 * Responsibilities: Renders title and subtitle uniformly.
 * Dependencies: react
 * Future extensibility: Accept an icon or logo prop.
 */

import React from 'react';

interface AuthHeaderProps {
  title: string;
  subtitle: string;
}

export const AuthHeader = ({ title, subtitle }: AuthHeaderProps) => {
  return (
    <div className="text-center mb-8">
      <h1 className="text-3xl font-bold text-text-main mb-2 tracking-tight">{title}</h1>
      <p className="text-text-muted text-sm">{subtitle}</p>
    </div>
  );
};
