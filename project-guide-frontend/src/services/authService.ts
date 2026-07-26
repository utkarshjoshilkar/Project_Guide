/**
 * Purpose: Authentication API Service.
 * Responsibilities: Handles login, register, and token management via Axios.
 * Dependencies: axios instance, auth types.
 * Future extensibility: Add forgotPassword, resetPassword, or OAuth endpoints.
 */

import apiClient from '@/lib/axios';
import { AuthResponse } from '@/types/auth';

export const authService = {
  login: async (credentials: any): Promise<AuthResponse> => {
    const response = await apiClient.post<AuthResponse>('/auth/login', credentials);
    return response.data;
  },

  register: async (userData: any): Promise<AuthResponse> => {
    const response = await apiClient.post<AuthResponse>('/auth/register', userData);
    return response.data;
  },

  // Mock logout as JWT is stateless. Real implementation would call a backend invalidation endpoint if it existed.
  logout: async (): Promise<void> => {
    // Optionally call an endpoint like /auth/logout if implemented in backend
    return Promise.resolve();
  },

  // Refresh user data (if an endpoint exists, otherwise it returns current state or throws)
  refreshUser: async (): Promise<any> => {
    // Since backend does not have a /auth/me right now, we can fetch from dashboard to verify token
    const response = await apiClient.get('/dashboard');
    return response.data;
  }
};
