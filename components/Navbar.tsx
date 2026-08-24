import React, { useState, useEffect, useRef } from 'react';
import { Search, Bell, Menu, X, LogOut, User as UserIcon, Home, Film, Tv, TrendingUp, Sun, Moon } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { useAuth } from '../context/AuthContext';

interface NavbarProps {
  activeView: string;
  onViewChange: (view: any) => void;
  onSearch: (query: string) => void;
  onOpenAuth: () => void;
  isDarkMode: boolean;
  toggleTheme: () => void;
}

const Navbar: React.FC<NavbarProps> = ({ activeView, onViewChange, onSearch, onOpenAuth, isDarkMode, toggleTheme }) => {
  const [isScrolled, setIsScrolled] = useState(false);
  const [isSearchOpen, setIsSearchOpen] = useState(false);
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [showUserMenu, setShowUserMenu] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);
  
  const { user, logout } = useAuth();

  useEffect(() => {
    const handleScroll = () => {
      setIsScrolled(window.scrollY > 20);
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  useEffect(() => {
    if (isSearchOpen && inputRef.current) {
      inputRef.current.focus();
    }
  }, [isSearchOpen]);

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const query = e.target.value;
    setSearchQuery(query);
    onSearch(query);
  };

  const clearSearch = () => {
    setSearchQuery('');
    onSearch('');
    setIsSearchOpen(false);
  };

  const navLinks = [
    { id: 'home', label: 'Home', icon: <Home size={18} /> },
    { id: 'movies', label: 'Movies', icon: <Film size={18} /> },
    { id: 'series', label: 'Series', icon: <Tv size={18} /> },
    { id: 'popular', label: 'New & Popular', icon: <TrendingUp size={18} /> },
  ];

  const handleNavClick = (view: any) => {
    onViewChange(view);
    setIsMobileMenuOpen(false);
    clearSearch();
  };

  return (
    <>
      <motion.nav
        className={`fixed top-0 w-full z-[100] transition-all duration-300 ${
          isScrolled || isSearchOpen || isMobileMenuOpen
            ? 'bg-white/95 dark:bg-slate-950/95 backdrop-blur-md border-b border-slate-200 dark:border-white/5 py-3' 
            : 'bg-gradient-to-b from-black/80 to-transparent py-5'
        }`}
        initial={{ y: -100 }}
        animate={{ y: 0 }}
      >
        <div className="max-w-[1400px] mx-auto px-4 md:px-12 flex items-center justify-between">
          <div className="flex items-center gap-10">
            <button 
              onClick={() => handleNavClick('home')} 
              className="flex items-center gap-2 outline-none group"
            >
              <div className="w-9 h-9 bg-primary rounded-lg flex items-center justify-center text-white font-bold text-xl group-hover:scale-105 transition-transform">H</div>
              <span className="text-2xl font-bold tracking-tighter text-white dark:text-white group-hover:text-primary transition-colors hidden sm:block">HOMEFLIX</span>
            </button>
            
            <div className={`hidden md:flex items-center gap-8 text-sm font-medium transition-opacity duration-300 ${isSearchOpen ? 'opacity-0 pointer-events-none' : 'opacity-100'}`}>
              {navLinks.map((link) => (
                <button
                  key={link.id}
                  onClick={() => handleNavClick(link.id)}
                  className={`transition-colors relative py-2 ${
                    activeView === link.id 
                      ? (isScrolled ? 'text-primary' : 'text-white') 
                      : (isScrolled ? 'text-slate-500 hover:text-primary' : 'text-slate-300 hover:text-white')
                  }`}
                >
                  {link.label}
                  {activeView === link.id && (
                    <motion.div 
                      layoutId="navUnderline" 
                      className="absolute bottom-0 left-0 right-0 h-0.5 bg-primary rounded-full"
                    />
                  )}
                </button>
              ))}
            </div>
          </div>

          <div className="flex items-center gap-1 md:gap-5 text-slate-300 relative">
            {/* Theme Toggle */}
            <button 
              onClick={toggleTheme}
              className={`p-2 rounded-full transition-colors ${isScrolled ? 'text-slate-500 hover:bg-slate-100 dark:hover:bg-white/10 dark:text-slate-400' : 'text-slate-300 hover:bg-white/10'}`}
              aria-label="Toggle Theme"
            >
              {isDarkMode ? <Sun size={20} /> : <Moon size={20} />}
            </button>

            {/* Desktop Search */}
            <div className="hidden md:flex items-center">
              <div className={`relative flex items-center transition-all duration-300 ${isSearchOpen ? 'w-64' : 'w-10'}`}>
                <button 
                  onClick={() => isSearchOpen ? clearSearch() : setIsSearchOpen(true)}
                  className={`p-2 rounded-full hover:bg-white/10 transition-colors z-10 ${isScrolled ? 'text-slate-500' : 'text-slate-300'} ${isSearchOpen ? 'absolute right-0' : ''}`}
                >
                  {isSearchOpen ? <X size={20} /> : <Search size={20} />}
                </button>
                {isSearchOpen && (
                  <motion.input
                    ref={inputRef}
                    initial={{ width: 0, opacity: 0 }}
                    animate={{ width: '100%', opacity: 1 }}
                    type="text"
                    value={searchQuery}
                    onChange={handleSearchChange}
                    placeholder="Search titles..."
                    className="bg-slate-100 dark:bg-slate-900 border border-slate-200 dark:border-white/10 rounded-full py-2 pl-4 pr-10 text-slate-900 dark:text-white focus:outline-none focus:border-primary text-sm w-full"
                  />
                )}
              </div>
            </div>

            {/* Mobile Search Toggle */}
            <button 
              className={`md:hidden p-2 rounded-full ${isScrolled ? 'text-slate-500' : 'text-slate-300'}`}
              onClick={() => setIsMobileMenuOpen(true)}
            >
              <Search size={22} />
            </button>
            
            <button className={`hidden sm:flex p-2 rounded-full transition-colors ${isScrolled ? 'text-slate-500' : 'text-slate-300'}`}>
              <Bell size={20} />
            </button>

            {user ? (
              <div className="relative">
                <button 
                  onClick={() => setShowUserMenu(!showUserMenu)}
                  className="flex items-center gap-2 p-1 rounded-full hover:bg-black/5 dark:hover:bg-white/5 transition-all"
                >
                  {user.avatar ? (
                      <img src={user.avatar} alt={user.name} className="w-8 h-8 rounded-full border border-slate-200 dark:border-white/20" />
                  ) : (
                      <div className="w-8 h-8 rounded-full bg-primary flex items-center justify-center text-white font-bold text-xs uppercase">
                          {user.name.charAt(0)}
                      </div>
                  )}
                </button>

                <AnimatePresence>
                  {showUserMenu && (
                    <>
                      <div className="fixed inset-0 z-10" onClick={() => setShowUserMenu(false)} />
                      <motion.div
                        initial={{ opacity: 0, y: 10, scale: 0.95 }}
                        animate={{ opacity: 1, y: 0, scale: 1 }}
                        exit={{ opacity: 0, y: 10, scale: 0.95 }}
                        className="absolute right-0 top-full mt-3 w-56 bg-white dark:bg-slate-900 border border-slate-200 dark:border-white/10 rounded-2xl shadow-2xl overflow-hidden py-1 z-20"
                      >
                        <div className="px-5 py-4 border-b border-slate-100 dark:border-white/5 bg-slate-50 dark:bg-white/5">
                          <p className="text-sm font-bold text-slate-900 dark:text-white truncate">{user.name}</p>
                          <p className="text-xs text-slate-400 truncate mt-0.5">{user.email}</p>
                        </div>
                        
                        <button 
                          onClick={() => { handleNavClick('profile'); setShowUserMenu(false); }}
                          className="w-full text-left px-5 py-3 text-sm text-slate-600 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-white/5 hover:text-primary transition-colors flex items-center gap-3"
                        >
                          <UserIcon size={16} />
                          Account Settings
                        </button>

                        <button 
                          onClick={() => { logout(); setShowUserMenu(false); }}
                          className="w-full text-left px-5 py-3 text-sm text-red-500 dark:text-red-400 hover:bg-slate-50 dark:hover:bg-white/5 flex items-center gap-3 transition-colors"
                        >
                          <LogOut size={16} />
                          Sign Out
                        </button>
                      </motion.div>
                    </>
                  )}
                </AnimatePresence>
              </div>
            ) : (
              <button 
                  onClick={onOpenAuth}
                  className={`${isScrolled ? 'bg-primary text-white' : 'bg-white text-black'} px-5 py-2 rounded-full text-sm font-bold hover:opacity-90 transition-all active:scale-95 shadow-lg shadow-black/5`}
              >
                  Sign In
              </button>
            )}

            <button 
              onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
              className={`md:hidden p-2 ml-2 ${isScrolled ? 'text-slate-900 dark:text-white' : 'text-white'}`}
            >
              {isMobileMenuOpen ? <X size={26} /> : <Menu size={26} />}
            </button>
          </div>
        </div>
      </motion.nav>

      {/* Mobile Menu Drawer */}
      <AnimatePresence>
        {isMobileMenuOpen && (
          <>
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              onClick={() => setIsMobileMenuOpen(false)}
              className="fixed inset-0 z-[110] bg-black/60 backdrop-blur-sm md:hidden"
            />
            <motion.div
              initial={{ x: '100%' }}
              animate={{ x: 0 }}
              exit={{ x: '100%' }}
              transition={{ type: 'spring', damping: 25, stiffness: 200 }}
              className="fixed inset-y-0 right-0 z-[120] w-[80%] max-w-sm bg-white dark:bg-slate-950 md:hidden flex flex-col p-8"
            >
              <div className="flex justify-between items-center mb-10">
                <span className="text-xl font-bold tracking-tighter text-slate-900 dark:text-white">HOMEFLIX</span>
                <button onClick={() => setIsMobileMenuOpen(false)} className="p-2 text-slate-400 hover:text-primary transition-colors">
                  <X size={24} />
                </button>
              </div>

              {/* Mobile Search */}
              <div className="mb-8">
                <div className="relative">
                  <Search size={18} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
                  <input 
                    type="text" 
                    placeholder="Search movies..."
                    value={searchQuery}
                    onChange={handleSearchChange}
                    className="w-full bg-slate-100 dark:bg-white/5 border border-slate-200 dark:border-white/10 rounded-xl py-3 pl-10 pr-4 text-slate-900 dark:text-white focus:outline-none focus:border-primary"
                  />
                </div>
              </div>

              <div className="flex flex-col gap-2">
                <p className="text-[10px] font-bold text-slate-500 uppercase tracking-[0.2em] mb-4">Discovery</p>
                {navLinks.map((link) => (
                  <button
                    key={link.id}
                    onClick={() => handleNavClick(link.id)}
                    className={`flex items-center gap-4 text-lg font-medium py-4 px-4 rounded-xl transition-all ${
                      activeView === link.id 
                        ? 'bg-primary text-white shadow-lg shadow-primary/20' 
                        : 'text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-white/5'
                    }`}
                  >
                    {link.icon}
                    {link.label}
                  </button>
                ))}
              </div>

              <div className="mt-auto pt-8 border-t border-slate-100 dark:border-white/10">
                {user ? (
                   <button 
                    onClick={() => { handleNavClick('profile'); }}
                    className="flex items-center gap-4 p-4 w-full text-slate-600 dark:text-slate-300 hover:text-primary transition-colors"
                   >
                     <UserIcon size={20} />
                     Account Settings
                   </button>
                ) : (
                  <button 
                    onClick={() => { setIsMobileMenuOpen(false); onOpenAuth(); }}
                    className="w-full bg-primary text-white py-4 rounded-xl font-bold active:scale-[0.98] transition-transform shadow-lg shadow-primary/20"
                  >
                    Sign In to Homeflix
                  </button>
                )}
              </div>
            </motion.div>
          </>
        )}
      </AnimatePresence>
    </>
  );
};

export default Navbar;