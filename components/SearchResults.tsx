
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
    <div className="container mx-auto px-4 md:px-8 pt-32 pb-20 min-h-screen">
      <div className="mb-8">
        <h2 className="text-3xl font-bold text-white mb-2">Search Results</h2>
        <p className="text-slate-400">
          {isLoading ? `Searching for "${query}"...` : `Found ${results.length} results for "${query}"`}
        </p>
      </div>

      {isLoading ? (
         <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-6">
            {[...Array(10)].map((_, i) => (
              <div key={i} className="aspect-[2/3] bg-slate-800 rounded-xl animate-pulse" />
            ))}
         </div>
      ) : (
        <motion.div 
          className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-6"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.5 }}
        >
          {results.map((movie) => (
            <MovieCard 
              key={movie.id} 
              movie={movie} 
              onClick={onSelectMovie} 
              width="w-full"
            />
          ))}
          
          {results.length === 0 && (
            <div className="col-span-full py-20 text-center text-slate-500">
              <p className="text-xl">No movies or TV shows found matching your query.</p>
            </div>
          )}
        </motion.div>
      )}
    </div>
  );
};

export default SearchResults;
