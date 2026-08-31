import React from 'react';
import { Home, Film, Tv, Compass, Library } from 'lucide-react';

interface Props { activeView: string; onViewChange: (v:any)=>void }
const BottomNav: React.FC<Props> = ({ activeView, onViewChange }) => {
  const items = [
    { id: 'home', label: 'Home', icon: Home },
    { id: 'browse', label: 'Browse', icon: Compass },
    { id: 'movies', label: 'Movies', icon: Film },
    { id: 'series', label: 'Series', icon: Tv },
    { id: 'profile', label: 'Library', icon: Library },
  ];
  return (
    <div className="lg:hidden fixed bottom-0 inset-x-0 z-40 bg-white/95 dark:bg-[#020617]/95 backdrop-blur-xl border-t border-slate-200 dark:border-white/10">
      <div className="flex justify-around px-1 py-1.5 safe-area-pb">
        {items.map(it => {
          const active = activeView === it.id;
          return (
            <button key={it.id} onClick={()=>onViewChange(it.id)} className={`flex flex-1 min-w-0 flex-col items-center gap-1 px-1 py-1.5 rounded-2xl ${active ? 'text-amber-600 dark:text-amber-400' : 'text-slate-500'}`}>
              <span className={`w-7 h-7 grid place-items-center rounded-full ${active ? 'bg-amber-500/15' : ''}`}><it.icon size={18} /></span>
              <span className={`text-[10px] leading-none ${active ? 'font-bold' : 'font-medium'}`}>{it.label}</span>
            </button>
          );
        })}
      </div>
    </div>
  );
}
export default BottomNav;
