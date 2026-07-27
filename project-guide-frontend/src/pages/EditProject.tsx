/**
 * Purpose: Edit Project Page.
 * Responsibilities: Fetches existing project data, reverse-maps it to form data, and handles updates.
 * Dependencies: react, react-router-dom, projectService, ProjectForm
 * Future extensibility: N/A
 */

import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { ProjectHeader } from '@/features/projects/components/ProjectHeader';
import { ProjectForm } from '@/features/projects/components/ProjectForm';
import { projectService } from '@/services/projectService';
import { ProjectFormData } from '@/types/project';
import PageHeader from '@/components/layout/PageHeader';
import LoadingState from '@/components/ui/LoadingState';
import ErrorState from '@/components/ui/ErrorState';

const EditProject = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [initialData, setInitialData] = useState<ProjectFormData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchProject = async () => {
      try {
        if (!id) return;
        const project = await projectService.getProjectById(parseInt(id));
        
        // Map backend DTO to frontend Form Data
        // Since we don't have the exact duration dropdown value stored, we will default to '1 month'
        // or attempt to calculate it. For simplicity, we just set a default that the user can change.
        setInitialData({
          title: project.title,
          description: project.description,
          category: project.domain,
          techStack: project.preferredTechStack,
          difficulty: project.skillLevel,
          duration: '1 month', // The actual deadline is stored, but reverse mapping exact duration is complex
          expectedOutcome: project.expectedOutcome
        });
      } catch (err: any) {
        setError('Failed to load project details.');
      } finally {
        setLoading(false);
      }
    };

    fetchProject();
  }, [id]);

  const handleSubmit = async (data: ProjectFormData) => {
    if (!id) return;
    await projectService.updateProject(parseInt(id), data);
    navigate(`/projects/${id}`);
  };

  if (loading) {
    return <LoadingState message="Loading project..." />;
  }

  if (error || !initialData) {
    return (
      <ErrorState 
        message={error || 'Project not found.'}
        onRetry={() => navigate('/projects')}
      />
    );
  }

  return (
    <div className="max-w-3xl mx-auto py-4">
      <PageHeader 
        title="Edit Project" 
        subtitle="Update the details of your learning project."
        breadcrumbs={[
          { label: 'Home', path: '/dashboard' },
          { label: 'Projects', path: '/projects' },
          { label: 'Edit Project' }
        ]}
        actions={
          <button 
            onClick={() => navigate(`/projects/${id}`)}
            className="px-4 py-2 bg-surface hover:bg-surface-light border border-white/10 rounded-lg text-sm transition-colors text-white"
          >
            Cancel
          </button>
        }
      />

      <ProjectForm initialData={initialData} onSubmit={handleSubmit} isEditMode />
    </div>
  );
};

export default EditProject;
