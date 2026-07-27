import React from 'react';
import { Loader2, AlertTriangle, X } from 'lucide-react';
import { ResourceResponse } from '../types/resource';

interface DeleteResourceDialogProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => Promise<void>;
  resource: ResourceResponse | null;
  isMutating: boolean;
}

export const DeleteResourceDialog: React.FC<DeleteResourceDialogProps> = ({
  isOpen,
  onClose,
  onConfirm,
  resource,
  isMutating,
}) => {
  if (!isOpen || !resource) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
      <div className="bg-background border border-border rounded-xl shadow-2xl w-full max-w-sm overflow-hidden animate-in fade-in zoom-in-95 duration-200">
        <div className="flex items-center justify-between p-4 border-b border-border bg-background-light">
          <div className="flex items-center gap-2 text-text-primary font-semibold">
            <AlertTriangle className="w-5 h-5 text-red-500" />
            Delete Resource
          </div>
          <button
            onClick={onClose}
            className="text-text-muted hover:text-text-primary transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>
        
        <div className="p-4">
          <p className="text-sm text-text-muted mb-2">
            Are you sure you want to delete the resource <span className="font-semibold text-text-primary">"{resource.title}"</span>?
          </p>
          <p className="text-sm text-text-muted">
            This action cannot be undone.
          </p>
        </div>

        <div className="p-4 pt-2 flex items-center justify-end gap-3 bg-background-light border-t border-border mt-2">
          <button
            type="button"
            onClick={onClose}
            disabled={isMutating}
            className="px-4 py-2 text-sm font-medium text-text-muted hover:text-text-primary transition-colors disabled:opacity-50"
          >
            Cancel
          </button>
          <button
            onClick={onConfirm}
            disabled={isMutating}
            className="flex items-center justify-center gap-2 px-4 py-2 bg-red-500 hover:bg-red-600 text-white text-sm font-medium rounded-lg transition-colors disabled:opacity-70 disabled:cursor-not-allowed min-w-[100px]"
          >
            {isMutating ? (
              <>
                <Loader2 className="w-4 h-4 animate-spin" />
                Deleting...
              </>
            ) : (
              'Delete'
            )}
          </button>
        </div>
      </div>
    </div>
  );
};
