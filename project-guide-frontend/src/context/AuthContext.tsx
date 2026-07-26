/**
 * Purpose: Global Authentication Context.
 * Responsibilities: Maintains user session state, handles login/logout logic, checks token presence.
 * Dependencies: react, lib/axios
 * Future extensibility: Store user roles, permissions, profile data, or integrate token refresh.
 */

import React, { createContext, useState, useEffect, ReactNode } from 'react';
import apiClient from '@/lib/axios';

interface AuthContextType {
  isAuthenticated: boolean;
  user: any | null; // Typing can be improved once user type is strictly defined
  login: (token: string, userData: any) => void;
  logout: () => void;
  loading: boolean;
}

export const AuthContext = createContext<AuthContextType>({
  isAuthenticated: false,
  user: null,
  login: () => {},
  logout: () => {},
  loading: true,
});

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState<any | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Initial load: check if token exists
    const token = localStorage.getItem('jwt');
    const storedUser = localStorage.getItem('user');

    if (token && storedUser) {
      setIsAuthenticated(true);
      setUser(JSON.parse(storedUser));
    }
    setLoading(false);
  }, []);

  const login = (token: string, userData: any) => {
    localStorage.setItem('jwt', token);
    localStorage.setItem('user', JSON.stringify(userData));
    setIsAuthenticated(true);
    setUser(userData);
  };

  const logout = () => {
    localStorage.removeItem('jwt');
    localStorage.removeItem('user');
    setIsAuthenticated(false);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, user, login, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
};
