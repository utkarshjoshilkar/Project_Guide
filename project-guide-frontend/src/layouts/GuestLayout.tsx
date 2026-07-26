/**
 * Purpose: Layout for unauthenticated pages (Login, Register).
 * Responsibilities: Renders a centered, clean container for auth forms.
 * Dependencies: react-router-dom
 * Future extensibility: Add a generic marketing footer or split-screen branding image.
 */

import React, { useContext } from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { AuthContext } from '@/context/AuthContext';

const GuestLayout = () => {
  const { isAuthenticated, loading } = useContext(AuthContext);

  if (loading) {
    return <div className="flex items-center justify-center min-h-screen">Loading session...</div>;
  }

  // If already authenticated, redirect away from guest routes (like login) to dashboard
  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />;
  }

  return (
    <div className="min-h-screen flex items-center justify-center p-4">
      <Outlet />
    </div>
  );
};

export default GuestLayout;
