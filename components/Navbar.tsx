import React, { useState, useEffect, useRef } from 'react';
import { Search, Bell, X, LogOut, User as UserIcon, Home, Film, Tv, TrendingUp, Sun, Moon, Bookmark } from 'lucide-react';
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
    const handleScroll = () => setIsScrolled(window.scrollY > 10);
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  useEffect(() => {
    if (isSearchOpen && inputRef.current) inputRef.current.focus();
  }, [isSearchOpen]);

  // cmd+k to focus search
  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      if ((e.metaKey || e.ctrlKey) && e.key.toLowerCase() === 'k') {
        e.preventDefault();
        setIsSearchOpen(true);
      }
      if (e.key === 'Escape') setIsSearchOpen(false);
    };
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, []);

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
    { id: 'home', label: 'Home', icon: Home },
    { id: 'movies', label: 'Movies', icon: Film },
    { id: 'series', label: 'Series', icon: Tv },
    { id: 'popular', label: 'Popular', icon: TrendingUp },
  ];

  const handleNavClick = (view: any) => {
    onViewChange(view);
    setIsMobileMenuOpen(false);
    clearSearch();
  };

  const isTransparent = !isScrolled && !isSearchOpen && activeView === 'home';

  return (
    <>
      <motion.nav
        className={`fixed top-0 w-full z-[100] transition-all duration-300 border-b ${
          isTransparent
            ? 'bg-gradient-to-b from-black/70 via-black/20 to-transparent border-transparent py-4'
            : 'bg-white/80 dark:bg-[#020617]/80 backdrop-blur-xl border-slate-200/60 dark:border-white/[0.06] py-3 shadow-sm'
        }`}
        initial={{ y: -20, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
      >
        <div className="max-w-[1440px] mx-auto px-4 md:px-8 flex items-center justify-between gap-4">
          {/* Left: Logo + Nav */}
          <div className="flex items-center gap-6 md:gap-8">
            <button onClick={() => handleNavClick('home')} className="flex items-center gap-2.5 group outline-none">
              <div className="w-[36px] h-[36px] rounded-xl bg-gradient-to-br from-amber-400 to-amber-600 flex items-center justify-center text-black font-black text-[15px] shadow-lg shadow-amber-500/20 group-hover:scale-105 transition-transform">H</div>
              <span className={`hidden sm:block font-display font-bold tracking-tighter text-[20px] leading-none ${isTransparent ? 'text-white' : 'text-slate-900 dark:text-white'}`}>HOMEFLIX</span>
            </button>

            {/* Desktop segmented nav */}
            <div className="hidden lg:flex items-center gap-1 p-1 rounded-full bg-black/5 dark:bg-white/[0.06] border border-black/5 dark:border-white/5">
              {navLinks.map(link => {
                const ActiveIcon = link.icon;
                const active = activeView === link.id;
                return (
                  <button
                    key={link.id}
                    onClick={() => handleNavClick(link.id)}
                    className={`px-4 py-1.5 rounded-full text-sm font-medium transition-all flex items-center gap-1.5 ${
                      active
                        ? 'bg-white dark:bg-white text-black shadow-sm'
                        : isTransparent
                        ? 'text-white/70 hover:text-white hover:bg-white/10'
                        : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white hover:bg-white dark:hover:bg-white/10'
                    }`}
                  >
                    <ActiveIcon size={14} />
                    {link.label}
                  </button>
                );
              })}
            </div>
          </div>

          {/* Right */}
          <div className="flex items-center gap-1.5 md:gap-2">
            {/* Search - desktop */}
            <div className="hidden md:flex items-center">
              <div className={`relative flex items-center transition-all duration-300 ${isSearchOpen ? 'w-[280px]' : 'w-auto'}`}>
                {isSearchOpen ? (
                  <div className="flex items-center gap-2 w-full bg-slate-100 dark:bg-white/[0.08] border border-slate-200 dark:border-white/10 rounded-full px-3 py-2">
                    <Search size={16} className="text-slate-400 shrink-0" />
                    <input
                      ref={inputRef}
                      value={searchQuery}
                      onChange={handleSearchChange}
                      placeholder="Search movies, series..."
                      className="bg-transparent outline-none text-sm flex-1 placeholder:text-slate-400 text-slate-900 dark:text-white"
                    />
                    <button onClick={clearSearch} className="p-1 rounded-full hover:bg-black/10 dark:hover:bg-white/10">
                      <X size={14} />
                    </button>
                  </div>
                ) : (
                  <button
                    onClick={() => setIsSearchOpen(true)}
                    className={`flex items-center gap-2 rounded-full px-3.5 py-2 text-sm font-medium border transition-colors ${
                      isTransparent
                        ? 'bg-white/10 text-white border-white/15 hover:bg-white/15 backdrop-blur'
                        : 'bg-slate-100 dark:bg-white/[0.06] text-slate-600 dark:text-slate-300 border-slate-200 dark:border-white/10 hover:bg-slate-200 dark:hover:bg-white/10'
                    }`}
                  >
                    <Search size={16} />
                    <span className="hidden xl:inline">Search</span>
                    <span className="hidden xl:inline-flex ml-1 text-xs px-1.5 py-0.5 rounded bg-black/10 dark:bg-white/10 border border-black/10 dark:border-white/10">⌘K</span>
                  </button>
                )}
              </div>
            </div>

            {/* Mobile search */}
            <button onClick={() => setIsMobileMenuOpen(true)} className={`md:hidden w-9 h-9 grid place-items-center rounded-full border ${isTransparent ? 'bg-white/10 text-white border-white/15 backdrop-blur' : 'bg-slate-100 dark:bg-white/10 text-slate-600 dark:text-slate-300 border-slate-200 dark:border-white/10'}`}>
              <Search size={16} />
            </button>

            <button
              onClick={toggleTheme}
              className={`w-9 h-9 grid place-items-center rounded-full border transition-colors ${isTransparent ? 'bg-white/10 text-white border-white/15 backdrop-blur hover:bg-white/15' : 'bg-slate-100 dark:bg-white/[0.06] text-slate-600 dark:text-slate-400 border-slate-200 dark:border-white/10 hover:bg-slate-200 dark:hover:bg-white/10'}`}
              aria-label="Toggle theme"
            >
              {isDarkMode ? <Sun size={16} /> : <Moon size={16} />}
            </button>

            <button className={`hidden sm:grid w-9 h-9 place-items-center rounded-full border ${isTransparent ? 'bg-white/10 text-white border-white/15 backdrop-blur' : 'bg-slate-100 dark:bg-white/[0.06] text-slate-500 dark:text-slate-400 border-slate-200 dark:border-white/10'}`}>
              <Bell size={16} />
            </button>

            {user ? (
              <div className="relative">
                <button onClick={() => setShowUserMenu(!showUserMenu)} className="flex items-center gap-2 pl-1 pr-1 py-1 rounded-full bg-slate-900 dark:bg-white text-white dark:text-black hover:opacity-90 transition-opacity">
                  {user.avatar ? <img src={user.avatar} alt={user.name} className="w-7 h-7 rounded-full object-cover" /> : <div className="w-7 h-7 rounded-full bg-primary grid place-items-center text-white text-xs font-bold">{user.name.charAt(0)}</div>}
                  <span className="hidden md:block text-sm font-medium pr-2 max-w-[100px] truncate">{user.name.split(' ')[0]}</span>
                </button>
                <AnimatePresence>
                  {showUserMenu && (
                    <>
                      <div className="fixed inset-0 z-10" onClick={() => setShowUserMenu(false)} />
                      <motion.div
                        initial={{ opacity: 0, y: 8, scale: 0.98 }}
                        animate={{ opacity: 1, y: 0, scale: 1 }}
                        exit={{ opacity: 0, y: 8, scale: 0.98 }}
                        className="absolute right-0 top-full mt-3 w-64 bg-white dark:bg-[#0f172a] border border-slate-200 dark:border-white/10 rounded-2xl shadow-xl overflow-hidden z-20"
                      >
                        <div className="px-4 py-4 flex gap-3 items-center">
                          {user.avatar ? <img src={user.avatar} className="w-10 h-10 rounded-full" /> : <div className="w-10 h-10 rounded-full bg-primary grid place-items-center text-white font-bold">{user.name.charAt(0)}</div>}
                          <div className="min-w-0">
                            <div className="text-sm font-semibold truncate text-slate-900 dark:text-white">{user.name}</div>
                            <div className="text-xs text-slate-500 truncate">{user.email}</div>
                          </div>
                        </div>
                        <div className="h-px bg-slate-100 dark:bg-white/5" />
                        <button onClick={() => { handleNavClick('profile'); setShowUserMenu(false); }} className="w-full text-left px-4 py-2.5 text-sm flex items-center gap-2.5 hover:bg-slate-50 dark:hover:bg-white/[0.04] text-slate-700 dark:text-slate-300">
                          <UserIcon size={16} /> Profile & Lists
                        </button>
                        <button onClick={() => { handleNavClick('profile'); setShowUserMenu(false); }} className="w-full text-left px-4 py-2.5 text-sm flex items-center gap-2.5 hover:bg-slate-50 dark:hover:bg-white/[0.04] text-slate-700 dark:text-slate-300">
                          <Bookmark size={16} /> My List
                        </button>
                        <div className="h-px bg-slate-100 dark:bg-white/5" />
                        <button onClick={() => { logout(); setShowUserMenu(false); }} className="w-full text-left px-4 py-2.5 text-sm flex items-center gap-2.5 hover:bg-slate-50 dark:hover:bg-white/[0.04] text-red-600">
                          <LogOut size={16} /> Sign out
                        </button>
                      </motion.div>
                    </>
                  )}
                </AnimatePresence>
              </div>
            ) : (
              <button onClick={onOpenAuth} className="bg-[#bf9708] hover:bg-[#a68207] text-white px-5 py-2 rounded-full text-sm font-semibold shadow-lg shadow-amber-500/20 transition-colors">
                Sign in
              </button>
            )}
          </div>
        </div>
      </motion.nav>

      {/* Mobile bottom nav */}
      <div className="lg:hidden fixed bottom-0 inset-x-0 z-[90] bg-white/95 dark:bg-[#020617]/95 backdrop-blur-xl border-t border-slate-200 dark:border-white/10 safe-area-pb">
        <div className="flex items-center justify-around px-2 py-1.5">
          {navLinks.map(link => {
            const Icon = link.icon;
            const active = activeView === link.id;
            return (
              <button key={link.id} onClick={() => handleNavClick(link.id)} className={`flex flex-col items-center gap-1 py-1.5 px-3 rounded-2xl min-w-[64px] transition-colors ${active ? 'text-[#bf9708]' : 'text-slate-500 dark:text-slate-400'}`}>
                <span className={`w-6 h-6 grid place-items-center rounded-full ${active ? 'bg-amber-500/15' : ''}`}>
                  <Icon size={18} />
                </span>
                <span className={`text-[10px] font-medium tracking-wide ${active ? 'font-bold' : ''}`}>{link.label}</span>
              </button>
            );
          })}
          <button onClick={() => handleNavClick('profile')} className={`flex flex-col items-center gap-1 py-1.5 px-3 rounded-2xl min-w-[64px] ${activeView === 'profile' ? 'text-[#bf9708]' : 'text-slate-500 dark:text-slate-400'}`}>
            <span className={`w-6 h-6 grid place-items-center rounded-full ${activeView === 'profile' ? 'bg-amber-500/15' : ''}`}>
              <UserIcon size={18} />
            </span>
            <span className="text-[10px] font-medium">You</span>
          </button>
        </div>
      </div>

      {/* Mobile search sheet */}
      <AnimatePresence>
        {isMobileMenuOpen && (
          <>
            <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} onClick={() => setIsMobileMenuOpen(false)} className="fixed inset-0 z-[110] bg-black/40 backdrop-blur-sm lg:hidden" />
            <motion.div
              initial={{ y: '100%' }}
              animate={{ y: 0 }}
              exit={{ y: '100%' }}
              transition={{ type: 'spring', damping: 28, stiffness: 300 }}
              className="fixed bottom-0 inset-x-0 z-[120] bg-white dark:bg-[#0f172a] rounded-t-[28px] p-6 pb-10 lg:hidden max-h-[85vh] overflow-auto"
            >
              <div className="w-10 h-1 rounded-full bg-slate-200 dark:bg-white/10 mx-auto mb-6" />
              <div className="flex items-center justify-between mb-4">
                <h3 className="font-semibold text-slate-900 dark:text-white">Search</h3>
                <button onClick={() => setIsMobileMenuOpen(false)} className="w-8 h-8 grid place-items-center rounded-full bg-slate-100 dark:bg-white/10">
                  <X size={16} />
                </button>
              </div>
              <div className="relative">
                <Search size={18} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
                <input autoFocus value={searchQuery} onChange={handleSearchChange} placeholder="Movies, TV shows, people..." className="w-full bg-slate-100 dark:bg-white/[0.06] border border-slate-200 dark:border-white/10 rounded-2xl py-3.5 pl-11 pr-4 text-slate-900 dark:text-white outline-none focus:border-amber-500" />
              </div>
              <div className="mt-6">
                <div className="text-xs font-bold tracking-widest text-slate-400 mb-3">BROWSE</div>
                <div className="grid grid-cols-2 gap-2">
                  {navLinks.map(l => (
                    <button key={l.id} onClick={() => handleNavClick(l.id)} className={`p-4 rounded-2xl border text-left flex items-center gap-3 ${activeView === l.id ? 'bg-slate-900 dark:bg-white text-white dark:text-black border-slate-900' : 'bg-slate-50 dark:bg-white/[0.04] border-slate-200 dark:border-white/10 text-slate-700 dark:text-slate-300'}`}>
                      <l.icon size={18} /> {l.label}
                    </button>
                  ))}
                </div>
              </div>
            </motion.div>
          </>
        )}
      </AnimatePresence>
    </>
  );
};

export default Navbar;
