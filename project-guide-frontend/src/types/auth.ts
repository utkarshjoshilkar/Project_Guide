/**
 * Purpose: TypeScript interfaces for the authentication module.
 * Responsibilities: Define shapes for Auth requests and responses.
 * Dependencies: None
 * Future extensibility: Expand AuthResponse if backend adds roles, permissions, etc.
 */

export interface AuthResponse {
  jwt: string;
  user: {
    userId: number;
    email: string;
    role: string;
    fullName: string;
  };
}
