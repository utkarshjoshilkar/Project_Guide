import apiClient from '@/lib/axios';

export interface ProgressReport {
  projectId: number;
  featureVerifications: FeatureVerification[];
}

export interface FeatureVerification {
  expectedTask: {
    id: number;
    title: string;
    description: string;
  };
  evidence: {
    featureName: string;
    signals: {
      type: string;
      description: string;
      filePath: string;
      confidenceScore: number;
    }[];
    overallStrength: string;
  };
  status: string;
  reasoning: string;
}

export interface GuidanceAction {
  taskTitle: string;
  actionType: string;
  explanation: string;
}

export interface AdaptiveGuidanceResponse {
  projectId: number;
  recommendedActions: GuidanceAction[];
}

export const githubService = {
  connectRepository: async (projectId: number, repositoryUrl: string) => {
    const response = await apiClient.post(`/projects/${projectId}/github`, { repositoryUrl });
    return response.data;
  },

  acquireRepository: async (projectId: number) => {
    const response = await apiClient.post(`/projects/${projectId}/github/acquire`);
    return response.data;
  },

  scanRepository: async (projectId: number) => {
    const response = await apiClient.get(`/projects/${projectId}/github/scan`);
    return response.data;
  },

  analyzeRepository: async (projectId: number) => {
    const response = await apiClient.get(`/projects/${projectId}/github/analyze`);
    return response.data;
  },

  verifyProgress: async (projectId: number): Promise<ProgressReport> => {
    const response = await apiClient.get(`/projects/${projectId}/github/verify`);
    return response.data;
  },

  getGuidance: async (projectId: number): Promise<AdaptiveGuidanceResponse> => {
    const response = await apiClient.get(`/projects/${projectId}/github/guidance`);
    return response.data;
  }
};
