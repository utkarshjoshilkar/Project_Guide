/**
 * Purpose: Edit Profile Page.
 * Responsibilities: Fetch current profile and render ProfileForm in edit mode.
 * Dependencies: react, AuthContext, ProfileForm
 * Future extensibility: Autosave functionality.
 */

import React, { useContext, useEffect, useState } from 'react';
import { AuthContext } from '@/context/AuthContext';
import { ProfileForm } from '@/features/student-profile/components/ProfileForm';
import { useNavigate } from 'react-router-dom';
import { ProfileFormData } from '@/types/profile';
import PageHeader from '@/components/layout/PageHeader';
import LoadingState from '@/components/ui/LoadingState';

const EditProfile = () => {
  const { profile, loading, refreshProfile } = useContext(AuthContext);
  const navigate = useNavigate();
  const [initialData, setInitialData] = useState<ProfileFormData | null>(null);

  useEffect(() => {
    if (!loading && profile) {
      // Reverse map the backend string concatenations to form fields
      // This is a naive split based on the mapping strategy in studentProfileService
      const collegeParts = profile.college.split(' - ');
      
      let domains: string[] = [];
      let bio = '';
      if (profile.interests.includes(' | Bio: ')) {
        const parts = profile.interests.split(' | Bio: ');
        domains = parts[0].replace('Domains: ', '').split(',').map(s => s.trim()).filter(Boolean);
        bio = parts[1];
      } else {
        domains = profile.interests.split(',').map(s => s.trim()).filter(Boolean);
      }

      let careerGoal = profile.learningGoal;
      let style = '';
      let hours = 10;
      if (profile.learningGoal.includes(' | Style: ')) {
        const parts = profile.learningGoal.split(' | Style: ');
        careerGoal = parts[0];
        const subParts = parts[1].split(' | Hours/Week: ');
        style = subParts[0];
        if (subParts.length > 1) {
          hours = parseInt(subParts[1]) || 10;
        }
      }

      setInitialData({
        college: collegeParts.length > 1 ? collegeParts[1] : profile.college,
        degree: collegeParts.length > 1 ? collegeParts[0] : '',
        branch: profile.branch,
        currentSemester: profile.year * 2, // Approximate
        cgpa: profile.cgpa,
        skills: profile.skills.split(',').map(s => s.trim()).filter(Boolean),
        interestedDomains: domains,
        shortBio: bio,
        careerGoal: careerGoal,
        learningStyle: style,
        weeklyStudyHours: hours,
        preferredLanguage: profile.preferredTechStack,
        githubProfile: profile.githubProfile,
        linkedinProfile: profile.linkedinProfile
      });
    }
  }, [profile, loading]);

  if (loading || !initialData) {
    return <LoadingState message="Loading profile..." />;
  }

  const handleSuccess = async () => {
    await refreshProfile();
    navigate('/profile');
  };

  return (
    <div className="max-w-4xl mx-auto py-8">
      <PageHeader 
        title="Edit Profile"
        subtitle="Update your details to refine your recommendations."
        breadcrumbs={[
          { label: 'Home', path: '/dashboard' },
          { label: 'Profile', path: '/profile' },
          { label: 'Edit' }
        ]}
        actions={
          <button onClick={() => navigate('/profile')} className="px-4 py-2 bg-surface hover:bg-surface-light border border-white/10 rounded-lg text-sm transition-colors">
            Cancel
          </button>
        }
      />
      <ProfileForm initialData={initialData} isEditMode onSuccess={handleSuccess} />
    </div>
  );
};

export default EditProfile;
