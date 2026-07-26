/**
 * Purpose: Reusable Text Input for Profile forms.
 * Responsibilities: Renders label, input field, and validation error messages.
 * Dependencies: react
 * Future extensibility: Support text areas or icons.
 */

import React, { InputHTMLAttributes, forwardRef } from 'react';

interface ProfileInputProps extends InputHTMLAttributes<HTMLInputElement | HTMLTextAreaElement> {
  label: string;
  error?: string;
  textarea?: boolean;
}

export const ProfileInput = forwardRef<HTMLInputElement | HTMLTextAreaElement, ProfileInputProps>(
  ({ label, error, className = '', textarea, ...props }, ref) => {
    const baseClasses = `
      w-full px-4 py-3 bg-surface/50 border rounded-lg text-text-main placeholder-text-muted/50
      focus:outline-none focus:ring-2 transition-all duration-200
      ${error ? 'border-red-500 focus:ring-red-500/50' : 'border-white/10 focus:ring-primary'}
    `;

    return (
      <div className={`mb-4 ${className}`}>
        <label className="block text-sm font-medium text-text-muted mb-1 ml-1">
          {label}
        </label>
        {textarea ? (
          <textarea
            ref={ref as any}
            className={`${baseClasses} min-h-[100px] resize-y`}
            {...(props as any)}
          />
        ) : (
          <input
            ref={ref as any}
            className={baseClasses}
            {...(props as any)}
          />
        )}
        {error && <p className="mt-1 text-sm text-red-400 ml-1">{error}</p>}
      </div>
    );
  }
);

ProfileInput.displayName = 'ProfileInput';
