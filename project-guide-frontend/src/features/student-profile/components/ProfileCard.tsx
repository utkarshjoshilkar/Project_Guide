/**
 * Purpose: Read-only Profile Display Component.
 * Responsibilities: Renders the fetched profile data elegantly.
 * Dependencies: react, UI components, Profile types
 * Future extensibility: Add action buttons (edit, share) to the header.
 */

import React from 'react';
import { StudentProfileResponse } from '@/types/profile';

interface ProfileCardProps {
  profile: StudentProfileResponse;
}

export const ProfileCard = ({ profile }: ProfileCardProps) => {
  return (
    <div className="glass-card p-8">
      <div className="flex flex-col md:flex-row justify-between items-start mb-6">
        <div>
          <h1 className="text-3xl font-bold text-text-main mb-1">Student Profile</h1>
          <p className="text-text-muted">Overview of your academic and career details.</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div>
          <h2 className="text-lg font-semibold text-primary-light border-b border-white/10 pb-2 mb-4">Academics</h2>
          <div className="space-y-3">
            <div>
              <p className="text-sm text-text-muted">College / University</p>
              <p className="font-medium">{profile.college}</p>
            </div>
            <div>
              <p className="text-sm text-text-muted">Branch / Major</p>
              <p className="font-medium">{profile.branch}</p>
            </div>
            <div className="flex gap-8">
              <div>
                <p className="text-sm text-text-muted">Year</p>
                <p className="font-medium">{profile.year}</p>
              </div>
              <div>
                <p className="text-sm text-text-muted">CGPA</p>
                <p className="font-medium">{profile.cgpa}</p>
              </div>
            </div>
          </div>
        </div>

        <div>
          <h2 className="text-lg font-semibold text-primary-light border-b border-white/10 pb-2 mb-4">Skills & Tech Stack</h2>
          <div className="space-y-4">
            <div>
              <p className="text-sm text-text-muted mb-2">Technical Skills</p>
              <div className="flex flex-wrap gap-2">
                {profile.skills.split(',').map((skill, index) => (
                  <span key={index} className="px-3 py-1 bg-accent/20 text-accent-light rounded-full text-sm">
                    {skill.trim()}
                  </span>
                ))}
              </div>
            </div>
            <div>
              <p className="text-sm text-text-muted">Preferred Language</p>
              <p className="font-medium">{profile.preferredTechStack}</p>
            </div>
          </div>
        </div>
      </div>

      <div className="mt-8">
        <h2 className="text-lg font-semibold text-primary-light border-b border-white/10 pb-2 mb-4">Career & Interests</h2>
        <div className="space-y-3">
          <div>
            <p className="text-sm text-text-muted">Interests & Bio</p>
            <p className="font-medium">{profile.interests}</p>
          </div>
          <div>
            <p className="text-sm text-text-muted">Learning Goals</p>
            <p className="font-medium">{profile.learningGoal}</p>
          </div>
        </div>
      </div>

      <div className="mt-8">
        <h2 className="text-lg font-semibold text-primary-light border-b border-white/10 pb-2 mb-4">Social Links</h2>
        <div className="flex gap-4">
          {profile.githubProfile && (
            <a href={profile.githubProfile} target="_blank" rel="noopener noreferrer" className="text-accent hover:text-accent-light underline">
              GitHub
            </a>
          )}
          {profile.linkedinProfile && (
            <a href={profile.linkedinProfile} target="_blank" rel="noopener noreferrer" className="text-accent hover:text-accent-light underline">
              LinkedIn
            </a>
          )}
          {!profile.githubProfile && !profile.linkedinProfile && (
            <p className="text-text-muted text-sm">No social links provided.</p>
          )}
        </div>
      </div>
    </div>
  );
};
