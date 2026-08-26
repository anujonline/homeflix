import React, { useEffect, useState } from 'react';
import { X, Play, Plus, ThumbsUp, Share2, Star, Clock } from 'lucide-react';
import { motion } from 'framer-motion';
import { Movie } from '../types';
import { fetchMovieDetails } from '../services/tmdbService';

interface MovieDetailsProps {
  movie: Movie;
  onClose: () => void;
  onPlay: () => void;
  onAddToPlaylist: () => void;
}

const MovieDetails: React.FC<MovieDetailsProps> = ({ movie: initialMovie, onClose, onPlay, onAddToPlaylist }) => {
  const [movie, setMovie] = useState<Movie>(initialMovie);

  useEffect(() => {
    if (initialMovie.duration) {
      setMovie(initialMovie);
      return;
    }
    let mounted = true;
    fetchMovieDetails(initialMovie.id, initialMovie.mediaType).then(d => mounted && setMovie(d)).catch(() => {});
    return () => { mounted = false; };
  }, [initialMovie]);

  return (
    <div className="fixed inset-0 z-[150] flex items-end md:items-center justify-center">
      <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} onClick={onClose} className="absolute inset-0 bg-black/70 backdrop-blur-sm" />

      <motion.div
        layoutId={`movie-${movie.id}`}
        initial={{ y: 40, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        exit={{ y: 40, opacity: 0 }}
        transition={{ type: 'spring', damping: 28, stiffness: 300 }}
        className="relative w-full md:max-w-3xl bg-white dark:bg-[#0f172a] md:rounded-[28px] rounded-t-[28px] overflow-hidden shadow-2xl max-h-[92vh] md:max-h-[88vh] flex flex-col"
      >
        <button onClick={onClose} className="absolute top-4 right-4 z-10 w-8 h-8 grid place-items-center rounded-full bg-black/60 backdrop-blur text-white hover:bg-black/80 border border-white/10">
          <X size={16} />
        </button>

        {/* hero */}
        <div className="relative h-[42vh] md:h-[360px] shrink-0">
          <img src={movie.backdropUrl} alt={movie.title} className="w-full h-full object-cover" />
          <div className="absolute inset-0 bg-gradient-to-t from-white dark:from-[#0f172a] via-white/10 dark:via-[#0f172a]/40 to-transparent" />
          <div className="absolute bottom-0 p-6 md:p-8 w-full">
            <div className="flex items-center gap-2 mb-2">
              <span className="inline-flex items-center gap-1 bg-amber-400 text-black text-xs font-bold px-2 py-1 rounded-full"><Star size={12} className="fill-black" /> {movie.rating.toFixed(1)}</span>
              <span className="text-xs bg-black/60 backdrop-blur text-white px-2 py-1 rounded-full border border-white/10">{movie.year}</span>
              {movie.duration && <span className="text-xs bg-black/60 backdrop-blur text-white px-2 py-1 rounded-full border border-white/10 inline-flex items-center gap-1"><Clock size={12} /> {movie.duration}</span>}
            </div>
            <h2 className="text-[28px] md:text-4xl font-display font-bold leading-none tracking-tight text-slate-900 dark:text-white md:text-white drop-shadow-sm">{movie.title}</h2>
            <div className="flex items-center gap-2 mt-2 text-xs text-slate-600 dark:text-slate-300 md:text-white/80">
              <span className="hidden md:inline">{movie.genre.slice(0, 3).join(' • ')}</span>
              <span className="inline-flex px-2 py-0.5 rounded-full bg-white text-black font-bold text-[10px] tracking-widest">HD</span>
            </div>
          </div>
        </div>

        <div className="flex-1 overflow-y-auto">
          <div className="p-6 md:p-8">
            <div className="flex gap-3 mb-6">
              <button onClick={onPlay} className="flex-1 md:flex-none inline-flex items-center justify-center gap-2 bg-[#bf9708] hover:bg-[#a68207] text-white px-8 py-3 rounded-full font-bold shadow-lg shadow-amber-500/20">
                <Play size={18} fill="currentColor" /> Play
              </button>
              <button onClick={onAddToPlaylist} className="w-11 h-11 grid place-items-center rounded-full bg-slate-100 dark:bg-white/10 border border-slate-200 dark:border-white/10 hover:bg-slate-200 dark:hover:bg-white/15">
                <Plus size={18} />
              </button>
              <button className="w-11 h-11 grid place-items-center rounded-full bg-slate-100 dark:bg-white/10 border border-slate-200 dark:border-white/10 hidden md:grid">
                <ThumbsUp size={16} />
              </button>
              <button className="w-11 h-11 grid place-items-center rounded-full bg-slate-100 dark:bg-white/10 border border-slate-200 dark:border-white/10 hidden md:grid">
                <Share2 size={16} />
              </button>
            </div>

            <p className="text-[15px] leading-relaxed text-slate-700 dark:text-slate-300">{movie.description}</p>

            <div className="mt-6 grid grid-cols-2 gap-4 text-sm border-t border-slate-100 dark:border-white/5 pt-6">
              <div>
                <div className="text-xs font-bold tracking-widest text-slate-400 mb-1">GENRES</div>
                <div className="flex flex-wrap gap-1.5">
                  {movie.genre.map(g => (
                    <span key={g} className="px-2.5 py-1 rounded-full bg-slate-100 dark:bg-white/10 border border-slate-200 dark:border-white/10 text-xs font-medium">{g}</span>
                  ))}
                </div>
              </div>
              <div>
                <div className="text-xs font-bold tracking-widest text-slate-400 mb-1">DETAILS</div>
                <div className="text-slate-600 dark:text-slate-300 capitalize text-sm">{movie.mediaType === 'tv' ? 'TV Series' : 'Movie'} • {movie.year}</div>
                <div className="text-xs text-slate-500 mt-1">Available in 4K • 5.1 Audio</div>
              </div>
            </div>
          </div>
        </div>

        {/* drag handle mobile */}
        <div className="md:hidden absolute top-2 left-1/2 -translate-x-1/2 w-10 h-1 rounded-full bg-white/80" />
      </motion.div>
    </div>
  );
};

export default MovieDetails;
