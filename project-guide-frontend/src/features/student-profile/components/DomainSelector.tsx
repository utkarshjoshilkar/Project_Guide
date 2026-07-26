/**
 * Purpose: Interested Domains Selector.
 * Responsibilities: Wraps MultiSelect for domain interests.
 * Dependencies: MultiSelect
 * Future extensibility: Predefined domains like Web Dev, ML, etc.
 */

import React from 'react';
import { MultiSelect } from './MultiSelect';

interface DomainSelectorProps {
  domains: string[];
  onChange: (domains: string[]) => void;
  error?: string;
}

export const DomainSelector = ({ domains, onChange, error }: DomainSelectorProps) => {
  return (
    <MultiSelect
      label="Interested Domains"
      placeholder="e.g. Web Development, AI, Cloud"
      items={domains}
      onChange={onChange}
      error={error}
    />
  );
};
