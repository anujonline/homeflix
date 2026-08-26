import React, { useEffect, useState } from 'react';
import { Play, Info, Star, Clock, ChevronLeft, ChevronRight } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { Movie } from '../types';

interface Props {
  movies: Movie[];
  onPlay: (m: Movie) => void;
  onMore: (m: Movie) => void;
}

const HeroCarousel: React.FC<Props> = ({ movies, onPlay, onMore }) => {
  const [idx, setIdx] = useState(0);
  const [paused, setPaused] = useState(false);
  const featured = movies.slice(0, 5);
  const current = featured[idx];

  useEffect(() => {
    if (paused || featured.length <= 1) return;
    const id = setInterval(() => setIdx(i => (i + 1) % featured.length), 6000);
    return () => clearInterval(id);
  }, [paused, featured.length]);

  if (!current) {
    return (
      <div className="h-[52vh] md:h-[64vh] bg-slate-200 dark:bg-[#0b1220] animate-pulse grid place-items-center">
        <div className="w-10 h-10 border-2 border-amber-500 border-t-transparent rounded-full animate-spin" />
      </div>
    );
  }

  return (
    <div className="relative h-[56vh] md:h-[68vh] overflow-hidden bg-black group" onMouseEnter={() => setPaused(true)} onMouseLeave={() => setPaused(false)}>
      <AnimatePresence mode="wait">
        <motion.div
          key={current.id}
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          transition={{ duration: 0.6 }}
          className="absolute inset-0"
        >
          <img src={current.backdropUrl} alt={current.title} className="w-full h-full object-cover" />
          <div className="absolute inset-0 bg-gradient-to-t from-[#020617] via-[#020617]/50 to-transparent" />
          <div className="absolute inset-0 bg-gradient-to-r from-[#020617]/80 via-transparent to-transparent hidden md:block" />
        </motion.div>
      </AnimatePresence>

      <div className="absolute inset-0 flex items-end">
        <div className="w-full px-4 md:px-10 pb-8 md:pb-10">
          <div className="max-w-3xl">
            <div className="flex flex-wrap items-center gap-2 mb-3">
              <span className="bg-amber-400 text-black text-[11px] font-black tracking-widest px-2.5 py-1 rounded-full">#{idx + 1} TRENDING</span>
              <span className="hidden md:inline-flex bg-white/10 backdrop-blur border border-white/15 text-white text-xs px-2.5 py-1 rounded-full">{current.mediaType === 'tv' ? 'Series' : 'Movie'} • {current.year}</span>
              <span className="inline-flex items-center gap-1 bg-black/50 backdrop-blur border border-white/10 text-white text-xs px-2 py-1 rounded-full"><Star size={12} className="fill-amber-400 text-amber-400" /> {current.rating.toFixed(1)}</span>
              {current.genre[0] && <span className="hidden md:inline-flex bg-white text-black text-xs font-semibold px-2.5 py-1 rounded-full">{current.genre[0]}</span>}
            </div>
            <h1 className="font-display font-bold text-[30px] md:text-[48px] leading-[0.9] tracking-tight text-white drop-shadow-xl line-clamp-2">{current.title}</h1>
            <p className="hidden md:block text-white/80 mt-3 line-clamp-2 max-w-2xl leading-relaxed">{current.description}</p>
            <div className="hidden md:flex items-center gap-2 mt-2 text-sm text-white/60">
              <Clock size={14} /> {current.duration || '—'} <span className="w-1 h-1 rounded-full bg-white/40" /> {current.genre.slice(0, 3).join(' • ')}
            </div>
            <div className="flex gap-3 mt-5">
              <button onClick={() => onPlay(current)} className="bg-white text-black px-6 md:px-8 py-3 rounded-full font-bold flex items-center gap-2 hover:bg-white/90 active:scale-95 text-sm md:text-base"><Play size={18} fill="currentColor" /> Play</button>
              <button onClick={() => onMore(current)} className="bg-white/15 backdrop-blur border border-white/15 text-white px-6 py-3 rounded-full font-semibold flex items-center gap-2 hover:bg-white/20 text-sm"><Info size={18} /> Details</button>
            </div>
          </div>
        </div>
      </div>

      {/* controls */}
      <div className="absolute bottom-4 right-4 md:right-10 flex items-center gap-2">
        <button onClick={() => setIdx(i => (i - 1 + featured.length) % featured.length)} className="hidden md:grid w-9 h-9 place-items-center rounded-full bg-black/40 backdrop-blur border border-white/15 text-white hover:bg-black/60"><ChevronLeft size={16} /></button>
        <button onClick={() => setIdx(i => (i + 1) % featured.length)} className="hidden md:grid w-9 h-9 place-items-center rounded-full bg-black/40 backdrop-blur border border-white/15 text-white hover:bg-black/60"><ChevronRight size={16} /></button>
      </div>
      <div className="absolute bottom-4 left-1/2 -translate-x-1/2 md:left-10 md:translate-x-0 flex gap-1.5">
        {featured.map((_, i) => (
          <button key={i} onClick={() => setIdx(i)} className={`h-1 rounded-full transition-all ${i === idx ? 'w-8 bg-white' : 'w-5 bg-white/40 hover:bg-white/70'}`} />
        ))}
      </div>
    </div>
  );
};

export default HeroCarousel;
