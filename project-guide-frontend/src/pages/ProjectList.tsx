/**
 * Purpose: Project List Page.
 * Responsibilities: Fetches projects, handles local filtering/sorting, renders grid.
 * Dependencies: react, project components, project service
 * Future extensibility: Implement pagination if project count grows large.
 */

import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { projectService } from '@/services/projectService';
import { ProjectResponse } from '@/types/project';

import { ProjectHeader } from '@/features/projects/components/ProjectHeader';
import { ProjectCard } from '@/features/projects/components/ProjectCard';
import { EmptyProjects } from '@/features/projects/components/EmptyProjects';
import { LoadingSkeleton } from '@/features/projects/components/LoadingSkeleton';
import { Search, Plus, Filter } from 'lucide-react';

const ProjectList = () => {
  const navigate = useNavigate();
  const [projects, setProjects] = useState<ProjectResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Filters
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [difficultyFilter, setDifficultyFilter] = useState('ALL');

  useEffect(() => {
    fetchProjects();
  }, []);

  const fetchProjects = async () => {
    try {
      setLoading(true);
      const data = await projectService.getAllProjects();
      // Sort by newest first based on createdAt or id
      data.sort((a, b) => b.id - a.id);
      setProjects(data);
    } catch (err: any) {
      setError(err.message || 'Failed to fetch projects');
    } finally {
      setLoading(false);
    }
  };

  // Client-side filtering
  const filteredProjects = projects.filter(p => {
    const matchesSearch = p.title.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesStatus = statusFilter === 'ALL' || p.status === statusFilter;
    const matchesDifficulty = difficultyFilter === 'ALL' || p.skillLevel === difficultyFilter;
    return matchesSearch && matchesStatus && matchesDifficulty;
  });

  return (
    <div className="max-w-7xl mx-auto py-4">
      <ProjectHeader 
        title="My Projects" 
        subtitle="Manage your learning projects and roadmaps."
        action={
          <button 
            onClick={() => navigate('/projects/new')}
            className="px-4 py-2 bg-primary hover:bg-primary-light text-white rounded-lg flex items-center gap-2 transition-colors font-medium shadow-lg shadow-primary/20"
          >
            <Plus size={18} />
            New Project
          </button>
        }
      />

      {/* Filters Bar */}
      <div className="mb-8 flex flex-col md:flex-row gap-4 items-center bg-surface/30 p-4 rounded-xl border border-white/5">
        <div className="relative flex-1 w-full">
          <Search size={18} className="absolute left-3 top-1/2 -translate-y-1/2 text-text-muted" />
          <input 
            type="text"
            placeholder="Search projects by title..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full bg-background border border-white/10 rounded-lg pl-10 pr-4 py-2 text-white placeholder-text-muted focus:outline-none focus:border-primary/50 transition-colors"
          />
        </div>
        
        <div className="flex gap-4 w-full md:w-auto">
          <div className="flex items-center gap-2 w-full md:w-auto">
            <Filter size={16} className="text-text-muted shrink-0" />
            <select 
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="bg-background border border-white/10 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-primary/50 flex-1 md:w-36 appearance-none"
            >
              <option value="ALL">All Statuses</option>
              <option value="IDEA_SUBMITTED">Idea Submitted</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="COMPLETED">Completed</option>
            </select>
          </div>

          <select 
            value={difficultyFilter}
            onChange={(e) => setDifficultyFilter(e.target.value)}
            className="bg-background border border-white/10 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-primary/50 flex-1 md:w-36 appearance-none"
          >
            <option value="ALL">All Difficulties</option>
            <option value="Beginner">Beginner</option>
            <option value="Intermediate">Intermediate</option>
            <option value="Advanced">Advanced</option>
            <option value="Expert">Expert</option>
          </select>
        </div>
      </div>

      {/* Content */}
      {loading ? (
        <LoadingSkeleton />
      ) : error ? (
        <div className="text-center py-12 text-red-400 bg-red-500/10 rounded-xl border border-red-500/20">
          <p>{error}</p>
          <button onClick={fetchProjects} className="mt-4 px-4 py-2 bg-surface rounded-lg text-white">Try Again</button>
        </div>
      ) : projects.length === 0 ? (
        <EmptyProjects />
      ) : filteredProjects.length === 0 ? (
        <div className="text-center py-20 text-text-muted">
          No projects match your current filters.
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredProjects.map(project => (
            <ProjectCard key={project.id} project={project} />
          ))}
        </div>
      )}
    </div>
  );
};

export default ProjectList;
