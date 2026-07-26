/**
 * Purpose: API Service for the Projects module.
 * Responsibilities: Handles CRUD operations and maps UI forms to backend DTO.
 * Dependencies: axios instance, project types.
 * Future extensibility: Support pagination and backend-side filtering/sorting if needed.
 */

import apiClient from '@/lib/axios';
import { ProjectRequest, ProjectResponse, ProjectFormData } from '@/types/project';

// Helper to map UI form data to backend DTO and calculate deadline
const mapFormToRequest = (data: ProjectFormData): ProjectRequest => {
  const deadline = new Date();
  
  // Calculate relative date based on duration dropdown
  switch (data.duration) {
    case '1 week': deadline.setDate(deadline.getDate() + 7); break;
    case '2 weeks': deadline.setDate(deadline.getDate() + 14); break;
    case '1 month': deadline.setMonth(deadline.getMonth() + 1); break;
    case '3 months': deadline.setMonth(deadline.getMonth() + 3); break;
    case '6 months': deadline.setMonth(deadline.getMonth() + 6); break;
    default: deadline.setMonth(deadline.getMonth() + 1); // fallback 1 month
  }

  return {
    title: data.title,
    description: data.description,
    domain: data.category,
    preferredTechStack: data.techStack,
    skillLevel: data.difficulty,
    expectedOutcome: data.expectedOutcome,
    deadline: deadline.toISOString().split('T')[0], // YYYY-MM-DD format
  };
};

export const projectService = {
  getAllProjects: async (): Promise<ProjectResponse[]> => {
    const response = await apiClient.get<ProjectResponse[]>('/projects');
    return response.data;
  },

  getProjectById: async (id: number): Promise<ProjectResponse> => {
    const response = await apiClient.get<ProjectResponse>(`/projects/${id}`);
    return response.data;
  },

  createProject: async (formData: ProjectFormData): Promise<ProjectResponse> => {
    const payload = mapFormToRequest(formData);
    const response = await apiClient.post<ProjectResponse>('/projects', payload);
    return response.data;
  },

  updateProject: async (id: number, formData: ProjectFormData): Promise<ProjectResponse> => {
    const payload = mapFormToRequest(formData);
    const response = await apiClient.put<ProjectResponse>(`/projects/${id}`, payload);
    return response.data;
  },

  deleteProject: async (id: number): Promise<void> => {
    await apiClient.delete(`/projects/${id}`);
  }
};
