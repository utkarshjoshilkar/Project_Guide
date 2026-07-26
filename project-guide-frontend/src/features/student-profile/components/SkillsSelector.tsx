/**
 * Purpose: Skills Selector Component.
 * Responsibilities: Wraps MultiSelect for technical skills input.
 * Dependencies: MultiSelect
 * Future extensibility: Pre-populate suggestions based on popular tech stacks.
 */

import React from 'react';
import { MultiSelect } from './MultiSelect';

interface SkillsSelectorProps {
  skills: string[];
  onChange: (skills: string[]) => void;
  error?: string;
}

export const SkillsSelector = ({ skills, onChange, error }: SkillsSelectorProps) => {
  return (
    <MultiSelect
      label="Technical Skills"
      placeholder="e.g. React, Java, Python"
      items={skills}
      onChange={onChange}
      error={error}
    />
  );
};
