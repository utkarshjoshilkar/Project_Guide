/**
 * Purpose: Login Page.
 * Responsibilities: Render login form and authenticate user.
 * Dependencies: react
 * Future extensibility: Implement form validation and API integration.
 */

import React from 'react';

const Login = () => {
  return (
    <div className="glass-card p-8 w-full max-w-md">
      <h1 className="text-2xl font-bold mb-4">Welcome Back</h1>
      <p className="text-text-muted mb-6">Sign in to continue to Project Guide.</p>
      {/* Form will go here */}
    </div>
  );
};

export default Login;
