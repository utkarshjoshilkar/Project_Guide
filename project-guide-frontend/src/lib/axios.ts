/**
 * Purpose: Global Axios instance configuration.
 * Responsibilities: Defines base URL, attaches JWT headers, handles global 401 responses.
 * Dependencies: axios
 * Future extensibility: Can be extended to handle token refresh logic or global error logging.
 */

import axios from 'axios';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to inject JWT
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('jwt');
    if (token && token !== 'undefined' && token !== 'null' && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle 401 Unauthorized globally
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Clear token and optionally redirect to login
      // NOTE: This will be managed more elegantly via AuthContext in the future.
      localStorage.removeItem('jwt');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default apiClient;
