/**
 * Purpose: Project Details Page.
 * Responsibilities: Renders a read-only view of a single project. Provides Edit/Delete actions.
 * Dependencies: react, react-router-dom, projectService, ProjectHeader, DeleteProjectModal
 * Future extensibility: Will host the AI-generated Roadmap in the next phase.
 */

import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { projectService } from '@/services/projectService';
import { ProjectResponse } from '@/types/project';
import { ProjectHeader } from '@/features/projects/components/ProjectHeader';
import { ProjectStatusBadge } from '@/features/projects/components/ProjectStatusBadge';
import { DifficultyBadge } from '@/features/projects/components/DifficultyBadge';
import { DeleteProjectModal } from '@/features/projects/components/DeleteProjectModal';
import { ArrowLeft, Edit2, Trash2, Calendar, Layers, CheckSquare, Clock } from 'lucide-react';

const ProjectDetails = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [project, setProject] = useState<ProjectResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);

  useEffect(() => {
    const fetchProject = async () => {
      try {
        if (!id) return;
        const data = await projectService.getProjectById(parseInt(id));
        setProject(data);
      } catch (err: any) {
        setError('Failed to load project details.');
      } finally {
        setLoading(false);
      }
    };

    fetchProject();
  }, [id]);

  const handleDelete = async () => {
    try {
      if (!id) return;
      setIsDeleting(true);
      await projectService.deleteProject(parseInt(id));
      setIsDeleteModalOpen(false);
      // Optimistic UI routing
      navigate('/projects');
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to delete project.');
      setIsDeleting(false);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64 text-text-muted">
        <div className="animate-spin w-8 h-8 border-2 border-primary border-t-transparent rounded-full"></div>
      </div>
    );
  }

  if (error || !project) {
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
    <div className="max-w-5xl mx-auto py-4">
      <button 
        onClick={() => navigate('/projects')}
        className="flex items-center gap-2 text-text-muted hover:text-white transition-colors mb-6 text-sm"
      >
        <ArrowLeft size={16} />
        Back to Projects
      </button>

      <ProjectHeader 
        title={project.title} 
        action={
          <div className="flex gap-3">
            <button 
              onClick={() => navigate(`/projects/${project.id}/edit`)}
              className="px-4 py-2 bg-surface hover:bg-surface-light border border-white/10 text-white rounded-lg flex items-center gap-2 transition-colors font-medium"
            >
              <Edit2 size={16} />
              Edit
            </button>
            <button 
              onClick={() => setIsDeleteModalOpen(true)}
              className="px-4 py-2 bg-red-500/10 hover:bg-red-500/20 text-red-400 border border-red-500/20 rounded-lg flex items-center gap-2 transition-colors font-medium"
            >
              <Trash2 size={16} />
              Delete
            </button>
          </div>
        }
      />

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        {/* Main Content */}
        <div className="lg:col-span-2 space-y-6">
          <div className="glass-card p-6 md:p-8">
            <h2 className="text-xl font-bold text-text-main mb-4">Description</h2>
            <p className="text-text-muted whitespace-pre-wrap leading-relaxed">
              {project.description}
            </p>
          </div>

          <div className="glass-card p-6 md:p-8">
            <h2 className="text-xl font-bold text-text-main mb-4">Expected Outcome</h2>
            <p className="text-text-muted whitespace-pre-wrap leading-relaxed">
              {project.expectedOutcome}
            </p>
          </div>

          {/* Placeholder for AI Roadmap module */}
          <div className="glass-card p-6 md:p-8 border-dashed border-primary/30 bg-primary/5 relative overflow-hidden flex flex-col items-center justify-center text-center min-h-[250px]">
             <h2 className="text-xl font-bold text-text-main mb-2">Roadmap Generation</h2>
             <p className="text-text-muted mb-6 max-w-md">The AI generation feature will be implemented in the next phase. Soon, you'll be able to instantly generate tasks and milestones for this project.</p>
             <button disabled className="px-6 py-2 bg-primary/50 text-white font-medium rounded-lg opacity-50 cursor-not-allowed">Generate Roadmap</button>
          </div>
        </div>

        {/* Sidebar Metadata */}
        <div className="space-y-6">
          <div className="glass-card p-6">
            <h3 className="text-sm font-semibold text-text-muted uppercase tracking-wider mb-4">Status & Level</h3>
            <div className="flex flex-col gap-4">
              <div>
                <p className="text-xs text-text-muted mb-1">Current Status</p>
                <ProjectStatusBadge status={project.status} />
              </div>
              <div>
                <p className="text-xs text-text-muted mb-1">Difficulty</p>
                <DifficultyBadge difficulty={project.skillLevel} />
              </div>
            </div>
          </div>

          <div className="glass-card p-6">
            <h3 className="text-sm font-semibold text-text-muted uppercase tracking-wider mb-4">Details</h3>
            <div className="flex flex-col gap-5">
              <div className="flex items-start gap-3">
                <Layers size={18} className="text-primary mt-0.5" />
                <div>
                  <p className="text-xs text-text-muted mb-0.5">Category</p>
                  <p className="font-medium text-text-main">{project.domain}</p>
                </div>
              </div>
              
              <div className="flex items-start gap-3">
                <CheckSquare size={18} className="text-accent mt-0.5" />
                <div>
                  <p className="text-xs text-text-muted mb-0.5">Tech Stack</p>
                  <p className="font-medium text-text-main">{project.preferredTechStack}</p>
                </div>
              </div>

              <div className="flex items-start gap-3">
                <Calendar size={18} className="text-emerald-400 mt-0.5" />
                <div>
                  <p className="text-xs text-text-muted mb-0.5">Target Deadline</p>
                  <p className="font-medium text-text-main">{project.deadline}</p>
                </div>
              </div>

              <div className="flex items-start gap-3">
                <Clock size={18} className="text-orange-400 mt-0.5" />
                <div>
                  <p className="text-xs text-text-muted mb-0.5">Created On</p>
                  <p className="font-medium text-text-main">
                    {new Date(project.createdAt).toLocaleDateString()}
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>

      </div>

      <DeleteProjectModal 
        isOpen={isDeleteModalOpen} 
        projectName={project.title} 
        isDeleting={isDeleting} 
        onConfirm={handleDelete} 
        onCancel={() => setIsDeleteModalOpen(false)} 
      />
    </div>
  );
};

export default ProjectDetails;
