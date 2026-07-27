import React from 'react';
import { Loader2 } from 'lucide-react';

export const LoadingState = () => {
  return (
    <div className="flex flex-col items-center justify-center p-8 gap-3 animate-in fade-in duration-300">
      <Loader2 size={32} className="text-primary animate-spin" />
      <span className="text-text-muted text-sm font-medium">Updating progress...</span>
    </div>
  );
};
