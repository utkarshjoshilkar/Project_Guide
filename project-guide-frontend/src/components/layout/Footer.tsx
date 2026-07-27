import React from 'react';

const Footer: React.FC = () => {
  return (
    <footer className="py-6 px-6 text-center text-sm text-text-muted border-t border-white/5 glass mt-auto">
      <p>&copy; {new Date().getFullYear()} Project Guide. All rights reserved.</p>
    </footer>
  );
};

export default Footer;
