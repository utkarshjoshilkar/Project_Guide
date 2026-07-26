/**
 * Purpose: Reusable Select Dropdown for Profile forms.
 * Responsibilities: Renders label, select field, and validation error messages.
 * Dependencies: react
 * Future extensibility: Support custom styled dropdown menus.
 */

import React, { SelectHTMLAttributes, forwardRef } from 'react';

interface ProfileSelectProps extends SelectHTMLAttributes<HTMLSelectElement> {
  label: string;
  error?: string;
  options: { label: string; value: string | number }[];
}

export const ProfileSelect = forwardRef<HTMLSelectElement, ProfileSelectProps>(
  ({ label, error, className = '', options, ...props }, ref) => {
    return (
      <div className={`mb-4 ${className}`}>
        <label className="block text-sm font-medium text-text-muted mb-1 ml-1">
          {label}
        </label>
        <select
          ref={ref}
          className={`
            w-full px-4 py-3 bg-surface/50 border rounded-lg text-text-main appearance-none
            focus:outline-none focus:ring-2 transition-all duration-200
            ${error ? 'border-red-500 focus:ring-red-500/50' : 'border-white/10 focus:ring-primary'}
          `}
          {...props}
        >
          <option value="" disabled className="bg-surface text-text-muted">Select an option</option>
          {options.map((opt) => (
            <option key={opt.value} value={opt.value} className="bg-surface text-text-main">
              {opt.label}
            </option>
          ))}
        </select>
        {error && <p className="mt-1 text-sm text-red-400 ml-1">{error}</p>}
      </div>
    );
  }
);

ProfileSelect.displayName = 'ProfileSelect';
