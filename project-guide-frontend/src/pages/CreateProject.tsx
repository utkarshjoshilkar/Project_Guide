/**
 * Purpose: Create Project Page.
 * Responsibilities: Renders ProjectForm for new project creation.
 * Dependencies: react, react-router-dom, projectService, ProjectForm
 * Future extensibility: N/A
 */

import React from 'react';
import { useNavigate } from 'react-router-dom';
import { ProjectForm } from '@/features/projects/components/ProjectForm';
import { projectService } from '@/services/projectService';
import { ProjectFormData } from '@/types/project';
import PageHeader from '@/components/layout/PageHeader';

const CreateProject = () => {
  const navigate = useNavigate();

  const handleSubmit = async (data: ProjectFormData) => {
    const newProject = await projectService.createProject(data);
    // After creating, route them to the project details page
    navigate(`/projects/${newProject.id}`);
  };

  return (
    <div className="max-w-3xl mx-auto py-4">
      <PageHeader 
        title="Create New Project" 
        subtitle="Define the scope and goals for your next learning project."
        breadcrumbs={[
          { label: 'Home', path: '/dashboard' },
          { label: 'Projects', path: '/projects' },
          { label: 'New Project' }
        ]}
      />

      <ProjectForm onSubmit={handleSubmit} />
    </div>
  );
};

export default CreateProject;
