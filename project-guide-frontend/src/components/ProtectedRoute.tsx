/**
 * Purpose: Route guard for authenticated pages.
 * Responsibilities: Checks if user is authenticated via AuthContext. Redirects to /login if not.
 * Dependencies: react-router-dom, context/AuthContext
 * Future extensibility: Add role-based access control (RBAC) checks here.
 */

import React, { useContext } from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { AuthContext } from '@/context/AuthContext';

const ProtectedRoute = () => {
  const { isAuthenticated, loading } = useContext(AuthContext);

  if (loading) {
    return <div className="flex items-center justify-center min-h-screen">Loading session...</div>;
  }

  return isAuthenticated ? <Outlet /> : <Navigate to="/login" replace />;
};

export default ProtectedRoute;
