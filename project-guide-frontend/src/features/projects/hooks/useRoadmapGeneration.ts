/**
 * Purpose: Custom Hook for Roadmap Generation.
 * Responsibilities: Manages state machine and loading message rotation for AI roadmap generation.
 * Dependencies: react, roadmapService
 * Future extensibility: N/A
 */

import { useState, useEffect, useCallback } from 'react';
import { roadmapService } from '@/services/roadmapService';
import { RoadmapResponse } from '@/types/roadmap';

export type GenerationState = 'INITIAL_LOADING' | 'IDLE' | 'GENERATING' | 'SUCCESS' | 'ERROR';

const LOADING_MESSAGES = [
  "Analyzing your project requirements...",
  "Evaluating required skills and tech stack...",
  "Designing an optimal learning path...",
  "Structuring milestones...",
  "Creating actionable tasks...",
  "Finalizing AI roadmap..."
];

export const useRoadmapGeneration = (projectId: number) => {
  const [status, setStatus] = useState<GenerationState>('INITIAL_LOADING');
  const [roadmap, setRoadmap] = useState<RoadmapResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  
  const [loadingMessageIndex, setLoadingMessageIndex] = useState(0);

  const fetchExistingRoadmap = useCallback(async () => {
    try {
      setStatus('INITIAL_LOADING');
      const data = await roadmapService.getRoadmap(projectId);
      setRoadmap(data);
      setStatus('SUCCESS');
    } catch (err: any) {
      if (err.response?.status === 404) {
        // Roadmap doesn't exist yet, we show the idle state with generate button
        setStatus('IDLE');
      } else {
        // Actual network or server error
        setError('Failed to check roadmap status.');
        setStatus('ERROR');
      }
    }
  }, [projectId]);

  useEffect(() => {
    fetchExistingRoadmap();
  }, [fetchExistingRoadmap]);

  // Message cycler during 'GENERATING'
  useEffect(() => {
    let interval: NodeJS.Timeout;
    if (status === 'GENERATING') {
      setLoadingMessageIndex(0);
      interval = setInterval(() => {
        setLoadingMessageIndex(prev => {
          // Cap at the last message so it doesn't loop forever or go out of bounds
          return prev < LOADING_MESSAGES.length - 1 ? prev + 1 : prev;
        });
      }, 3000);
    }
    
    return () => {
      if (interval) clearInterval(interval);
    };
  }, [status]);

  const generateRoadmap = async () => {
    try {
      setStatus('GENERATING');
      setError(null);
      
      await roadmapService.generateRoadmap(projectId);
      
      // Fetch the newly generated roadmap
      const newRoadmap = await roadmapService.getRoadmap(projectId);
      setRoadmap(newRoadmap);
      setStatus('SUCCESS');
      
    } catch (err: any) {
      const errMsg = err.response?.data?.message || err.message || 'The AI service encountered an error.';
      setError(errMsg);
      setStatus('ERROR');
    }
  };

  return {
    status,
    roadmap,
    error,
    currentLoadingMessage: LOADING_MESSAGES[loadingMessageIndex],
    generateRoadmap,
    retry: fetchExistingRoadmap
  };
};
