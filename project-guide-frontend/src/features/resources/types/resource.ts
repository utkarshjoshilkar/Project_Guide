/**
 * Types for the Resources module.
 */

export type ResourceType = 'YOUTUBE' | 'ARTICLE' | 'DOCUMENTATION' | 'GITHUB' | 'COURSE' | 'BOOK';

export interface ResourceResponse {
  id: number;
  taskId: number;
  title: string;
  url: string;
  type: ResourceType;
  description: string;
  createdAt: string;
}

export interface ResourceRequest {
  title: string;
  url: string;
  type: ResourceType;
  description?: string;
}
