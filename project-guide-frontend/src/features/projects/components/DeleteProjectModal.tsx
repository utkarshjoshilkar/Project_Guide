/**
 * Purpose: Delete Project Confirmation Modal.
 * Responsibilities: Prompts the user before irreversible deletion.
 * Dependencies: react, lucide-react
 * Future extensibility: Support typing project name to confirm.
 */

import React from 'react';
import { AlertTriangle, X } from 'lucide-react';

interface DeleteProjectModalProps {
  isOpen: boolean;
  projectName: string;
  isDeleting: boolean;
  onConfirm: () => void;
  onCancel: () => void;
}

export const DeleteProjectModal = ({ isOpen, projectName, isDeleting, onConfirm, onCancel }: DeleteProjectModalProps) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-background/80 backdrop-blur-sm animate-in fade-in duration-200">
      <div className="bg-surface border border-white/10 rounded-xl max-w-md w-full shadow-2xl overflow-hidden">
        <div className="p-6">
          <div className="flex justify-between items-start mb-4">
            <div className="w-12 h-12 rounded-full bg-red-500/10 flex items-center justify-center text-red-400">
              <AlertTriangle size={24} />
            </div>
            <button 
              onClick={onCancel}
              disabled={isDeleting}
              className="text-text-muted hover:text-white transition-colors disabled:opacity-50"
            >
              <X size={20} />
            </button>
          </div>
          
          <h2 className="text-xl font-bold text-text-main mb-2">Delete Project?</h2>
          <p className="text-text-muted mb-6">
            Are you sure you want to delete <span className="text-white font-medium">"{projectName}"</span>? This action cannot be undone and will permanently remove its roadmap and all associated tasks.
          </p>
          
          <div className="flex gap-3 justify-end">
            <button 
              onClick={onCancel}
              disabled={isDeleting}
              className="px-4 py-2 bg-surface hover:bg-surface-light border border-white/10 rounded-lg text-sm font-medium transition-colors disabled:opacity-50"
            >
              Cancel
            </button>
            <button 
              onClick={onConfirm}
              disabled={isDeleting}
              className="px-4 py-2 bg-red-500 hover:bg-red-600 text-white rounded-lg text-sm font-medium transition-colors disabled:opacity-50 flex items-center gap-2"
            >
              {isDeleting ? (
                <>
                  <span className="w-4 h-4 border-2 border-white/20 border-t-white rounded-full animate-spin"></span>
                  Deleting...
                </>
              ) : (
                'Delete Project'
              )}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
