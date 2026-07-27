/**
 * Purpose: Register form component.
 * Responsibilities: Manages local form state, validation, and submission for Registration.
 * Dependencies: react, AuthInput, PasswordInput, AuthButton, authService, AuthContext
 * Future extensibility: Add more robust password strength requirements.
 */

import React, { useState, useContext } from 'react';
import { AuthInput } from './AuthInput';
import { PasswordInput } from './PasswordInput';
import { AuthButton } from './AuthButton';
import { authService } from '@/services/authService';
import { AuthContext } from '@/context/AuthContext';
import { useNavigate, Link } from 'react-router-dom';

export const RegisterForm = () => {
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [errors, setErrors] = useState<{ fullName?: string; email?: string; password?: string; confirmPassword?: string; general?: string }>({});
  const [isLoading, setIsLoading] = useState(false);
  
  const { login } = useContext(AuthContext);
  const navigate = useNavigate();

  const validate = () => {
    const newErrors: any = {};
    if (!fullName) newErrors.fullName = 'Full Name is required';
    
    if (!email) newErrors.email = 'Email is required';
    else if (!/\S+@\S+\.\S+/.test(email)) newErrors.email = 'Email is invalid';
    
    if (!password) newErrors.password = 'Password is required';
    else if (password.length < 6) newErrors.password = 'Password must be at least 6 characters';
    
    if (password !== confirmPassword) newErrors.confirmPassword = 'Passwords do not match';
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    setIsLoading(true);
    setErrors({});

    try {
      const payload = { fullName, email, password, role: 'STUDENT' };
      await authService.register(payload);
      
      // The backend returns null for the token on register. 
      // We must explicitly call login to get the JWT.
      const loginResponse = await authService.login({ email, password });
      
      let userData = { email, fullName, role: 'STUDENT' };
      try {
        const payloadDecoded = JSON.parse(atob(loginResponse.token.split('.')[1]));
        if (payloadDecoded.sub) userData.email = payloadDecoded.sub;
      } catch (e) {
        // Fallback
      }

      // Auto-login after registration
      login(loginResponse.token, userData);
      navigate('/dashboard');
    } catch (error: any) {
      setErrors({ 
        general: error.response?.data?.message || 'Registration failed. Please try again.' 
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
        label="Full Name" 
        type="text" 
        placeholder="John Doe"
        value={fullName}
        onChange={(e) => setFullName(e.target.value)}
        error={errors.fullName}
      />
      
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
        placeholder="Create a strong password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        error={errors.password}
      />
      
      <PasswordInput 
        label="Confirm Password" 
        placeholder="Repeat your password"
        value={confirmPassword}
        onChange={(e) => setConfirmPassword(e.target.value)}
        error={errors.confirmPassword}
      />

      <AuthButton type="submit" isLoading={isLoading} className="mt-4">
        Create Account
      </AuthButton>
      
      <p className="text-center text-text-muted mt-6 text-sm">
        Already have an account?{' '}
        <Link to="/login" className="text-accent hover:text-accent-light font-medium transition-colors">
          Sign In
        </Link>
      </p>
    </form>
  );
};
