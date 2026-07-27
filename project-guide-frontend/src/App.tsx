/**
 * Purpose: Application Root Component.
 * Responsibilities: Sets up Context Providers and defines the React Router DOM routes.
 * Dependencies: react, react-router-dom, context providers, layout/page components.
 * Future extensibility: Add global Error Boundaries or suspense fallbacks for lazy loading.
 */

import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from '@/context/AuthContext';
import { ThemeProvider } from '@/context/ThemeContext';

// Layouts
import GuestLayout from '@/layouts/GuestLayout';
import MainLayout from '@/layouts/MainLayout';
import ProtectedRoute from '@/components/ProtectedRoute';

// Pages
import Login from '@/pages/Login';
import Register from '@/pages/Register';
import ForgotPassword from '@/pages/ForgotPassword';
import Dashboard from '@/pages/Dashboard';
import ViewProfile from '@/pages/ViewProfile';
import CreateProfile from '@/pages/CreateProfile';
import EditProfile from '@/pages/EditProfile';
import ProjectList from '@/pages/ProjectList';
import CreateProject from '@/pages/CreateProject';
import EditProject from '@/pages/EditProject';
import ProjectDetails from '@/pages/ProjectDetails';
import RoadmapViewer from '@/pages/RoadmapViewer';

function App() {
  return (
    <ThemeProvider>
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            {/* Base Redirect */}
            <Route path="/" element={<Navigate to="/dashboard" replace />} />

            {/* Guest Routes */}
            <Route element={<GuestLayout />}>
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route path="/forgot-password" element={<ForgotPassword />} />
            </Route>

            {/* Protected Routes */}
            <Route element={<ProtectedRoute />}>
              <Route element={<MainLayout />}>
                <Route path="/dashboard" element={<Dashboard />} />
                <Route path="/profile" element={<ViewProfile />} />
                <Route path="/profile/create" element={<CreateProfile />} />
                <Route path="/profile/edit" element={<EditProfile />} />
                
                {/* Project Routes */}
                <Route path="/projects" element={<ProjectList />} />
                <Route path="/projects/new" element={<CreateProject />} />
                <Route path="/projects/:id" element={<ProjectDetails />} />
                <Route path="/projects/:id/edit" element={<EditProject />} />
                <Route path="/projects/:id/roadmap" element={<RoadmapViewer />} />

                {/* 
                  Placeholders for future implementation:
                  <Route path="/roadmap" element={<Roadmap />} />
                  <Route path="/resources" element={<Resources />} />
                  <Route path="/settings" element={<Settings />} />
                */}
              </Route>
            </Route>

            {/* Fallback */}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </BrowserRouter>
      </AuthProvider>
    </ThemeProvider>
  );
}

export default App;
