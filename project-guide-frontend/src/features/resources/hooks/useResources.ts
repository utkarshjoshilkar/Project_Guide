import { useState, useEffect, useCallback } from 'react';
import { ResourceResponse, ResourceRequest } from '../types/resource';
import { resourceService } from '../services/resourceService';

export const useResources = (taskId: number) => {
  const [resources, setResources] = useState<ResourceResponse[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [isMutating, setIsMutating] = useState<boolean>(false);

  const fetchResources = useCallback(async () => {
    try {
      setIsLoading(true);
      setError(null);
      const data = await resourceService.getResourcesForTask(taskId);
      setResources(data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to fetch resources. Please try again.');
    } finally {
      setIsLoading(false);
    }
  }, [taskId]);

  useEffect(() => {
    fetchResources();
  }, [fetchResources]);

  const addResource = async (request: ResourceRequest) => {
    try {
      setIsMutating(true);
      const newResource = await resourceService.addResource(taskId, request);
      setResources((prev) => [...prev, newResource]);
      return newResource;
    } finally {
      setIsMutating(false);
    }
  };

  const updateResource = async (resourceId: number, request: ResourceRequest) => {
    try {
      setIsMutating(true);
      const updatedResource = await resourceService.updateResource(resourceId, request);
      setResources((prev) =>
        prev.map((r) => (r.id === resourceId ? updatedResource : r))
      );
      return updatedResource;
    } finally {
      setIsMutating(false);
    }
  };

  const deleteResource = async (resourceId: number) => {
    try {
      setIsMutating(true);
      await resourceService.deleteResource(resourceId);
      setResources((prev) => prev.filter((r) => r.id !== resourceId));
    } finally {
      setIsMutating(false);
    }
  };

  return {
    resources,
    isLoading,
    error,
    isMutating,
    refetch: fetchResources,
    addResource,
    updateResource,
    deleteResource,
  };
};
