/**
 * Purpose: View Profile Page.
 * Responsibilities: Checks if profile exists. If so, renders ProfileCard; otherwise, redirects to CreateProfile.
 * Dependencies: react, AuthContext, ProfileCard, Link
 * Future extensibility: Add export profile to PDF functionality.
 */

import React, { useContext } from 'react';
import { AuthContext } from '@/context/AuthContext';
import { ProfileCard } from '@/features/student-profile/components/ProfileCard';
import { Navigate, useNavigate } from 'react-router-dom';

const ViewProfile = () => {
  const { profile, loading } = useContext(AuthContext);
  const navigate = useNavigate();

  if (loading) {
    return <div className="flex justify-center items-center h-64 text-text-muted">Loading profile...</div>;
  }

  // If the user hasn't created a profile yet, prompt them or redirect them.
  if (!profile) {
    return (
      <div className="flex flex-col items-center justify-center py-20 text-center">
        <div className="w-20 h-20 bg-accent/20 rounded-full flex items-center justify-center mb-6">
          <span className="text-3xl">👤</span>
        </div>
        <h2 className="text-2xl font-bold mb-4">No Profile Found</h2>
        <p className="text-text-muted mb-8 max-w-md">
          You need to complete your student profile so we can generate accurate roadmaps and project recommendations for you.
        </p>
        <button 
          onClick={() => navigate('/profile/create')}
          className="px-6 py-3 bg-primary hover:bg-primary-light text-white font-medium rounded-lg transition-colors"
        >
          Complete Profile Now
        </button>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto py-8">
      <div className="flex justify-end mb-4">
        <button 
          onClick={() => navigate('/profile/edit')}
          className="px-4 py-2 bg-accent/20 hover:bg-accent/40 text-accent-light border border-accent/50 rounded-lg text-sm transition-all duration-200"
        >
          Edit Profile
        </button>
      </div>
      <ProfileCard profile={profile} />
    </div>
  );
};

export default ViewProfile;
