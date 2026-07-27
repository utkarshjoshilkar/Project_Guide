import React from 'react';
import { AlertCircle } from 'lucide-react';

interface CompletionDialogProps {
  isOpen: boolean;
  title: string;
  message: string;
  onConfirm: () => void;
  onCancel: () => void;
}

export const CompletionDialog = ({ isOpen, title, message, onConfirm, onCancel }: CompletionDialogProps) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm animate-in fade-in duration-200">
      <div className="glass-card max-w-md w-full mx-4 p-6 rounded-2xl animate-in zoom-in-95 duration-200">
        <div className="flex items-start gap-4">
          <div className="p-3 bg-primary/10 text-primary rounded-full">
            <AlertCircle size={24} />
          </div>
          <div className="flex-1">
            <h3 className="text-lg font-bold text-text-main mb-2">{title}</h3>
            <p className="text-text-muted text-sm mb-6">{message}</p>
            <div className="flex justify-end gap-3">
              <button 
                onClick={onCancel}
                className="px-4 py-2 rounded-lg text-text-main font-medium hover:bg-white/5 transition-colors"
              >
                Cancel
              </button>
              <button 
                onClick={onConfirm}
                className="px-4 py-2 rounded-lg bg-primary text-white font-medium hover:bg-primary-hover transition-colors shadow-lg shadow-primary/20"
              >
                Confirm
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
