import apiClient from '@/lib/axios';
import { TaskResponse } from '@/types/roadmap';

export const progressService = {
  markTaskComplete: async (taskId: number): Promise<TaskResponse> => {
    const response = await apiClient.patch<TaskResponse>(`/tasks/${taskId}/complete`);
    return response.data;
  }
};
