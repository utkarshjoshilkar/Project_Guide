import React, { useContext } from 'react';
import { Menu, Moon, Sun } from 'lucide-react';
import { ThemeContext } from '@/context/ThemeContext';
import { AuthContext } from '@/context/AuthContext';

interface NavbarProps {
  onMenuClick: () => void;
}

const Navbar: React.FC<NavbarProps> = ({ onMenuClick }) => {
  const { theme, toggleTheme } = useContext(ThemeContext);
  const { user } = useContext(AuthContext);

  const getInitials = (name?: string) => {
    if (!name) return 'U';
    return name.charAt(0).toUpperCase();
  };

  return (
    <header className="h-16 glass border-b border-white/5 flex items-center px-4 md:px-6 justify-between sticky top-0 z-30">
      <div className="flex items-center gap-4">
        <button 
          className="p-2 -ml-2 rounded-lg text-text-muted hover:text-text-main hover:bg-white/10 md:hidden transition-colors"
          onClick={onMenuClick}
        >
          <Menu size={24} />
        </button>
        <div className="font-medium text-text-muted md:hidden">Project Guide</div>
      </div>
      
      <div className="flex items-center gap-4">
        <button
          onClick={toggleTheme}
          className="p-2 rounded-full text-text-muted hover:text-text-main hover:bg-white/10 transition-colors"
          aria-label="Toggle Theme"
        >
          {theme === 'dark' ? <Sun size={20} /> : <Moon size={20} />}
        </button>
        
        <div className="w-9 h-9 rounded-full bg-gradient-to-br from-accent to-primary flex items-center justify-center text-white font-bold shadow-lg shadow-accent/20 border border-white/10">
          {getInitials(user?.name)}
        </div>
      </div>
    </header>
  );
};

export default Navbar;
