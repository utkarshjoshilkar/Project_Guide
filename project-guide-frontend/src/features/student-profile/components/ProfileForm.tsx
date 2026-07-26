/**
 * Purpose: Main Form for Student Profile.
 * Responsibilities: Manages state, validation, and submission for creating or editing a profile.
 * Dependencies: react, UI components, studentProfileService
 * Future extensibility: Implement react-hook-form and zod for complex validation.
 */

import React, { useState, useEffect } from 'react';
import { ProfileInput } from './ProfileInput';
import { ProfileSelect } from './ProfileSelect';
import { SkillsSelector } from './SkillsSelector';
import { DomainSelector } from './DomainSelector';
import { SaveButton } from './SaveButton';
import { studentProfileService } from '@/services/studentProfileService';
import { ProfileFormData } from '@/types/profile';
import { useNavigate } from 'react-router-dom';

interface ProfileFormProps {
  initialData?: ProfileFormData | null;
  isEditMode?: boolean;
  onSuccess?: () => void;
}

export const ProfileForm = ({ initialData, isEditMode = false, onSuccess }: ProfileFormProps) => {
  const [formData, setFormData] = useState<ProfileFormData>({
    college: '', degree: '', branch: '', currentSemester: 1, cgpa: 0.0,
    skills: [], interestedDomains: [], careerGoal: '', learningStyle: '',
    weeklyStudyHours: 10, preferredLanguage: '', githubProfile: '',
    linkedinProfile: '', shortBio: ''
  });

  const [errors, setErrors] = useState<Partial<Record<keyof ProfileFormData, string>> & { general?: string }>({});
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (initialData) {
      setFormData(initialData);
    }
  }, [initialData]);

  const handleChange = (field: keyof ProfileFormData, value: any) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    // Clear error for field
    if (errors[field]) setErrors(prev => ({ ...prev, [field]: undefined }));
  };

  const validate = () => {
    const newErrors: any = {};
    if (!formData.college) newErrors.college = 'College is required';
    if (!formData.branch) newErrors.branch = 'Branch is required';
    if (formData.skills.length === 0) newErrors.skills = 'At least one skill is required';
    if (!formData.careerGoal) newErrors.careerGoal = 'Career goal is required';
    if (formData.cgpa < 0 || formData.cgpa > 10) newErrors.cgpa = 'CGPA must be between 0 and 10';
    
    // URL Validation simple check
    if (formData.githubProfile && !formData.githubProfile.startsWith('http')) {
      newErrors.githubProfile = 'Must be a valid URL';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    setIsLoading(true);
    setErrors({});

    try {
      if (isEditMode) {
        await studentProfileService.updateProfile(formData);
      } else {
        await studentProfileService.createProfile(formData);
      }
      if (onSuccess) onSuccess();
      else navigate('/dashboard');
    } catch (error: any) {
      setErrors({ 
        general: error.response?.data?.message || 'Failed to save profile. Please try again.' 
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-6">
      {errors.general && (
        <div className="p-4 bg-red-500/10 border border-red-500/50 rounded-lg text-red-400 text-sm">
          {errors.general}
        </div>
      )}

      {/* Education Section */}
      <div className="glass-card p-6">
        <h2 className="text-xl font-semibold mb-4 text-primary-light">Education</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <ProfileInput label="College / University" value={formData.college} onChange={e => handleChange('college', e.target.value)} error={errors.college} />
          <ProfileInput label="Degree (e.g. B.Tech)" value={formData.degree} onChange={e => handleChange('degree', e.target.value)} error={errors.degree} />
          <ProfileInput label="Branch / Major" value={formData.branch} onChange={e => handleChange('branch', e.target.value)} error={errors.branch} />
          
          <ProfileSelect 
            label="Current Semester" 
            value={formData.currentSemester} 
            onChange={e => handleChange('currentSemester', parseInt(e.target.value))}
            options={[1, 2, 3, 4, 5, 6, 7, 8].map(sem => ({ label: `Semester ${sem}`, value: sem }))}
          />
          <ProfileInput type="number" step="0.1" label="Current CGPA" value={formData.cgpa} onChange={e => handleChange('cgpa', parseFloat(e.target.value))} error={errors.cgpa} />
        </div>
      </div>

      {/* Skills & Interests */}
      <div className="glass-card p-6">
        <h2 className="text-xl font-semibold mb-4 text-primary-light">Skills & Interests</h2>
        <SkillsSelector skills={formData.skills} onChange={skills => handleChange('skills', skills)} error={errors.skills} />
        <DomainSelector domains={formData.interestedDomains} onChange={domains => handleChange('interestedDomains', domains)} error={errors.interestedDomains} />
        
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
          <ProfileInput label="Preferred Programming Language" value={formData.preferredLanguage} onChange={e => handleChange('preferredLanguage', e.target.value)} />
          <ProfileInput label="Career Goal" value={formData.careerGoal} onChange={e => handleChange('careerGoal', e.target.value)} error={errors.careerGoal} />
        </div>
      </div>

      {/* Learning Profile */}
      <div className="glass-card p-6">
        <h2 className="text-xl font-semibold mb-4 text-primary-light">Learning Profile</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <ProfileSelect 
            label="Preferred Learning Style" 
            value={formData.learningStyle} 
            onChange={e => handleChange('learningStyle', e.target.value)}
            options={[
              { label: 'Visual (Videos/Diagrams)', value: 'Visual' },
              { label: 'Reading (Docs/Books)', value: 'Reading' },
              { label: 'Kinesthetic (Hands-on Projects)', value: 'Kinesthetic' }
            ]}
          />
          <ProfileInput type="number" label="Weekly Study Hours" value={formData.weeklyStudyHours} onChange={e => handleChange('weeklyStudyHours', parseInt(e.target.value))} />
        </div>
      </div>

      {/* Social & Bio */}
      <div className="glass-card p-6">
        <h2 className="text-xl font-semibold mb-4 text-primary-light">Social & Bio</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <ProfileInput label="GitHub Profile URL" value={formData.githubProfile} onChange={e => handleChange('githubProfile', e.target.value)} error={errors.githubProfile} />
          <ProfileInput label="LinkedIn Profile URL" value={formData.linkedinProfile} onChange={e => handleChange('linkedinProfile', e.target.value)} error={errors.linkedinProfile} />
        </div>
        <ProfileInput textarea label="Short Bio" value={formData.shortBio} onChange={e => handleChange('shortBio', e.target.value)} className="mt-4" />
      </div>

      <div className="flex justify-end">
        <SaveButton type="submit" isLoading={isLoading} label={isEditMode ? "Save Changes" : "Complete Profile"} />
      </div>
    </form>
  );
};
