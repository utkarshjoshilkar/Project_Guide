import apiClient from '@/lib/axios';
import { ResourceRequest, ResourceResponse } from '../types/resource';

export const resourceService = {
  getResourcesForTask: async (taskId: number): Promise<ResourceResponse[]> => {
    const response = await apiClient.get<ResourceResponse[]>(`/tasks/${taskId}/resources`);
    return response.data;
  },

  addResource: async (taskId: number, request: ResourceRequest): Promise<ResourceResponse> => {
    const response = await apiClient.post<ResourceResponse>(`/tasks/${taskId}/resources`, request);
    return response.data;
  },

  updateResource: async (resourceId: number, request: ResourceRequest): Promise<ResourceResponse> => {
    const response = await apiClient.put<ResourceResponse>(`/resources/${resourceId}`, request);
    return response.data;
  },

  deleteResource: async (resourceId: number): Promise<void> => {
    await apiClient.delete(`/resources/${resourceId}`);
  }
};
