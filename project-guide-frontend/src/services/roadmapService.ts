/**
 * Purpose: API Service for Roadmap operations.
 * Responsibilities: Handles the endpoints constrained to Phase 6.
 * Dependencies: axios instance, roadmap types.
 * Future extensibility: Expand when milestone endpoints are introduced.
 */

import apiClient from '@/lib/axios';
import { AIResponse, RoadmapResponse, MilestoneResponse, TaskResponse, ResourceResponse } from '@/types/roadmap';

export const roadmapService = {
  
  generateRoadmap: async (projectId: number): Promise<AIResponse> => {
    const response = await apiClient.post<AIResponse>(`/projects/${projectId}/generate-roadmap`);
    return response.data;
  },

  getRoadmap: async (projectId: number): Promise<RoadmapResponse> => {
    const response = await apiClient.get<RoadmapResponse>(`/projects/${projectId}/roadmap`);
    return response.data;
  },

  getMilestones: async (roadmapId: number): Promise<MilestoneResponse[]> => {
    const response = await apiClient.get<MilestoneResponse[]>(`/roadmaps/${roadmapId}/milestones`);
    return response.data;
  },

  getTasks: async (milestoneId: number): Promise<TaskResponse[]> => {
    const response = await apiClient.get<TaskResponse[]>(`/milestones/${milestoneId}/tasks`);
    return response.data;
  },

  getResources: async (taskId: number): Promise<ResourceResponse[]> => {
    const response = await apiClient.get<ResourceResponse[]>(`/tasks/${taskId}/resources`);
    return response.data;
  }
};
