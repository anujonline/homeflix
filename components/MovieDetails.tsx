import React, { useEffect, useState } from 'react';
import { X, Play, Plus, ThumbsUp, Share2 } from 'lucide-react';
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

    let isMounted = true;
    const loadDetails = async () => {
      try {
        const details = await fetchMovieDetails(initialMovie.id, initialMovie.mediaType);
        if (isMounted) setMovie(details);
      } catch (e) {
        console.error("Failed to load full movie details", e);
      }
    };
    
    loadDetails();
    
    return () => { isMounted = false; };
  }, [initialMovie]);

  return (
    <div className="fixed inset-0 z-[150] flex items-center justify-center p-4 md:p-8">
      <motion.div 
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        exit={{ opacity: 0 }}
        onClick={onClose}
        className="absolute inset-0 bg-black/60 backdrop-blur-sm"
      />
      
      <motion.div
        layoutId={`movie-${movie.id}`}
        className="relative w-full max-w-4xl bg-white dark:bg-[#18181b] rounded-2xl overflow-hidden shadow-2xl flex flex-col max-h-full z-10"
      >
        <button 
          onClick={onClose}
          className="absolute top-4 right-4 z-10 p-2 bg-black/50 rounded-full text-white hover:bg-black/80 transition-colors"
        >
          <X size={20} />
        </button>

        <div className="relative h-64 md:h-96 flex-shrink-0">
          <img src={movie.backdropUrl} alt={movie.title} className="w-full h-full object-cover" />
          <div className="absolute inset-0 bg-gradient-to-t from-white dark:from-[#18181b] to-transparent" />
          
          <div className="absolute bottom-0 left-0 p-8 w-full">
            <motion.h2 className="text-4xl font-bold text-slate-900 dark:text-white mb-2">{movie.title}</motion.h2>
            <div className="flex items-center gap-4 text-slate-600 dark:text-slate-300 text-sm">
                <span className="text-primary font-bold">{movie.rating} Rating</span>
                <span>{movie.year}</span>
                {movie.duration && <span>{movie.duration}</span>}
                {movie.mediaType === 'tv' ? (
                   <span className="border border-slate-400 dark:border-slate-500 px-1 text-xs rounded">TV Series</span>
                ) : (
                   <span className="border border-slate-400 dark:border-slate-500 px-1 text-xs rounded">HD</span>
                )}
            </div>
          </div>
        </div>

        <div className="p-8 grid md:grid-cols-3 gap-8 overflow-y-auto">
          <div className="md:col-span-2 space-y-6">
            <div className="flex items-center gap-4">
               <button 
                onClick={onPlay}
                className="flex-1 bg-primary text-white py-3 rounded-lg font-bold flex items-center justify-center gap-2 hover:bg-primary-700 transition-colors shadow-lg shadow-primary/20"
               >
                 <Play size={20} fill="currentColor" /> Play
               </button>
               <button 
                onClick={onAddToPlaylist}
                className="p-3 border border-slate-300 dark:border-slate-600 rounded-full text-slate-600 dark:text-slate-300 hover:border-primary hover:text-primary transition-colors"
                title="Add to Playlist"
               >
                 <Plus size={20} />
               </button>
               <button className="p-3 border border-slate-300 dark:border-slate-600 rounded-full text-slate-600 dark:text-slate-300 hover:border-primary hover:text-primary transition-colors">
                 <ThumbsUp size={20} />
               </button>
               <button className="p-3 border border-slate-300 dark:border-slate-600 rounded-full text-slate-600 dark:text-slate-300 hover:border-primary hover:text-primary transition-colors">
                 <Share2 size={20} />
               </button>
            </div>

            <p className="text-slate-700 dark:text-slate-300 text-lg leading-relaxed">
              {movie.description}
            </p>
          </div>

          <div className="space-y-4 text-sm text-slate-500 dark:text-slate-400">
             <div>
                <span className="block text-slate-400 dark:text-slate-500 mb-1">Genres</span>
                <div className="flex flex-wrap gap-2">
                    {movie.genre.map(g => (
                        <span key={g} className="text-slate-900 dark:text-white hover:underline cursor-pointer">{g}</span>
                    ))}
                </div>
             </div>
             <div>
                <span className="block text-slate-400 dark:text-slate-500 mb-1">Content Type</span>
                <span className="text-slate-900 dark:text-white capitalize">{movie.mediaType === 'tv' ? 'TV Series' : 'Movie'}</span>
             </div>
          </div>
        </div>
      </motion.div>
    </div>
  );
};

export default MovieDetails;