/**
 * Purpose: API Service for Roadmap operations.
 * Responsibilities: Handles the endpoints constrained to Phase 6.
 * Dependencies: axios instance, roadmap types.
 * Future extensibility: Expand when milestone endpoints are introduced.
 */

import apiClient from '@/lib/axios';
import { AIResponse, RoadmapResponse } from '@/types/roadmap';

export const roadmapService = {
  
  generateRoadmap: async (projectId: number): Promise<AIResponse> => {
    const response = await apiClient.post<AIResponse>(`/projects/${projectId}/generate-roadmap`);
    return response.data;
  },

  getRoadmap: async (projectId: number): Promise<RoadmapResponse> => {
    const response = await apiClient.get<RoadmapResponse>(`/projects/${projectId}/roadmap`);
    return response.data;
  }
  
};
