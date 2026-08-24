import React, { useRef } from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { Movie } from '../types';
import MovieCard from './MovieCard';

interface MovieRowProps {
  title: string;
  movies: Movie[];
  onSelectMovie: (movie: Movie) => void;
}

const MovieRow: React.FC<MovieRowProps> = ({ title, movies, onSelectMovie }) => {
  const rowRef = useRef<HTMLDivElement>(null);

  const scroll = (direction: 'left' | 'right') => {
    if (rowRef.current) {
      const { current } = rowRef;
      const scrollAmount = direction === 'left' ? -current.clientWidth / 1.5 : current.clientWidth / 1.5;
      current.scrollBy({ left: scrollAmount, behavior: 'smooth' });
    }
  };

  if (!movies || movies.length === 0) return null;

  return (
    <div className="py-10 space-y-5 group">
      <div className="max-w-[1400px] mx-auto px-4 md:px-12 flex justify-between items-end">
        <h2 className="text-2xl md:text-3xl font-bold text-slate-900 dark:text-white tracking-tight">{title}</h2>
        <div className="hidden md:flex gap-3 opacity-0 group-hover:opacity-100 transition-opacity duration-300">
          <button 
            onClick={() => scroll('left')}
            className="p-2.5 rounded-full bg-slate-200 hover:bg-primary dark:bg-white/5 dark:hover:bg-primary text-slate-600 dark:text-white transition-colors border border-slate-300 dark:border-white/5 active:scale-90"
            aria-label="Scroll Left"
          >
            <ChevronLeft size={22} />
          </button>
          <button 
            onClick={() => scroll('right')}
            className="p-2.5 rounded-full bg-slate-200 hover:bg-primary dark:bg-white/5 dark:hover:bg-primary text-slate-600 dark:text-white transition-colors border border-slate-300 dark:border-white/5 active:scale-90"
            aria-label="Scroll Right"
          >
            <ChevronRight size={22} />
          </button>
        </div>
      </div>

      <div className="relative">
        <div 
          ref={rowRef}
          className="flex gap-4 md:gap-6 overflow-x-auto no-scrollbar scroll-smooth snap-x pb-4"
        >
          {/* Spacer for alignment */}
          <div className="flex-none w-[16px] md:w-[48px]" />
          
          {movies.map((movie) => (
            <MovieCard key={movie.id} movie={movie} onClick={onSelectMovie} />
          ))}

          {/* Spacer for end alignment */}
          <div className="flex-none w-[16px] md:w-[48px]" />
        </div>
        
        {/* Subtle shadow indicators - adjusted for light/dark mode */}
        <div className="absolute left-0 top-0 bottom-0 w-12 bg-gradient-to-r from-slate-50 dark:from-slate-950 to-transparent pointer-events-none opacity-0 group-hover:opacity-100 transition-opacity" />
        <div className="absolute right-0 top-0 bottom-0 w-12 bg-gradient-to-l from-slate-50 dark:from-slate-950 to-transparent pointer-events-none opacity-0 group-hover:opacity-100 transition-opacity" />
      </div>
    </div>
  );
};

export default MovieRow;