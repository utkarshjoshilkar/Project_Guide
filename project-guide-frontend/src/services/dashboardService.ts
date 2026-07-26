/**
 * Purpose: API Service for the Dashboard module.
 * Responsibilities: Handles fetching the dashboard summary payload.
 * Dependencies: axios instance, dashboard types.
 * Future extensibility: N/A
 */

import apiClient from '@/lib/axios';
import { DashboardResponse } from '@/types/dashboard';

export const dashboardService = {
  getDashboard: async (): Promise<DashboardResponse> => {
    const response = await apiClient.get<DashboardResponse>('/dashboard');
    return response.data;
  }
};
