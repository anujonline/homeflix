import React from 'react';
import { Home, Film, Tv, Compass, Bookmark, History, Library } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

interface SidebarProps {
  activeView: string;
  onViewChange: (v: any) => void;
}

const Sidebar: React.FC<SidebarProps> = ({ activeView, onViewChange }) => {
  const { user } = useAuth();
  const links = [
    { id: 'home', label: 'Home', icon: Home, desc: 'For you' },
    { id: 'browse', label: 'Browse', icon: Compass, desc: 'Search & discover' },
    { id: 'movies', label: 'Movies', icon: Film, desc: 'Top & popular' },
    { id: 'series', label: 'Series', icon: Tv, desc: 'TV shows' },
  ];

  return (
    <aside className="hidden lg:flex w-[272px] shrink-0 flex-col sticky top-0 h-screen bg-white dark:bg-[#070b18] border-r border-slate-200 dark:border-white/[0.06]">
      <div className="px-6 pt-7 pb-6">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-xl bg-gradient-to-br from-amber-400 to-amber-600 grid place-items-center text-black font-black">H</div>
          <div>
            <div className="font-display font-bold tracking-tight leading-none">HOMEFLIX</div>
            <div className="text-[11px] tracking-[0.14em] font-bold text-slate-400">CINEMA OS</div>
          </div>
        </div>
        <div className="mt-6 flex items-center gap-2 text-xs">
          <span className="px-2.5 py-1 rounded-full bg-amber-500 text-black font-bold">PRO</span>
          <span className="text-slate-500">v2 • TMDB</span>
        </div>
      </div>

      <nav className="px-3 space-y-1">
        <div className="px-3 mb-2 text-[11px] font-bold tracking-widest text-slate-400">DISCOVER</div>
        {links.map(l => {
          const active = activeView === l.id;
          return (
            <button
              key={l.id}
              onClick={() => onViewChange(l.id)}
              className={`w-full flex items-center gap-3 px-3 py-3 rounded-2xl text-left transition ${active ? 'bg-slate-900 dark:bg-white text-white dark:text-black shadow-lg' : 'text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-white/[0.06] hover:text-slate-900 dark:hover:text-white'}`}
            >
              <span className={`w-9 h-9 grid place-items-center rounded-xl ${active ? 'bg-white/15 dark:bg-black/10' : 'bg-slate-100 dark:bg-white/[0.06]'}`}>
                <l.icon size={18} />
              </span>
              <span className="flex-1 min-w-0">
                <div className="text-sm font-semibold leading-none">{l.label}</div>
                <div className={`text-xs leading-none mt-1 ${active ? 'text-white/60 dark:text-black/60' : 'text-slate-500'}`}>{l.desc}</div>
              </span>
              {active && <span className="w-1.5 h-1.5 rounded-full bg-amber-400" />}
            </button>
          );
        })}
      </nav>

      <div className="px-3 mt-6">
        <div className="px-3 mb-2 text-[11px] font-bold tracking-widest text-slate-400">LIBRARY</div>
        <button onClick={() => onViewChange('profile')} className={`w-full flex items-center gap-3 px-3 py-3 rounded-2xl text-left ${activeView === 'profile' ? 'bg-slate-900 dark:bg-white text-white dark:text-black' : 'text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-white/[0.06]'}`}>
          <span className="w-9 h-9 grid place-items-center rounded-xl bg-slate-100 dark:bg-white/[0.06]"><Library size={18} /></span>
          <span className="flex-1">
            <div className="text-sm font-semibold leading-none">My Library</div>
            <div className="text-xs text-slate-500 leading-none mt-1">{user ? `${user.history.length} watched • ${user.playlists.length} lists` : 'Sign in to save'}</div>
          </span>
        </button>
        {user && user.history.length > 0 && (
          <div className="mt-3 mx-3 p-3 rounded-2xl bg-slate-50 dark:bg-white/[0.04] border border-slate-200 dark:border-white/5">
            <div className="flex items-center gap-2 text-xs font-semibold text-slate-600 dark:text-slate-300"><History size={14} /> Continue</div>
            <div className="flex -space-x-2 mt-2">
              {user.history.slice(0, 4).map(m => (
                <img key={m.id} src={m.imageUrl} className="w-8 h-12 object-cover rounded-lg border-2 border-white dark:border-[#070b18]" />
              ))}
              {user.history.length > 4 && <div className="w-8 h-12 rounded-lg bg-slate-900 dark:bg-white text-white dark:text-black grid place-items-center text-xs font-bold border-2 border-white dark:border-[#070b18]">+{user.history.length - 4}</div>}
            </div>
          </div>
        )}
      </div>

      <div className="mt-auto p-4 space-y-3">
        <div className="rounded-2xl bg-slate-900 dark:bg-white text-white dark:text-black p-4">
          <div className="text-[11px] font-bold tracking-widest opacity-60">EXPLORE</div>
          <div className="text-sm font-semibold mt-1">Jump to a category</div>
          <div className="flex gap-2 mt-3">
            <button onClick={() => onViewChange('movies')} className="flex-1 py-2 rounded-full bg-white dark:bg-black text-black dark:text-white text-xs font-bold hover:opacity-90">Movies</button>
            <button onClick={() => onViewChange('series')} className="flex-1 py-2 rounded-full bg-white/15 dark:bg-black/5 border border-white/20 dark:border-black/10 text-xs font-bold hover:bg-white/20">Series</button>
          </div>
        </div>
        <div className="px-1 text-[10px] leading-relaxed text-slate-400">
          This product uses the TMDB API but is not endorsed or certified by TMDB.
          <div className="mt-2 flex items-center gap-2 text-[11px]">
            <span className="w-2 h-2 rounded-full bg-slate-300 dark:bg-white/20" /> © 2024 Homeflix • Press <span className="px-1 py-0.5 rounded bg-slate-200 dark:bg-white/10 text-slate-700 dark:text-slate-300 text-[10px] font-bold">/</span> to search
          </div>
        </div>
      </div>
    </aside>
  );
};

export default Sidebar;
