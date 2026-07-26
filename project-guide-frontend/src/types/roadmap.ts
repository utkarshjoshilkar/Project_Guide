/**
 * Purpose: TypeScript interfaces for AI Roadmap Generation.
 * Responsibilities: Define shapes for the Roadmap responses.
 * Dependencies: None
 * Future extensibility: Extend with Milestone and Task types when building Phase 7.
 */

export interface AIResponse {
  roadmapId: number;
  projectId: number;
  message: string;
  generatedAt: string;
}

export interface RoadmapResponse {
  id: number;
  projectId: number;
  estimatedDurationWeeks: number;
  status: 'DRAFT' | 'ACTIVE' | 'ARCHIVED'; // Assuming standard statuses
  generatedAt: string;
  progressPercentage: number;
}
