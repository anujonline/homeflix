
import React from 'react';
import { Movie } from '../types';
import MovieCard from './MovieCard';
import { motion } from 'framer-motion';

interface SearchResultsProps {
  results: Movie[];
  query: string;
  onSelectMovie: (movie: Movie) => void;
  isLoading?: boolean;
}

const SearchResults: React.FC<SearchResultsProps> = ({ results, query, onSelectMovie, isLoading }) => {
  return (
    <div className="max-w-[1440px] mx-auto px-4 md:px-10 pt-28 md:pt-32 pb-24 min-h-screen">
      <div className="flex items-end justify-between gap-4 mb-6">
        <div>
          <h2 className="text-2xl md:text-3xl font-bold tracking-tight text-slate-900 dark:text-white">Search</h2>
          <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
            {isLoading ? `Searching for “${query}”...` : query ? `${results.length} results for “${query}”` : 'Type to search'}
          </p>
        </div>
        {query && !isLoading && <span className="hidden md:inline-flex text-xs font-medium px-3 py-1 rounded-full bg-slate-100 dark:bg-white/10 border border-slate-200 dark:border-white/10">{results.length} titles</span>}
      </div>

      {isLoading ? (
         <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-4 md:gap-6">
            {[...Array(12)].map((_, i) => (
              <div key={i} className="aspect-[2/3] bg-slate-200 dark:bg-white/5 rounded-2xl animate-pulse" />
            ))}
         </div>
      ) : (
        <motion.div 
          className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-4 md:gap-6"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.4 }}
        >
          {results.map((movie) => (
            <MovieCard 
              key={movie.id} 
              movie={movie} 
              onClick={onSelectMovie} 
              width="w-full"
            />
          ))}
          
          {results.length === 0 && query && (
            <div className="col-span-full py-16 text-center">
              <div className="w-16 h-16 rounded-full bg-slate-100 dark:bg-white/5 grid place-items-center mx-auto mb-4 text-2xl">🔍</div>
              <p className="font-semibold text-slate-900 dark:text-white">No results for “{query}”</p>
              <p className="text-sm text-slate-500 mt-1">Try different keywords or browse categories.</p>
            </div>
          )}
        </motion.div>
      )}
    </div>
  );
};

export default SearchResults;
