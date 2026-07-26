/**
 * Purpose: Global Authentication Context.
 * Responsibilities: Maintains user session state, handles login/logout logic, fetches profile on login.
 * Dependencies: react, lib/axios, services/studentProfileService
 * Future extensibility: Store user roles, permissions, profile data, or integrate token refresh.
 */

import React, { createContext, useState, useEffect, ReactNode } from 'react';
import { studentProfileService } from '@/services/studentProfileService';
import { StudentProfileResponse } from '@/types/profile';

interface AuthContextType {
  isAuthenticated: boolean;
  user: any | null; // Typing can be improved once user type is strictly defined
  profile: StudentProfileResponse | null;
  login: (token: string, userData: any) => void;
  logout: () => void;
  refreshProfile: () => Promise<void>;
  loading: boolean;
}

export const AuthContext = createContext<AuthContextType>({
  isAuthenticated: false,
  user: null,
  profile: null,
  login: () => {},
  logout: () => {},
  refreshProfile: async () => {},
  loading: true,
});

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState<any | null>(null);
  const [profile, setProfile] = useState<StudentProfileResponse | null>(null);
  const [loading, setLoading] = useState(true);

  const fetchProfile = async () => {
    try {
      const fetchedProfile = await studentProfileService.getProfile();
      setProfile(fetchedProfile);
    } catch (error) {
      console.error("Failed to fetch profile", error);
    }
  };

  useEffect(() => {
    const initializeAuth = async () => {
      const token = localStorage.getItem('jwt');
      const storedUser = localStorage.getItem('user');

      if (token && storedUser) {
        setIsAuthenticated(true);
        setUser(JSON.parse(storedUser));
        await fetchProfile(); // Fetch profile when user is verified
      }
      setLoading(false);
    };

    initializeAuth();
  }, []);

  const login = async (token: string, userData: any) => {
    localStorage.setItem('jwt', token);
    localStorage.setItem('user', JSON.stringify(userData));
    setIsAuthenticated(true);
    setUser(userData);
    await fetchProfile();
  };

  const logout = () => {
    localStorage.removeItem('jwt');
    localStorage.removeItem('user');
    setIsAuthenticated(false);
    setUser(null);
    setProfile(null);
  };

  const refreshProfile = async () => {
    await fetchProfile();
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, user, profile, login, logout, refreshProfile, loading }}>
      {children}
    </AuthContext.Provider>
  );
};
