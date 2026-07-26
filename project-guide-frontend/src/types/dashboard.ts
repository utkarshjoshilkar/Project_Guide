/**
 * Purpose: TypeScript interfaces for the dashboard module.
 * Responsibilities: Define shapes for Dashboard API responses.
 * Dependencies: Profile Types
 * Future extensibility: Separate project types if they grow too large.
 */

import { StudentProfileResponse } from './profile';

export interface ProjectResponse {
  id: number;
  title: string;
  description: string;
  status: string; // IDEA_SUBMITTED, IN_PROGRESS, COMPLETED
  createdAt: string;
  updatedAt: string;
}

export interface ProjectSummaryResponse {
  projectId: number;
  projectTitle: string;
  projectStatus: string;
  hasRoadmap: boolean;
  roadmapProgressPercentage: number;
  totalMilestones: number;
  completedMilestones: number;
  totalTasks: number;
  completedTasks: number;
}

export interface DashboardResponse {
  userId: number;
  fullName: string;
  email: string;
  profile: StudentProfileResponse | null;
  totalProjects: number;
  projectsByStatus: Record<string, number>;
  overallProgressPercentage: number;
  recentProjects: ProjectResponse[];
  projectSummaries: ProjectSummaryResponse[];
}
