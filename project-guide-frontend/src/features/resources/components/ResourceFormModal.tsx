import React, { useState, useEffect } from 'react';
import { ResourceRequest, ResourceResponse, ResourceType } from '../types/resource';
import { X, Loader2 } from 'lucide-react';

interface ResourceFormModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (request: ResourceRequest) => Promise<void>;
  initialData?: ResourceResponse | null;
  isMutating: boolean;
}

export const ResourceFormModal: React.FC<ResourceFormModalProps> = ({
  isOpen,
  onClose,
  onSubmit,
  initialData,
  isMutating,
}) => {
  const [title, setTitle] = useState('');
  const [url, setUrl] = useState('');
  const [type, setType] = useState<ResourceType>('ARTICLE');
  const [description, setDescription] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    if (isOpen) {
      if (initialData) {
        setTitle(initialData.title);
        setUrl(initialData.url);
        setType(initialData.type);
        setDescription(initialData.description || '');
      } else {
        setTitle('');
        setUrl('');
        setType('ARTICLE');
        setDescription('');
      }
      setError('');
    }
  }, [isOpen, initialData]);

  if (!isOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim() || !url.trim()) {
      setError('Title and URL are required.');
      return;
    }
    
    // Basic URL validation
    if (!/^https?:\/\//i.test(url)) {
      setError('URL must start with http:// or https://');
      return;
    }

    try {
      await onSubmit({ title, url, type, description });
      onClose();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to save resource.');
    }
  };

  const resourceTypes: { value: ResourceType; label: string }[] = [
    { value: 'YOUTUBE', label: 'Video (YouTube)' },
    { value: 'ARTICLE', label: 'Article' },
    { value: 'DOCUMENTATION', label: 'Documentation' },
    { value: 'GITHUB', label: 'Repository (GitHub)' },
    { value: 'COURSE', label: 'Course' },
    { value: 'BOOK', label: 'Book' },
  ];

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
      <div className="bg-background border border-border rounded-xl shadow-2xl w-full max-w-md overflow-hidden animate-in fade-in zoom-in-95 duration-200">
        <div className="flex items-center justify-between p-4 border-b border-border bg-background-light">
          <h3 className="font-semibold text-text-primary">
            {initialData ? 'Edit Resource' : 'Add Resource'}
          </h3>
          <button
            onClick={onClose}
            className="text-text-muted hover:text-text-primary transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>
        
        <form onSubmit={handleSubmit} className="p-4 space-y-4">
          {error && (
            <div className="p-3 bg-red-500/10 border border-red-500/20 rounded-lg text-red-400 text-sm">
              {error}
            </div>
          )}
          
          <div className="space-y-1.5">
            <label className="text-sm font-medium text-text-primary">Title</label>
            <input
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className="w-full px-3 py-2 bg-background-light border border-border rounded-lg text-sm text-text-primary focus:outline-none focus:border-accent transition-colors"
              placeholder="e.g., React Official Documentation"
              disabled={isMutating}
            />
          </div>

          <div className="space-y-1.5">
            <label className="text-sm font-medium text-text-primary">URL</label>
            <input
              type="url"
              value={url}
              onChange={(e) => setUrl(e.target.value)}
              className="w-full px-3 py-2 bg-background-light border border-border rounded-lg text-sm text-text-primary focus:outline-none focus:border-accent transition-colors"
              placeholder="https://..."
              disabled={isMutating}
            />
          </div>

          <div className="space-y-1.5">
            <label className="text-sm font-medium text-text-primary">Type</label>
            <select
              value={type}
              onChange={(e) => setType(e.target.value as ResourceType)}
              className="w-full px-3 py-2 bg-background-light border border-border rounded-lg text-sm text-text-primary focus:outline-none focus:border-accent transition-colors"
              disabled={isMutating}
            >
              {resourceTypes.map(rt => (
                <option key={rt.value} value={rt.value}>{rt.label}</option>
              ))}
            </select>
          </div>

          <div className="space-y-1.5">
            <label className="text-sm font-medium text-text-primary">Description (Optional)</label>
            <textarea
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              className="w-full px-3 py-2 bg-background-light border border-border rounded-lg text-sm text-text-primary focus:outline-none focus:border-accent transition-colors resize-none h-20"
              placeholder="Add some context about why this is helpful..."
              disabled={isMutating}
            />
          </div>

          <div className="pt-4 flex items-center justify-end gap-3">
            <button
              type="button"
              onClick={onClose}
              disabled={isMutating}
              className="px-4 py-2 text-sm font-medium text-text-muted hover:text-text-primary transition-colors disabled:opacity-50"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isMutating}
              className="flex items-center justify-center gap-2 px-4 py-2 bg-accent hover:bg-accent-light text-white text-sm font-medium rounded-lg transition-colors disabled:opacity-70 disabled:cursor-not-allowed min-w-[100px]"
            >
              {isMutating ? (
                <>
                  <Loader2 className="w-4 h-4 animate-spin" />
                  Saving...
                </>
              ) : (
                initialData ? 'Save Changes' : 'Add Resource'
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
