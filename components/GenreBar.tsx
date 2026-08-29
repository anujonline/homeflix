import React from 'react';

interface Props {
  genres: string[];
  active: string | null;
  onSelect: (g: string | null) => void;
}

const GenreBar: React.FC<Props> = ({ genres, active, onSelect }) => {
  if (!genres.length) return null;
  return (
    <div className="flex gap-2 overflow-x-auto no-scrollbar py-2">
      <button onClick={() => onSelect(null)} className={`px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap border transition ${active === null ? 'bg-slate-900 dark:bg-white text-white dark:text-black border-slate-900 dark:border-white' : 'bg-white dark:bg-white/[0.06] border-slate-200 dark:border-white/10 text-slate-700 dark:text-slate-300 hover:bg-slate-50'}`}>All</button>
      {genres.map(g => (
        <button key={g} onClick={() => onSelect(g === active ? null : g)} className={`px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap border transition ${active === g ? 'bg-amber-500 text-black border-amber-500 font-bold' : 'bg-white dark:bg-white/[0.06] border-slate-200 dark:border-white/10 text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-white/10'}`}>{g}</button>
      ))}
    </div>
  );
};

export default GenreBar;
