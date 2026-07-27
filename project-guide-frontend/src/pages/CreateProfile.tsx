/**
 * Purpose: Create Profile Page.
 * Responsibilities: Render the ProfileForm for first-time profile creation.
 * Dependencies: react, ProfileForm
 * Future extensibility: Add a progress stepper if the form becomes multi-step.
 */

import { ProfileForm } from '@/features/student-profile/components/ProfileForm';
import PageHeader from '@/components/layout/PageHeader';

const CreateProfile = () => {
  return (
    <div className="max-w-4xl mx-auto py-8">
      <PageHeader 
        title="Complete Your Profile" 
        subtitle="Tell us about your background to get personalized roadmaps and projects."
      />
      <ProfileForm />
    </div>
  );
};

export default CreateProfile;
