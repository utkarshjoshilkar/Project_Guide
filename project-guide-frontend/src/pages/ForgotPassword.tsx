/**
 * Purpose: Forgot Password Page Placeholder.
 * Responsibilities: Render UI placeholder for password reset flow.
 * Dependencies: react, AuthCard, AuthHeader, Link
 * Future extensibility: Implement email sending logic and reset token validation.
 */

import React from 'react';
import { AuthCard } from '@/features/auth/components/AuthCard';
import { AuthHeader } from '@/features/auth/components/AuthHeader';
import { AuthInput } from '@/features/auth/components/AuthInput';
import { AuthButton } from '@/features/auth/components/AuthButton';
import { Link } from 'react-router-dom';

const ForgotPassword = () => {
  return (
    <AuthCard>
      <AuthHeader 
        title="Reset Password" 
        subtitle="Enter your email to receive a password reset link." 
      />
      <form className="flex flex-col gap-2" onSubmit={(e) => e.preventDefault()}>
        <AuthInput 
          label="Email Address" 
          type="email" 
          placeholder="you@example.com"
        />
        <AuthButton type="button" className="mt-4" onClick={() => alert("Forgot password functionality coming soon!")}>
          Send Reset Link
        </AuthButton>
        <p className="text-center text-text-muted mt-6 text-sm">
          Remember your password?{' '}
          <Link to="/login" className="text-accent hover:text-accent-light font-medium transition-colors">
            Sign In
          </Link>
        </p>
      </form>
    </AuthCard>
  );
};

export default ForgotPassword;
