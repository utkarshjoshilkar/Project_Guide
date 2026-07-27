import { useState } from 'react';
import { progressService } from '../services/progressService';
import { TaskResponse } from '@/types/roadmap';

interface UseTaskProgressOptions {
  onSuccess?: (updatedTask: TaskResponse) => void;
  onError?: (error: string) => void;
}

export const useTaskProgress = (options?: UseTaskProgressOptions) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const markComplete = async (taskId: number) => {
    try {
      setLoading(true);
      setError(null);
      const updatedTask = await progressService.markTaskComplete(taskId);
      if (options?.onSuccess) {
        options.onSuccess(updatedTask);
      }
      return updatedTask;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to update task progress.';
      setError(errorMessage);
      if (options?.onError) {
        options.onError(errorMessage);
      }
      throw err;
    } finally {
      setLoading(false);
    }
  };

  return { markComplete, loading, error };
};
