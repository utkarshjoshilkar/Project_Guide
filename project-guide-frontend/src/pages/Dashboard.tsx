/**
 * Purpose: Dashboard Page.
 * Responsibilities: Display high-level overview of projects, roadmaps, and tasks.
 * Dependencies: react
 * Future extensibility: Implement charts and real-time task progress updates.
 */

import React from 'react';

const Dashboard = () => {
  return (
    <div>
      <h1 className="text-3xl font-bold mb-6">Dashboard</h1>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="glass-card p-6">
          <h2 className="text-xl font-semibold mb-2">Active Projects</h2>
          <p className="text-4xl font-bold text-primary-light">2</p>
        </div>
        <div className="glass-card p-6">
          <h2 className="text-xl font-semibold mb-2">Pending Tasks</h2>
          <p className="text-4xl font-bold text-accent-light">5</p>
        </div>
        <div className="glass-card p-6">
          <h2 className="text-xl font-semibold mb-2">Completed Milestones</h2>
          <p className="text-4xl font-bold text-emerald-400">12</p>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
