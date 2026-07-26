/**
 * Purpose: Password Input with visibility toggle.
 * Responsibilities: Manages local state for showing/hiding password.
 * Dependencies: react, lucide-react, AuthInput
 * Future extensibility: Add password strength indicator.
 */

import React, { useState, forwardRef, InputHTMLAttributes } from 'react';
import { Eye, EyeOff } from 'lucide-react';

interface PasswordInputProps extends InputHTMLAttributes<HTMLInputElement> {
  label: string;
  error?: string;
}

export const PasswordInput = forwardRef<HTMLInputElement, PasswordInputProps>(
  ({ label, error, className = '', ...props }, ref) => {
    const [showPassword, setShowPassword] = useState(false);

    return (
      <div className={`mb-4 relative ${className}`}>
        <label className="block text-sm font-medium text-text-muted mb-1 ml-1">
          {label}
        </label>
        <div className="relative">
          <input
            ref={ref}
            type={showPassword ? 'text' : 'password'}
            className={`
              w-full pl-4 pr-12 py-3 bg-surface/50 border rounded-lg text-text-main placeholder-text-muted/50
              focus:outline-none focus:ring-2 transition-all duration-200
              ${
                error
                  ? 'border-red-500 focus:ring-red-500/50 focus:border-red-500'
                  : 'border-white/10 focus:ring-primary focus:border-primary-light'
              }
            `}
            {...props}
          />
          <button
            type="button"
            onClick={() => setShowPassword(!showPassword)}
            className="absolute right-3 top-1/2 -translate-y-1/2 text-text-muted hover:text-text-main transition-colors"
            tabIndex={-1}
          >
            {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
          </button>
        </div>
        {error && <p className="mt-1 text-sm text-red-400 ml-1">{error}</p>}
      </div>
    );
  }
);

PasswordInput.displayName = 'PasswordInput';
