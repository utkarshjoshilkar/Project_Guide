/**
 * Purpose: Create Profile Page.
 * Responsibilities: Render the ProfileForm for first-time profile creation.
 * Dependencies: react, ProfileForm
 * Future extensibility: Add a progress stepper if the form becomes multi-step.
 */

import React from 'react';
import { ProfileForm } from '@/features/student-profile/components/ProfileForm';

const CreateProfile = () => {
  return (
    <div className="max-w-4xl mx-auto py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-text-main mb-2">Complete Your Profile</h1>
        <p className="text-text-muted">Tell us about your background to get personalized roadmaps and projects.</p>
      </div>
      <ProfileForm />
    </div>
  );
};

export default CreateProfile;
