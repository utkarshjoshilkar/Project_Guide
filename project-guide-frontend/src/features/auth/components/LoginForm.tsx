/**
 * Purpose: Login form component.
 * Responsibilities: Manages local form state, validation, and submission for Login.
 * Dependencies: react, AuthInput, PasswordInput, AuthButton, authService, AuthContext
 * Future extensibility: Implement react-hook-form and zod for complex validation.
 */

import React, { useState, useContext } from 'react';
import { AuthInput } from './AuthInput';
import { PasswordInput } from './PasswordInput';
import { AuthButton } from './AuthButton';
import { authService } from '@/services/authService';
import { AuthContext } from '@/context/AuthContext';
import { useNavigate, Link } from 'react-router-dom';

export const LoginForm = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errors, setErrors] = useState<{ email?: string; password?: string; general?: string }>({});
  const [isLoading, setIsLoading] = useState(false);
  
  const { login } = useContext(AuthContext);
  const navigate = useNavigate();

  const validate = () => {
    const newErrors: any = {};
    if (!email) newErrors.email = 'Email is required';
    else if (!/\S+@\S+\.\S+/.test(email)) newErrors.email = 'Email is invalid';
    
    if (!password) newErrors.password = 'Password is required';
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    setIsLoading(true);
    setErrors({});

    try {
      const response = await authService.login({ email, password });
      
      let userData = { email };
      try {
        const payload = JSON.parse(atob(response.token.split('.')[1]));
        if (payload.sub) userData.email = payload.sub;
      } catch (e) {
        // Fallback
      }

      login(response.token, userData);
      navigate('/dashboard');
    } catch (error: any) {
      setErrors({ 
        general: error.response?.data?.message || 'Invalid credentials. Please try again.' 
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-2">
      {errors.general && (
        <div className="p-3 mb-4 bg-red-500/10 border border-red-500/50 rounded-lg text-red-400 text-sm text-center">
          {errors.general}
        </div>
      )}
      
      <AuthInput 
        label="Email Address" 
        type="email" 
        placeholder="you@example.com"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        error={errors.email}
      />
      
      <PasswordInput 
        label="Password" 
        placeholder="Enter your password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        error={errors.password}
      />
      
      <div className="flex justify-end mb-4">
        <Link to="/forgot-password" className="text-sm text-accent hover:text-accent-light transition-colors">
          Forgot password?
        </Link>
      </div>

      <AuthButton type="submit" isLoading={isLoading}>
        Sign In
      </AuthButton>
      
      <p className="text-center text-text-muted mt-6 text-sm">
        Don't have an account?{' '}
        <Link to="/register" className="text-accent hover:text-accent-light font-medium transition-colors">
          Register here
        </Link>
      </p>
    </form>
  );
};
