/**
 * Purpose: Login Page.
 * Responsibilities: Assemble the Login UI using reusable Auth components.
 * Dependencies: react, AuthCard, AuthHeader, LoginForm
 * Future extensibility: Add social login buttons here.
 */

import React from 'react';
import { AuthCard } from '@/features/auth/components/AuthCard';
import { AuthHeader } from '@/features/auth/components/AuthHeader';
import { LoginForm } from '@/features/auth/components/LoginForm';

const Login = () => {
  return (
    <AuthCard>
      <AuthHeader 
        title="Welcome Back" 
        subtitle="Sign in to continue to Project Guide." 
      />
      <LoginForm />
    </AuthCard>
  );
};

export default Login;
