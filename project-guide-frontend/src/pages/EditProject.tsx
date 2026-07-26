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
import { ProjectFormData, ProjectResponse } from '@/types/project';
import { ArrowLeft, Loader2 } from 'lucide-react';

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
    return (
      <div className="flex justify-center items-center h-64 text-text-muted">
        <Loader2 className="animate-spin mr-2" size={24} /> Loading project...
      </div>
    );
  }

  if (error || !initialData) {
    return (
      <div className="text-center py-12 text-red-400 bg-red-500/10 rounded-xl border border-red-500/20 max-w-3xl mx-auto">
        <p>{error}</p>
        <button onClick={() => navigate('/projects')} className="mt-4 px-4 py-2 bg-surface rounded-lg text-white">
          Back to Projects
        </button>
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto py-4">
      <button 
        onClick={() => navigate(`/projects/${id}`)}
        className="flex items-center gap-2 text-text-muted hover:text-white transition-colors mb-6 text-sm"
      >
        <ArrowLeft size={16} />
        Back to Project
      </button>

      <ProjectHeader 
        title="Edit Project" 
        subtitle="Update the details of your learning project."
      />

      <ProjectForm initialData={initialData} onSubmit={handleSubmit} isEditMode />
    </div>
  );
};

export default EditProject;
