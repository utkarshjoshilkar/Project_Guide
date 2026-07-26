/**
 * Purpose: Reusable Multi-Select/Tag Input.
 * Responsibilities: Manages an array of strings as tags, allows adding and removing.
 * Dependencies: react, lucide-react
 * Future extensibility: Autocomplete dropdown, max tag limit.
 */

import React, { useState, KeyboardEvent } from 'react';
import { X } from 'lucide-react';

interface MultiSelectProps {
  label: string;
  placeholder: string;
  items: string[];
  onChange: (items: string[]) => void;
  error?: string;
}

export const MultiSelect = ({ label, placeholder, items, onChange, error }: MultiSelectProps) => {
  const [inputValue, setInputValue] = useState('');

  const handleKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter' || e.key === ',') {
      e.preventDefault();
      const val = inputValue.trim();
      if (val && !items.includes(val)) {
        onChange([...items, val]);
      }
      setInputValue('');
    }
  };

  const removeItem = (itemToRemove: string) => {
    onChange(items.filter(item => item !== itemToRemove));
  };

  return (
    <div className="mb-4">
      <label className="block text-sm font-medium text-text-muted mb-1 ml-1">
        {label}
      </label>
      
      <div className={`
        flex flex-wrap gap-2 p-2 min-h-[50px] bg-surface/50 border rounded-lg transition-all duration-200 focus-within:ring-2
        ${error ? 'border-red-500 focus-within:ring-red-500/50' : 'border-white/10 focus-within:ring-primary'}
      `}>
        {items.map((item, index) => (
          <span 
            key={index} 
            className="flex items-center gap-1 px-3 py-1 bg-accent/20 text-accent-light rounded-full text-sm"
          >
            {item}
            <button
              type="button"
              onClick={() => removeItem(item)}
              className="hover:text-white focus:outline-none"
            >
              <X size={14} />
            </button>
          </span>
        ))}
        
        <input
          type="text"
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
          onKeyDown={handleKeyDown}
          placeholder={items.length === 0 ? placeholder : ''}
          className="flex-1 min-w-[120px] bg-transparent border-none text-text-main placeholder-text-muted/50 focus:outline-none focus:ring-0 px-2"
        />
      </div>
      {error && <p className="mt-1 text-sm text-red-400 ml-1">{error}</p>}
      <p className="mt-1 text-xs text-text-muted/50 ml-1">Press Enter or comma to add</p>
    </div>
  );
};
