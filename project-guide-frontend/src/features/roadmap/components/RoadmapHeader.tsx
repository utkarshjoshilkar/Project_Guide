import React from 'react';
import PageHeader from '@/components/layout/PageHeader';

interface RoadmapHeaderProps {
  projectName: string;
  projectId: number;
}

export const RoadmapHeader = ({ projectName, projectId }: RoadmapHeaderProps) => {
  return (
    <PageHeader 
      title="Learning Roadmap" 
      subtitle={`AI-generated roadmap for: ${projectName}`}
      breadcrumbs={[
        { label: 'Home', path: '/dashboard' },
        { label: 'Projects', path: '/projects' },
        { label: 'Project Details', path: `/projects/${projectId}` },
        { label: 'Roadmap' }
      ]}
    />
  );
};
