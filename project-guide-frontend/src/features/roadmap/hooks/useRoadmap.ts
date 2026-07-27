import { useState, useEffect, useCallback } from 'react';
import { roadmapService } from '@/services/roadmapService';
import { RoadmapResponse } from '@/types/roadmap';

export const useRoadmap = (projectId: number | undefined) => {
  const [roadmap, setRoadmap] = useState<RoadmapResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const fetchRoadmap = useCallback(async (quiet: boolean = false) => {
    if (!projectId) return;
    
    try {
      if (!quiet) setLoading(true);
      setError(null);
      const data = await roadmapService.getRoadmap(projectId);
      setRoadmap(data);
    } catch (err: any) {
      if (err.response?.status === 404) {
        setRoadmap(null);
      } else {
        setError(err.response?.data?.message || err.message || 'Failed to fetch roadmap.');
      }
    } finally {
      if (!quiet) setLoading(false);
    }
  }, [projectId]);

  useEffect(() => {
    fetchRoadmap();
  }, [fetchRoadmap]);

  return { roadmap, loading, error, retry: fetchRoadmap };
};
