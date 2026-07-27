import { useState, useEffect, useCallback } from 'react';
import { roadmapService } from '@/services/roadmapService';
import { MilestoneResponse, TaskResponse, ResourceResponse } from '@/types/roadmap';

export interface TaskWithResources extends TaskResponse {
  resources?: ResourceResponse[];
  loadingResources?: boolean;
  errorResources?: string | null;
}

export interface MilestoneWithTasks extends MilestoneResponse {
  tasks?: TaskWithResources[];
  loadingTasks?: boolean;
  errorTasks?: string | null;
}

export const useMilestones = (roadmapId: number | undefined) => {
  const [milestones, setMilestones] = useState<MilestoneWithTasks[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const fetchMilestones = useCallback(async (quiet: boolean = false) => {
    if (!roadmapId) return;
    
    try {
      if (!quiet) setLoading(true);
      setError(null);
      const data = await roadmapService.getMilestones(roadmapId);
      setMilestones(data);
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Failed to fetch milestones.');
    } finally {
      if (!quiet) setLoading(false);
    }
  }, [roadmapId]);

  useEffect(() => {
    fetchMilestones();
  }, [fetchMilestones]);

  const loadTasks = async (milestoneId: number) => {
    setMilestones(prev => 
      prev.map(m => m.id === milestoneId ? { ...m, loadingTasks: true, errorTasks: null } : m)
    );
    try {
      const tasks = await roadmapService.getTasks(milestoneId);
      setMilestones(prev => 
        prev.map(m => m.id === milestoneId ? { ...m, tasks, loadingTasks: false } : m)
      );
    } catch (err: any) {
      setMilestones(prev => 
        prev.map(m => m.id === milestoneId ? { 
          ...m, 
          loadingTasks: false, 
          errorTasks: err.response?.data?.message || 'Failed to load tasks.' 
        } : m)
      );
    }
  };

  const loadResources = async (milestoneId: number, taskId: number) => {
    setMilestones(prev => 
      prev.map(m => {
        if (m.id !== milestoneId || !m.tasks) return m;
        return {
          ...m,
          tasks: m.tasks.map(t => t.id === taskId ? { ...t, loadingResources: true, errorResources: null } : t)
        };
      })
    );
    
    try {
      const resources = await roadmapService.getResources(taskId);
      setMilestones(prev => 
        prev.map(m => {
          if (m.id !== milestoneId || !m.tasks) return m;
          return {
            ...m,
            tasks: m.tasks.map(t => t.id === taskId ? { ...t, resources, loadingResources: false } : t)
          };
        })
      );
    } catch (err: any) {
      setMilestones(prev => 
        prev.map(m => {
          if (m.id !== milestoneId || !m.tasks) return m;
          return {
            ...m,
            tasks: m.tasks.map(t => t.id === taskId ? { 
              ...t, 
              loadingResources: false, 
              errorResources: err.response?.data?.message || 'Failed to load resources.' 
            } : t)
          };
        })
      );
    }
  };

  // Locally update a task's status (optimistic UI update)
  const updateTaskStatus = (milestoneId: number, taskId: number, updatedTask: TaskResponse) => {
    setMilestones(prev => 
      prev.map(m => {
        if (m.id !== milestoneId || !m.tasks) return m;
        
        const newTasks = m.tasks.map(t => t.id === taskId ? { ...t, ...updatedTask } : t);
        const doneCount = newTasks.filter(t => t.status === 'DONE').length;
        const newCompletionPercentage = newTasks.length > 0 ? (doneCount / newTasks.length) * 100 : 0;

        return {
          ...m,
          tasks: newTasks,
          completionPercentage: newCompletionPercentage
        };
      })
    );
  };

  return { 
    milestones, 
    loading, 
    error, 
    retry: fetchMilestones,
    loadTasks,
    loadResources,
    updateTaskStatus
  };
};
