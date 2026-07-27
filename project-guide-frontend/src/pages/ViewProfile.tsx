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
import PageHeader from '@/components/layout/PageHeader';
import LoadingState from '@/components/ui/LoadingState';
import EmptyState from '@/components/ui/EmptyState';
import { UserCircle } from 'lucide-react';

const ViewProfile = () => {
  const { profile, loading } = useContext(AuthContext);
  const navigate = useNavigate();

  if (loading) {
    return <LoadingState message="Loading profile..." />;
  }

  // If the user hasn't created a profile yet, prompt them or redirect them.
  if (!profile) {
    return (
      <EmptyState
        title="No Profile Found"
        message="You need to complete your student profile so we can generate accurate roadmaps and project recommendations for you."
        icon={<UserCircle size={48} />}
        action={
          <button 
            onClick={() => navigate('/profile/create')}
            className="px-6 py-3 bg-primary hover:bg-primary-light text-white font-medium rounded-lg transition-colors shadow-lg shadow-primary/20"
          >
            Complete Profile Now
          </button>
        }
      />
    );
  }

  return (
    <div className="max-w-4xl mx-auto py-4">
      <PageHeader 
        title="Student Profile" 
        breadcrumbs={[
          { label: 'Home', path: '/dashboard' },
          { label: 'Profile' }
        ]}
        actions={
          <button 
            onClick={() => navigate('/profile/edit')}
            className="px-4 py-2 bg-surface hover:bg-surface-light text-text-main border border-white/10 rounded-lg text-sm transition-all duration-200 shadow-sm"
          >
            Edit Profile
          </button>
        }
      />
      <ProfileCard profile={profile} />
    </div>
  );
};

export default ViewProfile;
