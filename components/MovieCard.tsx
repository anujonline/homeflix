import React from 'react';
import { Play, Plus } from 'lucide-react';
import { motion } from 'framer-motion';
import { Movie } from '../types';

interface MovieCardProps {
  movie: Movie;
  onClick: (movie: Movie) => void;
  width?: string;
  aspectRatio?: string;
}

const MovieCard: React.FC<MovieCardProps> = ({ movie, onClick, width = "w-[200px] md:w-[240px]", aspectRatio = "aspect-[2/3]" }) => {
  return (
    <motion.div
      layoutId={`movie-${movie.id}`}
      className={`relative flex-none ${width} ${aspectRatio} rounded-xl overflow-hidden cursor-pointer group/card snap-start bg-slate-200 dark:bg-slate-800 shadow-sm`}
      whileHover={{ scale: 1.05, y: -10 }}
      transition={{ type: 'spring', stiffness: 300, damping: 20 }}
      onClick={() => onClick(movie)}
    >
      <img
        src={movie.imageUrl}
        alt={movie.title}
        className="w-full h-full object-cover transition-transform duration-500 group-hover/card:scale-110"
        loading="lazy"
      />
      
      <div className="absolute inset-0 bg-gradient-to-t from-black/90 via-black/20 to-transparent opacity-0 group-hover/card:opacity-100 transition-opacity duration-300 flex flex-col justify-end p-4">
        <h3 className="font-bold text-white text-lg leading-tight mb-1">{movie.title}</h3>
        <div className="flex items-center gap-2 text-xs text-slate-300 mb-3">
          <span className="text-primary font-bold">{movie.rating} Match</span>
          <span>{movie.year}</span>
          {movie.mediaType === 'tv' && <span className="border border-slate-600 px-1 rounded text-[10px]">TV</span>}
        </div>
        <div className="flex gap-2">
          <button className="p-2 bg-white text-black rounded-full hover:bg-slate-200 transition-colors">
            <Play size={16} fill="currentColor" />
          </button>
          <button className="p-2 bg-white/20 text-white rounded-full hover:bg-white/30 transition-colors">
            <Plus size={16} />
          </button>
        </div>
      </div>
    </motion.div>
  );
};

export default MovieCard;