import { TaskResponse } from '@/types/roadmap';

export interface ProgressUpdateResponse {
  success: boolean;
  task: TaskResponse;
  message?: string;
}
