/**
 * Purpose: Reusable Project Form Component.
 * Responsibilities: Handles UI state, validation, and submission for creating/editing projects.
 * Dependencies: react, lucide-react, project types
 * Future extensibility: Support rich text for description.
 */

import React, { useState } from 'react';
import { ProjectFormData } from '@/types/project';
import { AlertTriangle, Loader2 } from 'lucide-react';

interface ProjectFormProps {
  initialData?: ProjectFormData;
  onSubmit: (data: ProjectFormData) => Promise<void>;
  isEditMode?: boolean;
}

const DEFAULT_DATA: ProjectFormData = {
  title: '',
  description: '',
  category: 'Web Development',
  techStack: '',
  difficulty: 'Beginner',
  duration: '1 month',
  expectedOutcome: ''
};

export const ProjectForm = ({ initialData, onSubmit, isEditMode = false }: ProjectFormProps) => {
  const [formData, setFormData] = useState<ProjectFormData>(initialData || DEFAULT_DATA);
  const [errors, setErrors] = useState<Partial<Record<keyof ProjectFormData, string>>>({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitError, setSubmitError] = useState<string | null>(null);

  const validate = () => {
    const newErrors: Partial<Record<keyof ProjectFormData, string>> = {};
    if (!formData.title.trim()) newErrors.title = 'Title is required';
    if (!formData.description.trim()) newErrors.description = 'Description is required';
    if (formData.description.length > 1000) newErrors.description = 'Description cannot exceed 1000 characters';
    if (!formData.techStack.trim()) newErrors.techStack = 'Tech stack is required';
    if (!formData.expectedOutcome.trim()) newErrors.expectedOutcome = 'Expected outcome is required';
    if (formData.expectedOutcome.length > 1000) newErrors.expectedOutcome = 'Expected outcome cannot exceed 1000 characters';
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (errors[name as keyof ProjectFormData]) {
      setErrors(prev => ({ ...prev, [name]: undefined }));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitError(null);
    
    if (!validate()) return;

    try {
      setIsSubmitting(true);
      await onSubmit(formData);
    } catch (err: any) {
      setSubmitError(err.response?.data?.message || err.message || 'An error occurred while saving the project.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6 glass-card p-6 md:p-8 max-w-3xl">
      {submitError && (
        <div className="p-4 rounded-lg bg-red-500/10 border border-red-500/20 text-red-400 flex items-start gap-3">
          <AlertTriangle size={20} className="shrink-0 mt-0.5" />
          <span>{submitError}</span>
        </div>
      )}

      <div>
        <label className="block text-sm font-medium text-text-muted mb-2">Project Title *</label>
        <input 
          type="text"
          name="title"
          value={formData.title}
          onChange={handleChange}
          disabled={isSubmitting}
          className={`w-full bg-background border ${errors.title ? 'border-red-500/50 focus:border-red-500' : 'border-white/10 focus:border-primary/50'} rounded-lg px-4 py-3 text-white placeholder-text-muted focus:outline-none transition-colors`}
          placeholder="e.g. E-Commerce Platform"
        />
        {errors.title && <p className="mt-1 text-sm text-red-400">{errors.title}</p>}
      </div>

      <div>
        <label className="block text-sm font-medium text-text-muted mb-2">Description *</label>
        <textarea 
          name="description"
          value={formData.description}
          onChange={handleChange}
          disabled={isSubmitting}
          rows={4}
          className={`w-full bg-background border ${errors.description ? 'border-red-500/50 focus:border-red-500' : 'border-white/10 focus:border-primary/50'} rounded-lg px-4 py-3 text-white placeholder-text-muted focus:outline-none transition-colors resize-none`}
          placeholder="Describe what the project does..."
        />
        <div className="flex justify-between mt-1">
          {errors.description ? (
            <p className="text-sm text-red-400">{errors.description}</p>
          ) : <span></span>}
          <span className="text-xs text-text-muted">{formData.description.length}/1000</span>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <label className="block text-sm font-medium text-text-muted mb-2">Category</label>
          <select
            name="category"
            value={formData.category}
            onChange={handleChange}
            disabled={isSubmitting}
            className="w-full bg-background border border-white/10 rounded-lg px-4 py-3 text-white focus:border-primary/50 focus:outline-none transition-colors appearance-none"
          >
            <option value="Web Development">Web Development</option>
            <option value="Mobile App">Mobile App</option>
            <option value="Data Science">Data Science</option>
            <option value="Machine Learning">Machine Learning</option>
            <option value="Cybersecurity">Cybersecurity</option>
            <option value="Cloud Computing">Cloud Computing</option>
            <option value="Other">Other</option>
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-text-muted mb-2">Technology Stack *</label>
          <input 
            type="text"
            name="techStack"
            value={formData.techStack}
            onChange={handleChange}
            disabled={isSubmitting}
            className={`w-full bg-background border ${errors.techStack ? 'border-red-500/50 focus:border-red-500' : 'border-white/10 focus:border-primary/50'} rounded-lg px-4 py-3 text-white placeholder-text-muted focus:outline-none transition-colors`}
            placeholder="e.g. React, Node.js, MongoDB"
          />
          {errors.techStack && <p className="mt-1 text-sm text-red-400">{errors.techStack}</p>}
        </div>

        <div>
          <label className="block text-sm font-medium text-text-muted mb-2">Difficulty Level</label>
          <select
            name="difficulty"
            value={formData.difficulty}
            onChange={handleChange}
            disabled={isSubmitting}
            className="w-full bg-background border border-white/10 rounded-lg px-4 py-3 text-white focus:border-primary/50 focus:outline-none transition-colors appearance-none"
          >
            <option value="Beginner">Beginner</option>
            <option value="Intermediate">Intermediate</option>
            <option value="Advanced">Advanced</option>
            <option value="Expert">Expert</option>
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-text-muted mb-2">Estimated Duration</label>
          <select
            name="duration"
            value={formData.duration}
            onChange={handleChange}
            disabled={isSubmitting}
            className="w-full bg-background border border-white/10 rounded-lg px-4 py-3 text-white focus:border-primary/50 focus:outline-none transition-colors appearance-none"
          >
            <option value="1 week">1 Week</option>
            <option value="2 weeks">2 Weeks</option>
            <option value="1 month">1 Month</option>
            <option value="3 months">3 Months</option>
            <option value="6 months">6 Months</option>
          </select>
        </div>
      </div>

      <div>
        <label className="block text-sm font-medium text-text-muted mb-2">Expected Outcome *</label>
        <textarea 
          name="expectedOutcome"
          value={formData.expectedOutcome}
          onChange={handleChange}
          disabled={isSubmitting}
          rows={3}
          className={`w-full bg-background border ${errors.expectedOutcome ? 'border-red-500/50 focus:border-red-500' : 'border-white/10 focus:border-primary/50'} rounded-lg px-4 py-3 text-white placeholder-text-muted focus:outline-none transition-colors resize-none`}
          placeholder="What is the final goal? e.g. A fully deployed working web application with user auth."
        />
        <div className="flex justify-between mt-1">
          {errors.expectedOutcome ? (
            <p className="text-sm text-red-400">{errors.expectedOutcome}</p>
          ) : <span></span>}
          <span className="text-xs text-text-muted">{formData.expectedOutcome.length}/1000</span>
        </div>
      </div>

      <div className="pt-4 border-t border-white/5 flex justify-end">
        <button
          type="submit"
          disabled={isSubmitting}
          className="px-6 py-3 bg-primary hover:bg-primary-light text-white font-medium rounded-lg transition-colors flex items-center justify-center min-w-[150px] disabled:opacity-70"
        >
          {isSubmitting ? (
            <Loader2 className="animate-spin" size={20} />
          ) : (
            isEditMode ? 'Save Changes' : 'Create Project'
          )}
        </button>
      </div>
    </form>
  );
};
