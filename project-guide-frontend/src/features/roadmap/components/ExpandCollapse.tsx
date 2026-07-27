import React, { ReactNode, useState, useRef, useEffect } from 'react';
import { ChevronDown } from 'lucide-react';

interface ExpandCollapseProps {
  title: ReactNode;
  children: ReactNode;
  onExpand?: () => void;
  defaultExpanded?: boolean;
}

export const ExpandCollapse = ({ title, children, onExpand, defaultExpanded = false }: ExpandCollapseProps) => {
  const [isExpanded, setIsExpanded] = useState(defaultExpanded);
  const contentRef = useRef<HTMLDivElement>(null);
  const [height, setHeight] = useState<number | undefined>(defaultExpanded ? undefined : 0);

  const toggle = () => {
    if (!isExpanded && onExpand) {
      onExpand();
    }
    setIsExpanded(!isExpanded);
  };

  useEffect(() => {
    if (!isExpanded || !contentRef.current) {
      setHeight(0);
      return;
    }

    const resizeObserver = new ResizeObserver((entries) => {
      for (let entry of entries) {
        setHeight(entry.target.scrollHeight);
      }
    });

    resizeObserver.observe(contentRef.current);
    
    // Initial height set
    setHeight(contentRef.current.scrollHeight);

    return () => resizeObserver.disconnect();
  }, [isExpanded]);

  return (
    <div className="border border-white/5 rounded-xl overflow-hidden bg-background/30 transition-colors hover:bg-background/50">
      <button 
        onClick={toggle}
        className="w-full flex items-center justify-between p-4 text-left focus:outline-none"
      >
        <div className="flex-1">{title}</div>
        <div className={`transform transition-transform duration-300 ml-4 text-text-muted ${isExpanded ? 'rotate-180' : ''}`}>
          <ChevronDown size={20} />
        </div>
      </button>
      
      <div 
        className="transition-all duration-300 ease-in-out overflow-hidden"
        style={{ height: height !== undefined ? height : 'auto' }}
      >
        <div ref={contentRef} className="p-4 border-t border-white/5 bg-background/20">
          {children}
        </div>
      </div>
    </div>
  );
};
