/**
 * Purpose: API Service for the Student Profile module.
 * Responsibilities: Handles create, update, and fetch operations. Maps UI form data to backend DTO.
 * Dependencies: axios instance, profile types.
 * Future extensibility: Separate mapping logic into a utility file if it grows too complex.
 */

import apiClient from '@/lib/axios';
import { StudentProfileRequest, StudentProfileResponse, ProfileFormData } from '@/types/profile';

// Helper to map UI form data to the backend DTO string constraints
const mapFormToRequest = (data: ProfileFormData): StudentProfileRequest => {
  return {
    college: `${data.degree} - ${data.college}`,
    branch: data.branch,
    year: Math.ceil(data.currentSemester / 2), // Map semester (1-8) to year (1-4)
    cgpa: data.cgpa,
    skills: data.skills.join(', '),
    interests: `Domains: ${data.interestedDomains.join(', ')} | Bio: ${data.shortBio}`,
    preferredTechStack: data.preferredLanguage,
    githubProfile: data.githubProfile,
    linkedinProfile: data.linkedinProfile,
    learningGoal: `${data.careerGoal} | Style: ${data.learningStyle} | Hours/Week: ${data.weeklyStudyHours}`,
  };
};

export const studentProfileService = {
  createProfile: async (formData: ProfileFormData): Promise<StudentProfileResponse> => {
    const payload = mapFormToRequest(formData);
    const response = await apiClient.post<StudentProfileResponse>('/student-profile', payload);
    return response.data;
  },

  getProfile: async (): Promise<StudentProfileResponse | null> => {
    try {
      const response = await apiClient.get<StudentProfileResponse>('/student-profile/me');
      return response.data;
    } catch (error: any) {
      // If profile doesn't exist, it likely returns 404. Return null instead of throwing.
      if (error.response && error.response.status === 404) {
        return null;
      }
      throw error;
    }
  },

  updateProfile: async (formData: ProfileFormData): Promise<StudentProfileResponse> => {
    const payload = mapFormToRequest(formData);
    const response = await apiClient.put<StudentProfileResponse>('/student-profile', payload);
    return response.data;
  }
};
