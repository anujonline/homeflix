import React from 'react';
import { Play, Info, Star, Clock } from 'lucide-react';
import { Movie } from '../types';
import { motion } from 'framer-motion';

interface HeroProps {
  movie: Movie | null;
  isLoading?: boolean;
  onPlay: () => void;
  onMoreInfo: (movie: Movie) => void;
}

const Hero: React.FC<HeroProps> = ({ movie, isLoading, onPlay, onMoreInfo }) => {
  if (isLoading || !movie) {
    return (
      <div className="relative h-[56vh] md:h-[68vh] w-full bg-slate-200 dark:bg-[#0b1220] overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-t from-white dark:from-[#020617] via-transparent to-transparent" />
        <div className="absolute inset-0 flex items-center justify-center">
          <div className="flex flex-col items-center gap-3">
            <div className="w-10 h-10 border-2 border-amber-500 border-t-transparent rounded-full animate-spin" />
            <span className="text-xs tracking-widest font-bold text-slate-400">LOADING FEATURE</span>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="relative h-[62vh] md:h-[72vh] w-full overflow-hidden bg-black">
      {/* Background */}
      <div className="absolute inset-0">
        <img
          src={movie.backdropUrl}
          alt={movie.title}
          className="w-full h-full object-cover object-center"
        />
        {/* cinematic overlays */}
        <div className="absolute inset-0 bg-gradient-to-t from-[#020617] via-[#020617]/55 to-[#020617]/10" />
        <div className="absolute inset-0 bg-gradient-to-r from-[#020617]/85 via-[#020617]/30 to-transparent hidden md:block" />
        <div className="absolute inset-0 bg-gradient-to-b from-black/30 to-transparent md:hidden" />
      </div>

      {/* Top meta bar */}
      <div className="absolute top-0 inset-x-0 h-20 bg-gradient-to-b from-black/40 to-transparent pointer-events-none hidden md:block" />

      {/* Content */}
      <div className="absolute inset-0 flex items-end">
        <div className="max-w-[1440px] mx-auto w-full px-4 md:px-10 pb-8 md:pb-12 pt-20">
          <motion.div
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6, ease: 'easeOut' }}
            className="max-w-3xl"
          >
            {/* Eyebrow */}
            <div className="flex flex-wrap items-center gap-2 mb-3">
              <span className="inline-flex items-center gap-1.5 bg-amber-400 text-black text-[11px] font-extrabold tracking-widest px-2.5 py-1 rounded-full">
                <span className="w-1.5 h-1.5 rounded-full bg-black animate-pulse" />
                #1 TRENDING
              </span>
              <span className="hidden md:inline-flex items-center gap-1.5 bg-white/10 backdrop-blur text-white text-xs font-medium px-2.5 py-1 rounded-full border border-white/15">
                {movie.mediaType === 'tv' ? 'Series' : 'Movie'} • {movie.year}
              </span>
              {movie.genre[0] && (
                <span className="hidden md:inline-flex text-xs font-medium px-2.5 py-1 rounded-full bg-white text-black">
                  {movie.genre[0]}
                </span>
              )}
              <span className="inline-flex items-center gap-1 bg-black/40 backdrop-blur text-white text-xs px-2 py-1 rounded-full border border-white/10">
                <Star size={12} className="fill-amber-400 text-amber-400" />
                {movie.rating.toFixed(1)}
              </span>
            </div>

            <h1 className="font-display text-[34px] md:text-[56px] font-bold text-white leading-[0.9] tracking-tight drop-shadow-xl line-clamp-2">
              {movie.title}
            </h1>

            <div className="hidden md:flex items-center gap-2 mt-3 text-sm text-white/70">
              <span className="inline-flex items-center gap-1.5"><Clock size={14} /> {movie.duration || '2h 18m'}</span>
              <span className="w-1 h-1 rounded-full bg-white/40" />
              <span>{movie.genre.slice(0, 3).join(' • ')}</span>
              <span className="ml-2 inline-flex items-center gap-1 text-xs font-bold px-2 py-0.5 rounded bg-white text-black">4K HDR</span>
            </div>

            <p className="text-[15px] md:text-[16px] text-white/80 mt-3 md:mt-4 line-clamp-2 md:line-clamp-2 leading-relaxed max-w-2xl">
              {movie.description}
            </p>

            <div className="flex items-center gap-3 mt-6">
              <button
                onClick={(e) => { e.stopPropagation(); onPlay(); }}
                className="flex items-center justify-center gap-2 bg-white text-black px-6 md:px-8 py-3 md:py-3.5 rounded-full font-bold hover:bg-white/90 transition active:scale-[0.98] shadow-lg text-sm md:text-base"
              >
                <Play size={18} fill="currentColor" className="shrink-0" />
                Play
              </button>
              <button
                onClick={(e) => { e.stopPropagation(); onMoreInfo(movie); }}
                className="flex items-center justify-center gap-2 bg-white/15 backdrop-blur text-white px-6 md:px-7 py-3 md:py-3.5 rounded-full font-semibold hover:bg-white/20 transition border border-white/15 text-sm md:text-base"
              >
                <Info size={18} />
                <span className="hidden md:inline">More Info</span>
                <span className="md:hidden">Info</span>
              </button>
              <div className="hidden md:flex items-center gap-2 ml-2 text-xs text-white/60">
                <span className="w-8 h-8 rounded-full bg-white/10 grid place-items-center border border-white/15">16+</span>
                <span>HD • 5.1</span>
              </div>
            </div>
          </motion.div>
        </div>
      </div>
    </div>
  );
};

export default Hero;
