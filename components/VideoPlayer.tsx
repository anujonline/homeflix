
import React, { useState, useEffect } from 'react';
import { X, AlertCircle } from 'lucide-react';
import { motion } from 'framer-motion';
import { Movie } from '../types';

interface VideoPlayerProps {
  onClose: () => void;
  movie: Movie;
}

const VideoPlayer: React.FC<VideoPlayerProps> = ({ onClose, movie }) => {
  const [status, setStatus] = useState<'loading' | 'available' | 'unavailable'>('loading');
  const type = movie.mediaType === 'tv' ? 'tv' : 'movie';
  const src = `https://vidsrc-embed.su/embed/${type}/${movie.id}`;

  useEffect(() => {
    let isMounted = true;
    document.body.style.overflow = 'hidden';

    const checkAvailability = async () => {
      try {
        const response = await fetch(src, { method: 'HEAD' });
        if (isMounted) {
            if (response.status === 404) setStatus('unavailable');
            else setStatus('available');
        }
      } catch (error) {
        if (isMounted) setStatus('available');
      }
    };

    checkAvailability();

    return () => { 
      isMounted = false; 
      document.body.style.overflow = 'unset';
    };
  }, [src]);

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      className="fixed inset-0 z-[200] bg-black flex items-center justify-center"
    >
      <button 
        onClick={(e) => { e.stopPropagation(); onClose(); }}
        className="absolute top-6 right-6 md:top-8 md:right-8 text-white/50 hover:text-white transition-colors z-[210] p-3 bg-black/40 rounded-full hover:bg-black/80 backdrop-blur-md active:scale-90"
      >
        <X size={32} />
      </button>

      <div className="w-full h-full relative flex items-center justify-center">
        {status === 'loading' && (
             <div className="flex flex-col items-center gap-6">
                 <div className="w-16 h-16 border-4 border-violet-500 border-t-transparent rounded-full animate-spin"></div>
                 <p className="text-slate-400 font-medium animate-pulse tracking-wide uppercase text-xs">Fetching high-quality stream...</p>
             </div>
        )}

        {status === 'unavailable' && (
            <motion.div 
                initial={{ scale: 0.9, opacity: 0 }}
                animate={{ scale: 1, opacity: 1 }}
                className="text-center p-10 max-w-lg bg-[#09090b] rounded-3xl border border-white/10 mx-4 shadow-2xl"
            >
                <div className="w-20 h-20 bg-red-500/10 text-red-500 rounded-full flex items-center justify-center mx-auto mb-8">
                    <AlertCircle size={40} />
                </div>
                <h3 className="text-3xl font-bold text-white mb-4">Stream Unavailable</h3>
                <p className="text-slate-400 mb-10 leading-relaxed text-lg">
                    Sorry, <span className="text-white font-semibold">"{movie.title}"</span> is currently not available for streaming in your region or provider.
                </p>
                <button 
                    onClick={onClose}
                    className="w-full bg-white text-black px-8 py-4 rounded-2xl font-bold hover:bg-slate-200 transition-all active:scale-95"
                >
                    Dismiss
                </button>
            </motion.div>
        )}

        {status === 'available' && (
             <iframe
                src={src}
                className="w-full h-full border-0"
                allowFullScreen
                allow="autoplay; encrypted-media; fullscreen; picture-in-picture"
                title={`Streaming ${movie.title}`}
            ></iframe>
        )}
      </div>
    </motion.div>
  );
};

export default VideoPlayer;