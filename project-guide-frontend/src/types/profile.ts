/**
 * Purpose: TypeScript interfaces for the student profile module.
 * Responsibilities: Define shapes for Profile API requests, responses, and local form state.
 * Dependencies: None
 * Future extensibility: Separate UI form fields from backend DTO cleanly if backend ever supports more fields.
 */

export interface StudentProfileResponse {
  id: number;
  college: string;
  branch: string;
  year: number;
  cgpa: number;
  skills: string;
  interests: string;
  preferredTechStack: string;
  githubProfile: string;
  linkedinProfile: string;
  learningGoal: string;
}

export interface StudentProfileRequest {
  college: string;
  branch: string;
  year: number;
  cgpa: number;
  skills: string;
  interests: string;
  preferredTechStack: string;
  githubProfile: string;
  linkedinProfile: string;
  learningGoal: string;
}

// Local UI Form State which contains fields the backend doesn't support natively
export interface ProfileFormData {
  college: string;
  degree: string;
  branch: string;
  currentSemester: number;
  cgpa: number;
  skills: string[]; // Arrays in UI for easy mapping
  interestedDomains: string[];
  careerGoal: string;
  learningStyle: string;
  weeklyStudyHours: number;
  preferredLanguage: string;
  githubProfile: string;
  linkedinProfile: string;
  shortBio: string;
}
