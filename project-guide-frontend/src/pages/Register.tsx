/**
 * Purpose: Register Page.
 * Responsibilities: Assemble the Register UI using reusable Auth components.
 * Dependencies: react, AuthCard, AuthHeader, RegisterForm
 * Future extensibility: Add social login buttons or terms of service checkboxes.
 */

import React from 'react';
import { AuthCard } from '@/features/auth/components/AuthCard';
import { AuthHeader } from '@/features/auth/components/AuthHeader';
import { RegisterForm } from '@/features/auth/components/RegisterForm';

const Register = () => {
  return (
    <AuthCard>
      <AuthHeader 
        title="Create an Account" 
        subtitle="Join Project Guide to track your learning journey." 
      />
      <RegisterForm />
    </AuthCard>
  );
};

export default Register;
