/**
 * Purpose: Reusable Submit Button for Profile forms.
 * Responsibilities: Handles loading state and visual indicators.
 * Dependencies: react, lucide-react
 * Future extensibility: Accept variants (e.g., outline, ghost) if needed.
 */

import React, { ButtonHTMLAttributes } from 'react';
import { Loader2, Save } from 'lucide-react';

interface SaveButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  isLoading?: boolean;
  label?: string;
}

export const SaveButton = ({ isLoading, disabled, label = "Save Profile", ...props }: SaveButtonProps) => {
  return (
    <button
      {...props}
      disabled={isLoading || disabled}
      className={`
        py-3 px-6 flex justify-center items-center rounded-lg font-medium text-white
        bg-emerald-600 hover:bg-emerald-500 transition-all duration-200
        focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:ring-offset-2 focus:ring-offset-background
        disabled:opacity-70 disabled:cursor-not-allowed
        ${props.className || ''}
      `}
    >
      {isLoading ? (
        <>
          <Loader2 className="animate-spin -ml-1 mr-2 h-5 w-5 text-white" />
          Saving...
        </>
      ) : (
        <>
          <Save className="-ml-1 mr-2 h-5 w-5 text-white" />
          {label}
        </>
      )}
    </button>
  );
};
