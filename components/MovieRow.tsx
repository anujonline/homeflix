import React, { useRef } from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { Movie } from '../types';
import MovieCard from './MovieCard';

interface MovieRowProps {
  title: string;
  movies: Movie[];
  onSelectMovie: (movie: Movie) => void;
  onSeeAll?: () => void;
}

const MovieRow: React.FC<MovieRowProps> = ({ title, movies, onSelectMovie, onSeeAll }) => {
  const rowRef = useRef<HTMLDivElement>(null);

  const scroll = (dir: 'left' | 'right') => {
    if (!rowRef.current) return;
    const amount = rowRef.current.clientWidth * 0.85;
    rowRef.current.scrollBy({ left: dir === 'left' ? -amount : amount, behavior: 'smooth' });
  };

  if (!movies || movies.length === 0) return null;

  const isTrending = title.toLowerCase().includes('trending');

  return (
    <div className="py-5 md:py-7">
      <div className="max-w-[1440px] mx-auto px-4 md:px-10">
        <div className="flex items-end justify-between gap-4 mb-3">
          <div className="flex items-center gap-3">
            <h2 className="text-[18px] md:text-[22px] font-bold tracking-tight text-slate-900 dark:text-white">{title}</h2>
            <span className="hidden md:inline-flex text-xs font-medium px-2 py-1 rounded-full bg-slate-100 dark:bg-white/10 text-slate-600 dark:text-slate-300 border border-slate-200 dark:border-white/10">
              {movies.length}
            </span>
          </div>
          <div className="flex items-center gap-2">
            <button
              onClick={() => (onSeeAll ? onSeeAll() : scroll('right'))}
              className="hidden md:inline-flex text-xs font-semibold px-3 py-1.5 rounded-full bg-white dark:bg-white/10 border border-slate-200 dark:border-white/10 text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-white/15 transition"
            >
              See all →
            </button>
            <div className="hidden md:flex items-center gap-1.5">
              <button onClick={() => scroll('left')} className="w-8 h-8 grid place-items-center rounded-full bg-white dark:bg-white/10 border border-slate-200 dark:border-white/10 hover:bg-slate-50 dark:hover:bg-white/15 text-slate-700 dark:text-white transition">
                <ChevronLeft size={16} />
              </button>
              <button onClick={() => scroll('right')} className="w-8 h-8 grid place-items-center rounded-full bg-white dark:bg-white/10 border border-slate-200 dark:border-white/10 hover:bg-slate-50 dark:hover:bg-white/15 text-slate-700 dark:text-white transition">
                <ChevronRight size={16} />
              </button>
            </div>
          </div>
        </div>
      </div>

      <div className="relative">
        <div
          ref={rowRef}
          className="flex gap-3 md:gap-4 overflow-x-auto overflow-y-hidden overscroll-x-contain no-scrollbar px-4 md:px-10 pb-3 pt-2"
          style={{ scrollbarWidth: 'none', WebkitOverflowScrolling: 'touch' as any }}
        >
          {movies.map((movie, i) => (
            <MovieCard key={movie.id} movie={movie} onClick={onSelectMovie} rank={isTrending ? i : undefined} />
          ))}
        </div>
        {/* fade edges — subtle, don't cover rank numbers */}
        <div className="pointer-events-none absolute inset-y-0 left-0 w-6 bg-gradient-to-r from-[#f8fafc] dark:from-[#020617] to-transparent hidden md:block" />
        <div className="pointer-events-none absolute inset-y-0 right-0 w-6 bg-gradient-to-l from-[#f8fafc] dark:from-[#020617] to-transparent hidden md:block" />
      </div>
    </div>
  );
};

export default MovieRow;
