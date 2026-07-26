/**
 * Purpose: Dashboard Student Summary.
 * Responsibilities: Displays academic context and profile completion status.
 * Dependencies: react, ProgressBar, lucide-react
 * Future extensibility: Calculate actual profile completion percentage based on missing fields.
 */

import React from 'react';
import { ProgressBar } from './ProgressBar';
import { GraduationCap, Award } from 'lucide-react';
import { StudentProfileResponse } from '@/types/profile';

interface StudentSummaryCardProps {
  profile: StudentProfileResponse;
}

export const StudentSummaryCard = ({ profile }: StudentSummaryCardProps) => {
  // Extract degree if mapped properly, else fallback to string parsing
  const collegeParts = profile.college.split(' - ');
  const degree = collegeParts.length > 1 ? collegeParts[0] : 'Student';
  const university = collegeParts.length > 1 ? collegeParts[1] : profile.college;

  return (
    <div className="glass-card p-6 flex flex-col justify-between h-full relative overflow-hidden">
      {/* Decorative background */}
      <div className="absolute top-0 right-0 w-32 h-32 bg-primary/10 rounded-bl-full pointer-events-none"></div>

      <div>
        <div className="flex items-center gap-3 mb-4">
          <div className="p-2 bg-primary/20 rounded-lg text-primary-light">
            <GraduationCap size={24} />
          </div>
          <div>
            <h3 className="font-semibold text-text-main">{degree} in {profile.branch}</h3>
            <p className="text-sm text-text-muted">{university}</p>
          </div>
        </div>
        
        <div className="grid grid-cols-2 gap-4 mt-6 mb-8">
          <div>
            <p className="text-xs text-text-muted mb-1">Current Year</p>
            <p className="font-medium text-text-main">Year {profile.year}</p>
          </div>
          <div>
            <p className="text-xs text-text-muted mb-1">CGPA</p>
            <p className="font-medium text-text-main flex items-center gap-1">
              <Award size={14} className="text-accent" />
              {profile.cgpa}
            </p>
          </div>
        </div>
      </div>

      <div>
        {/* If the profile exists, it is functionally 100% complete for the MVP */}
        <ProgressBar progress={100} label="Profile Completion" colorClass="bg-emerald-500" />
      </div>
    </div>
  );
};
