/**
 * Purpose: TypeScript interfaces for the projects module.
 * Responsibilities: Define shapes for Project API requests, responses, and UI forms.
 * Dependencies: None
 * Future extensibility: N/A
 */

export interface ProjectResponse {
  id: number;
  title: string;
  description: string;
  domain: string;
  preferredTechStack: string;
  skillLevel: string;
  deadline: string; // ISO Date string
  expectedOutcome: string;
  status: 'IDEA_SUBMITTED' | 'IN_PROGRESS' | 'COMPLETED' | 'ON_HOLD';
  studentProfileId?: number;
  createdAt: string;
  updatedAt: string;
}

export interface ProjectRequest {
  title: string;
  description: string;
  domain: string;
  preferredTechStack: string;
  skillLevel: string;
  deadline: string; // ISO Date string YYYY-MM-DD
  expectedOutcome: string;
}

export interface ProjectFormData {
  title: string;
  description: string;
  category: string; // UI terminology for domain
  techStack: string; // UI terminology for preferredTechStack
  difficulty: string; // UI terminology for skillLevel
  duration: string; // UI terminology (e.g. 1 week, 2 weeks, 1 month)
  expectedOutcome: string;
}
