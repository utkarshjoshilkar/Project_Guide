/**
 * Purpose: Dashboard Page.
 * Responsibilities: Fetches dashboard data and composes all dashboard widgets.
 * Dependencies: react, Dashboard components, DashboardService
 * Future extensibility: Implement pull-to-refresh for mobile or real-time websocket updates.
 */

import React, { useEffect, useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '@/context/AuthContext';
import { dashboardService } from '@/services/dashboardService';
import { DashboardResponse } from '@/types/dashboard';

import { DashboardHeader } from '@/features/dashboard/components/DashboardHeader';
import { StatCard } from '@/features/dashboard/components/StatCard';
import { ActivityCard } from '@/features/dashboard/components/ActivityCard';
import { ProgressCard } from '@/features/dashboard/components/ProgressCard';
import { StudentSummaryCard } from '@/features/dashboard/components/StudentSummaryCard';
import { EmptyState } from '@/features/dashboard/components/EmptyState';
import { LoadingSkeleton } from '@/features/dashboard/components/LoadingSkeleton';

import { FolderKanban, CheckSquare, ListTodo, AlertTriangle } from 'lucide-react';

const Dashboard = () => {
  const { profile } = useContext(AuthContext);
  const navigate = useNavigate();
  
  const [data, setData] = useState<DashboardResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchDashboard = async () => {
    try {
      setLoading(true);
      setError(null);
      const res = await dashboardService.getDashboard();
      
      // Profile Guard: If they hit this endpoint but don't have a profile yet
      if (!res.profile && !profile) {
        navigate('/profile/create');
        return;
      }
      
      setData(res);
    } catch (err: any) {
      setError(err.message || 'Failed to load dashboard data.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboard();
  }, []);

  if (loading) return <LoadingSkeleton />;

  if (error) {
    return (
      <div className="flex flex-col items-center justify-center py-20 text-center">
        <AlertTriangle size={48} className="text-red-400 mb-4" />
        <h2 className="text-2xl font-bold mb-2">Something went wrong</h2>
        <p className="text-text-muted mb-6">{error}</p>
        <button 
          onClick={fetchDashboard}
          className="px-6 py-2 bg-surface hover:bg-surface-light border border-white/10 rounded-lg transition-colors"
        >
          Try Again
        </button>
      </div>
    );
  }

  if (!data) return null;

  // Derive aggregated metrics from project summaries
  const totalTasks = data.projectSummaries.reduce((acc, curr) => acc + curr.totalTasks, 0);
  const completedTasks = data.projectSummaries.reduce((acc, curr) => acc + curr.completedTasks, 0);
  const totalMilestones = data.projectSummaries.reduce((acc, curr) => acc + curr.totalMilestones, 0);
  const completedMilestones = data.projectSummaries.reduce((acc, curr) => acc + curr.completedMilestones, 0);

  // Extract weekly hours if available in learningGoal
  let weeklyHours = 10;
  if (data.profile?.learningGoal.includes(' | Hours/Week: ')) {
    const parts = data.profile.learningGoal.split(' | Hours/Week: ');
    weeklyHours = parseInt(parts[1]) || 10;
  }

  return (
    <div className="max-w-7xl mx-auto">
      <DashboardHeader fullName={data.fullName} />
      
      {data.totalProjects === 0 ? (
        <EmptyState />
      ) : (
        <div className="flex flex-col gap-6">
          {/* Top Row: Metrics */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            <StatCard 
              title="Total Projects" 
              value={data.totalProjects} 
              icon={<FolderKanban size={20} />} 
              colorClass="text-primary-light"
            />
            <StatCard 
              title="In Progress" 
              value={data.projectsByStatus['IN_PROGRESS'] || 0} 
              icon={<ListTodo size={20} />} 
              colorClass="text-accent-light"
            />
            <StatCard 
              title="Completed Tasks" 
              value={completedTasks} 
              description={`Out of ${totalTasks} total tasks`}
              icon={<CheckSquare size={20} />} 
              colorClass="text-emerald-400"
            />
            <StatCard 
              title="Pending Milestones" 
              value={totalMilestones - completedMilestones} 
              icon={<AlertTriangle size={20} />} 
              colorClass="text-orange-400"
            />
          </div>

          {/* Middle Row: Progress & Summary */}
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <div className="lg:col-span-2">
              <ProgressCard 
                overallProgressPercentage={data.overallProgressPercentage}
                totalTasks={totalTasks}
                completedTasks={completedTasks}
                totalMilestones={totalMilestones}
                completedMilestones={completedMilestones}
                weeklyHours={weeklyHours}
              />
            </div>
            <div className="lg:col-span-1">
              <StudentSummaryCard profile={data.profile!} />
            </div>
          </div>

          {/* Bottom Row: Recent Activity */}
          <div className="grid grid-cols-1 gap-6">
            <ActivityCard recentProjects={data.recentProjects} />
          </div>
        </div>
      )}
    </div>
  );
};

export default Dashboard;
