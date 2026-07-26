/**
 * Purpose: Reusable Submit Button for Auth forms.
 * Responsibilities: Handles loading state (spinner) and disabled state.
 * Dependencies: react, lucide-react
 * Future extensibility: Accept variants (e.g., outline, ghost) if needed.
 */

import React, { ButtonHTMLAttributes } from 'react';
import { Loader2 } from 'lucide-react';

interface AuthButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  isLoading?: boolean;
}

export const AuthButton = ({ children, isLoading, disabled, ...props }: AuthButtonProps) => {
  return (
    <button
      {...props}
      disabled={isLoading || disabled}
      className={`
        w-full py-3 px-4 flex justify-center items-center rounded-lg font-medium text-white
        bg-accent hover:bg-accent-light transition-all duration-200
        focus:outline-none focus:ring-2 focus:ring-accent focus:ring-offset-2 focus:ring-offset-background
        disabled:opacity-70 disabled:cursor-not-allowed
        ${props.className || ''}
      `}
    >
      {isLoading ? (
        <>
          <Loader2 className="animate-spin -ml-1 mr-2 h-5 w-5 text-white" />
          Processing...
        </>
      ) : (
        children
      )}
    </button>
  );
};
