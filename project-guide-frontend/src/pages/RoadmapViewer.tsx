import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useRoadmap } from '@/features/roadmap/hooks/useRoadmap';
import { useMilestones } from '@/features/roadmap/hooks/useMilestones';
import { projectService } from '@/services/projectService';
import { ProjectResponse } from '@/types/project';
import { TaskResponse } from '@/types/roadmap';

import { RoadmapHeader } from '@/features/roadmap/components/RoadmapHeader';
import { RoadmapSummaryCard } from '@/features/roadmap/components/RoadmapSummaryCard';
import { Timeline } from '@/features/roadmap/components/Timeline';
import { EmptyState } from '@/features/roadmap/components/EmptyState';
import { LoadingSkeleton } from '@/features/roadmap/components/LoadingSkeleton';
import { ErrorState } from '@/features/roadmap/components/ErrorState';

const RoadmapViewer = () => {
  const { id } = useParams<{ id: string }>();
  const projectId = parseInt(id || '0', 10);
  const navigate = useNavigate();

  // Project data (just to get the project name for the header)
  const [project, setProject] = useState<ProjectResponse | null>(null);
  
  // Roadmap Data
  const { 
    roadmap, 
    loading: roadmapLoading, 
    error: roadmapError, 
    retry: retryRoadmap 
  } = useRoadmap(projectId);

  // Milestones Data
  const {
    milestones,
    loading: milestonesLoading,
    error: milestonesError,
    retry: retryMilestones,
    loadTasks,
    loadResources,
    updateTaskStatus
  } = useMilestones(roadmap?.id);

  useEffect(() => {
    if (projectId) {
      projectService.getProjectById(projectId)
        .then(setProject)
        .catch((err: any) => console.error("Failed to load project details", err));
    }
  }, [projectId]);

  const handleRetry = () => {
    retryRoadmap();
    if (roadmap?.id) retryMilestones();
  };

  const handleUpdateTask = (milestoneId: number, taskId: number, updatedTask: TaskResponse) => {
    updateTaskStatus(milestoneId, taskId, updatedTask);
    // Quietly refresh the roadmap to update the overall progress percentage without triggering the loading skeleton
    if (projectId) {
      retryRoadmap(true); // true = quiet refresh
    }
  };

  const isLoading = roadmapLoading || (roadmap && milestonesLoading);
  const error = roadmapError || milestonesError;

  return (
    <div className="max-w-5xl mx-auto py-4">
      <RoadmapHeader 
        projectName={project?.title || 'Loading Project...'} 
        projectId={projectId} 
      />

      {isLoading && <LoadingSkeleton />}

      {error && !isLoading && (
        <ErrorState error={error} onRetry={handleRetry} />
      )}

      {!isLoading && !error && !roadmap && (
        <EmptyState message="No roadmap has been generated for this project yet. Go back to the Project Details page to generate one." />
      )}

      {!isLoading && !error && roadmap && (
        <div className="animate-in fade-in slide-in-from-bottom-4 duration-500">
          <RoadmapSummaryCard roadmap={roadmap} />
          
          <div className="mt-8">
            <h3 className="text-xl font-bold text-text-main mb-6 px-2">Project Milestones</h3>
            
            {milestones.length === 0 ? (
              <EmptyState message="This roadmap has no milestones. Try regenerating it." />
            ) : (
              <Timeline 
                milestones={milestones}
                onExpandMilestone={loadTasks}
                onExpandTask={loadResources}
                onUpdateTask={handleUpdateTask}
              />
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default RoadmapViewer;
