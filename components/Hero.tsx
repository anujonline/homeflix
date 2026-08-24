import React from 'react';
import { Play, Info } from 'lucide-react';
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
      <div className="relative h-[80vh] md:h-[90vh] w-full bg-slate-200 dark:bg-slate-900 animate-pulse overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-t from-slate-50 dark:from-slate-950 via-transparent to-transparent" />
        <div className="absolute inset-0 flex items-center justify-center">
            <div className="w-12 h-12 border-4 border-primary border-t-transparent rounded-full animate-spin"></div>
        </div>
      </div>
    );
  }

  return (
    <div className="relative h-[80vh] md:h-[90vh] w-full overflow-hidden">
      {/* Background Image */}
      <div className="absolute inset-0 z-0">
        <img
          src={movie.backdropUrl}
          alt={movie.title}
          className="w-full h-full object-cover object-center scale-105"
        />
        {/* Gradients for text readability and cinematic fade */}
        <div className="absolute inset-0 bg-gradient-to-t from-slate-50 dark:from-slate-950 via-slate-950/30 dark:via-slate-950/30 to-transparent" />
        <div className="absolute inset-0 bg-gradient-to-r from-slate-950/80 via-slate-950/20 to-transparent dark:from-slate-950/90 dark:via-slate-950/20 dark:to-transparent" />
      </div>

      {/* Content */}
      <div className="absolute inset-0 z-10 flex items-center">
        <div className="max-w-[1400px] mx-auto w-full px-4 md:px-12 pt-16">
          <motion.div 
            initial={{ opacity: 0, x: -50 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.8, ease: "easeOut" }}
            className="max-w-2xl"
          >
            <motion.div 
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ delay: 0.5 }}
              className="flex items-center gap-3 mb-6 text-xs md:text-sm font-bold tracking-widest text-primary uppercase"
            >
              <span className="bg-primary/20 px-3 py-1 rounded-full border border-primary/30">
                Trending {movie.mediaType === 'tv' ? 'Series' : 'Movie'}
              </span>
              {movie.genre[0] && (
                <>
                    <span className="w-1 h-1 rounded-full bg-slate-500" />
                    <span className="text-slate-100">{movie.genre[0]}</span>
                </>
              )}
            </motion.div>

            <h1 className="text-5xl md:text-8xl font-bold text-white mb-8 leading-[0.95] tracking-tighter drop-shadow-2xl">
              {movie.title}
            </h1>

            <p className="text-lg md:text-xl text-slate-200 mb-10 line-clamp-3 leading-relaxed max-w-xl drop-shadow-lg opacity-90">
              {movie.description}
            </p>

            <div className="flex flex-wrap items-center gap-5">
              <button 
                onClick={(e) => { e.stopPropagation(); onPlay(); }}
                className="flex items-center justify-center gap-3 bg-white text-black px-8 md:px-10 py-4 rounded-full font-bold hover:bg-slate-200 transition-all active:scale-95 duration-200 shadow-xl shadow-white/5"
              >
                <Play size={20} fill="currentColor" />
                Play Now
              </button>
              <button 
                onClick={(e) => { e.stopPropagation(); onMoreInfo(movie); }}
                className="flex items-center justify-center gap-3 bg-white/10 backdrop-blur-md text-white px-8 md:px-10 py-4 rounded-full font-bold hover:bg-white/20 transition-all active:scale-95 duration-200 border border-white/20"
              >
                <Info size={20} />
                More Info
              </button>
            </div>
          </motion.div>
        </div>
      </div>
    </div>
  );
};

export default Hero;