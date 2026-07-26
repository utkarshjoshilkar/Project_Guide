/**
 * Purpose: Reusable Text Input for Auth forms.
 * Responsibilities: Renders label, input field, and validation error messages.
 * Dependencies: react
 * Future extensibility: Support icons (left/right) or different variants.
 */

import React, { InputHTMLAttributes, forwardRef } from 'react';

interface AuthInputProps extends InputHTMLAttributes<HTMLInputElement> {
  label: string;
  error?: string;
}

export const AuthInput = forwardRef<HTMLInputElement, AuthInputProps>(
  ({ label, error, className = '', ...props }, ref) => {
    return (
      <div className={`mb-4 ${className}`}>
        <label className="block text-sm font-medium text-text-muted mb-1 ml-1">
          {label}
        </label>
        <input
          ref={ref}
          className={`
            w-full px-4 py-3 bg-surface/50 border rounded-lg text-text-main placeholder-text-muted/50
            focus:outline-none focus:ring-2 transition-all duration-200
            ${
              error
                ? 'border-red-500 focus:ring-red-500/50 focus:border-red-500'
                : 'border-white/10 focus:ring-primary focus:border-primary-light'
            }
          `}
          {...props}
        />
        {error && <p className="mt-1 text-sm text-red-400 ml-1">{error}</p>}
      </div>
    );
  }
);

AuthInput.displayName = 'AuthInput';
