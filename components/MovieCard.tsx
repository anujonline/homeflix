import React from 'react';
import { Play, Plus, Star } from 'lucide-react';
import { Movie } from '../types';

interface MovieCardProps {
  movie: Movie;
  onClick: (movie: Movie) => void;
  width?: string;
  aspectRatio?: string;
  rank?: number;
}

const MovieCard: React.FC<MovieCardProps> = ({ movie, onClick, width = "w-[148px] md:w-[180px] lg:w-[200px]", aspectRatio = "aspect-[2/3]", rank }) => {
  const showRank = rank !== undefined && rank < 10;
  return (
    <div
      className={`relative flex-none ${width} group/card cursor-pointer snap-start ${showRank ? 'overflow-visible' : ''} hover:-translate-y-1 transition-transform duration-200`}
      onClick={() => onClick(movie)}
    >
      {/* rank number — outside the overflow-hidden image so it isn't clipped */}
      {showRank && (
        <div
          className="hidden md:block absolute -bottom-1 left-0 z-10 font-display font-black text-[68px] leading-none text-white select-none pointer-events-none"
          style={{ WebkitTextStroke: '3px rgba(0,0,0,0.75)', paintOrder: 'stroke fill', filter: 'drop-shadow(0 2px 8px rgba(0,0,0,0.65))' }}
          aria-hidden
        >
          {rank + 1}
        </div>
      )}
      <div className={`relative ${aspectRatio} rounded-2xl overflow-hidden bg-slate-200 dark:bg-[#0f172a] shadow-sm border border-black/5 dark:border-white/5 ${showRank ? 'ml-5' : ''}`}>
        <img
          src={movie.imageUrl}
          alt={movie.title}
          className="w-full h-full object-cover transition-transform duration-700 group-hover/card:scale-105"
          loading="lazy"
          decoding="async"
        />
        {/* top badges */}
        <div className="absolute top-2 left-2 flex items-center gap-1.5">
          <span className="inline-flex items-center gap-1 bg-black/70 backdrop-blur text-white text-[11px] font-bold px-1.5 py-1 rounded-full">
            <Star size={10} className="fill-amber-400 text-amber-400" />
            {movie.rating.toFixed(1)}
          </span>
          {showRank && (
            <span className="hidden md:inline-flex bg-amber-400 text-black text-[10px] font-black px-1.5 py-1 rounded-full shadow">TOP {rank + 1}</span>
          )}
        </div>
        {movie.mediaType === 'tv' && (
          <span className="absolute top-2 right-2 text-[10px] font-bold tracking-widest px-1.5 py-1 rounded-full bg-white text-black">TV</span>
        )}

        {/* hover overlay - desktop only */}
        <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/20 to-transparent opacity-0 group-hover/card:opacity-100 transition-opacity duration-300 hidden md:flex flex-col justify-end p-3">
          <div className="flex gap-2">
            <span className="w-8 h-8 rounded-full bg-white text-black grid place-items-center shadow">
              <Play size={14} fill="currentColor" className="ml-0.5" />
            </span>
            <span className="w-8 h-8 rounded-full bg-white/15 backdrop-blur text-white grid place-items-center border border-white/20">
              <Plus size={14} />
            </span>
          </div>
        </div>

        {/* play button mobile tap */}
        <div className="absolute inset-0 grid place-items-center opacity-0 group-active/card:opacity-100 transition md:hidden bg-black/30">
          <span className="w-10 h-10 rounded-full bg-white text-black grid place-items-center shadow-lg">
            <Play size={16} fill="currentColor" className="ml-0.5" />
          </span>
        </div>
      </div>

      {/* footer always visible */}
      <div className="mt-2 px-1">
        <h3 className="font-semibold text-[13px] leading-tight line-clamp-1 text-slate-900 dark:text-white">{movie.title}</h3>
        <div className="flex items-center gap-1.5 text-xs text-slate-500 dark:text-slate-400 mt-0.5">
          <span>{movie.year || '—'}</span>
          <span className="w-1 h-1 rounded-full bg-slate-300 dark:bg-slate-600" />
          <span className="truncate">{movie.genre[0] || (movie.mediaType === 'tv' ? 'Series' : 'Movie')}</span>
        </div>
      </div>
    </div>
  );
};

export default MovieCard;
