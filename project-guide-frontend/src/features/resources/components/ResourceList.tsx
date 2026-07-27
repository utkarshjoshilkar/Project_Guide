import React, { useState } from 'react';
import { useResources } from '../hooks/useResources';
import { ResourceCard } from './ResourceCard';
import { EmptyResources } from './EmptyResources';
import { LoadingSkeleton } from './LoadingSkeleton';
import { ErrorState } from './ErrorState';
import { ResourceFormModal } from './ResourceFormModal';
import { DeleteResourceDialog } from './DeleteResourceDialog';
import { ResourceResponse, ResourceRequest } from '../types/resource';
import { Plus } from 'lucide-react';

interface ResourceListProps {
  taskId: number;
}

export const ResourceList: React.FC<ResourceListProps> = ({ taskId }) => {
  const {
    resources,
    isLoading,
    error,
    isMutating,
    refetch,
    addResource,
    updateResource,
    deleteResource,
  } = useResources(taskId);

  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [editingResource, setEditingResource] = useState<ResourceResponse | null>(null);
  const [deletingResource, setDeletingResource] = useState<ResourceResponse | null>(null);

  const handleAddSubmit = async (request: ResourceRequest) => {
    await addResource(request);
  };

  const handleEditSubmit = async (request: ResourceRequest) => {
    if (editingResource) {
      await updateResource(editingResource.id, request);
    }
  };

  const handleDeleteConfirm = async () => {
    if (deletingResource) {
      await deleteResource(deletingResource.id);
      setDeletingResource(null);
    }
  };

  if (isLoading) {
    return <LoadingSkeleton />;
  }

  if (error) {
    return <ErrorState message={error} onRetry={refetch} />;
  }

  return (
    <div className="mt-4">
      <div className="flex items-center justify-between mb-3">
        <h3 className="text-sm font-semibold text-text-primary">Learning Resources</h3>
        {resources.length > 0 && (
          <button
            onClick={() => setIsAddModalOpen(true)}
            className="flex items-center gap-1 text-xs font-medium text-accent hover:text-accent-light transition-colors"
          >
            <Plus className="w-3.5 h-3.5" />
            Add Resource
          </button>
        )}
      </div>

      {resources.length === 0 ? (
        <EmptyResources onAdd={() => setIsAddModalOpen(true)} />
      ) : (
        <div className="flex flex-col gap-3">
          {resources.map((resource) => (
            <ResourceCard
              key={resource.id}
              resource={resource}
              onEdit={setEditingResource}
              onDelete={setDeletingResource}
            />
          ))}
        </div>
      )}

      {/* Add Modal */}
      <ResourceFormModal
        isOpen={isAddModalOpen}
        onClose={() => setIsAddModalOpen(false)}
        onSubmit={handleAddSubmit}
        isMutating={isMutating}
      />

      {/* Edit Modal */}
      <ResourceFormModal
        isOpen={!!editingResource}
        onClose={() => setEditingResource(null)}
        initialData={editingResource}
        onSubmit={handleEditSubmit}
        isMutating={isMutating}
      />

      {/* Delete Dialog */}
      <DeleteResourceDialog
        isOpen={!!deletingResource}
        onClose={() => setDeletingResource(null)}
        onConfirm={handleDeleteConfirm}
        resource={deletingResource}
        isMutating={isMutating}
      />
    </div>
  );
};
