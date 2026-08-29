import React, { useEffect, useState } from 'react';
import { X, Play, Plus, ThumbsUp, Share2, Check, Star, Clock, PlayCircle } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { Movie, Video, CastMember, WatchProviderResult, SeasonSummary, Episode } from '../types';
import { fetchMovieDetails, fetchVideos, fetchCredits, fetchWatchProviders, fetchSimilar, fetchRecommendations, fetchTvSeasons, fetchSeasonEpisodes, getWatchRegion } from '../services/tmdbService';
import MovieCard from './MovieCard';

interface MovieDetailsProps {
  movie: Movie;
  onClose: () => void;
  onPlay: () => void;
  onAddToPlaylist: () => void;
  onSelectMovie?: (movie: Movie) => void;
}

const formatAirDate = (d: string | null) => {
  if (!d) return '';
  try { return new Date(d + 'T00:00:00').toLocaleDateString(undefined, { month: 'short', day: 'numeric', year: 'numeric' }); } catch { return d; }
};

// keep in sync with the deep-link format App.tsx pushes (/:type/:id/:slug)
const slugify = (s: string) => s.toLowerCase().trim().replace(/[^a-z0-9]+/g, '-').replace(/^-+|-+$/g, '').slice(0, 80) || 'untitled';

const glassBtn = 'bg-white/70 dark:bg-white/10 backdrop-blur border border-slate-200/80 dark:border-white/15 hover:bg-white dark:hover:bg-white/15 hover:border-amber-400/60 text-slate-900 dark:text-white shadow-sm';

const MovieDetails: React.FC<MovieDetailsProps> = ({ movie: initialMovie, onClose, onPlay, onAddToPlaylist, onSelectMovie }) => {
  const [movie, setMovie] = useState<Movie>(initialMovie);

  const [videos, setVideos] = useState<Video[]>([]);
  const [credits, setCredits] = useState<{ cast: CastMember[]; directors: string[] } | null>(null);
  const [providers, setProviders] = useState<WatchProviderResult | null>(null);
  const [similar, setSimilar] = useState<Movie[]>([]);

  const [seasons, setSeasons] = useState<SeasonSummary[]>([]);
  const [selectedSeason, setSelectedSeason] = useState<number | null>(null);
  const [episodes, setEpisodes] = useState<Episode[]>([]);
  const [episodesLoading, setEpisodesLoading] = useState(false);

  const [showTrailer, setShowTrailer] = useState(false);
  const [copied, setCopied] = useState(false);

  const movieId = initialMovie.id;
  const mediaType = initialMovie.mediaType || 'movie';

  // native share sheet on mobile, clipboard fallback everywhere else
  const handleShare = async () => {
    const url = `${window.location.origin}/${mediaType}/${movie.id}/${slugify(movie.title)}`;
    if (navigator.share) {
      try { await navigator.share({ title: movie.title, url }); return; }
      catch (e: any) { if (e?.name === 'AbortError') return; }
    }
    let ok = false;
    try { await navigator.clipboard.writeText(url); ok = true; } catch { /* insecure context etc. */ }
    if (!ok) {
      const ta = document.createElement('textarea');
      ta.value = url;
      ta.style.position = 'fixed';
      ta.style.opacity = '0';
      document.body.appendChild(ta);
      ta.select();
      try { ok = document.execCommand('copy'); } catch { /* no clipboard at all */ }
      document.body.removeChild(ta);
    }
    if (ok) {
      setCopied(true);
      setTimeout(() => setCopied(false), 1800);
    }
  };

  useEffect(() => {
    if (initialMovie.duration) {
      setMovie(initialMovie);
      return;
    }
    let mounted = true;
    fetchMovieDetails(initialMovie.id, mediaType).then(d => mounted && setMovie(d)).catch(() => {});
    return () => { mounted = false; };
  }, [initialMovie, mediaType]);

  // enrichment — videos, credits, watch providers, similar (recommendations as fallback)
  useEffect(() => {
    let mounted = true;
    setVideos([]); setCredits(null); setProviders(null); setSimilar([]); setShowTrailer(false);
    const t = mediaType as 'movie' | 'tv';
    Promise.allSettled([
      fetchVideos(movieId, t).then(v => mounted && setVideos(v)),
      fetchCredits(movieId, t).then(c => mounted && setCredits(c)),
      fetchWatchProviders(movieId, t, getWatchRegion()).then(p => mounted && setProviders(p)),
      fetchSimilar(movieId, t)
        .then(async s => {
          if (!mounted) return;
          if (s.length) setSimilar(s);
          else setSimilar(await fetchRecommendations(movieId, t).catch(() => []));
        }),
    ]);
    return () => { mounted = false; };
  }, [movieId, mediaType]);

  // TV seasons list
  useEffect(() => {
    if (mediaType !== 'tv') { setSeasons([]); setSelectedSeason(null); setEpisodes([]); return; }
    let mounted = true;
    fetchTvSeasons(movieId)
      .then(s => {
        if (!mounted) return;
        setSeasons(s);
        setSelectedSeason(s.length ? s[0].seasonNumber : null);
      })
      .catch(() => {});
    return () => { mounted = false; };
  }, [movieId, mediaType]);

  // episodes for the selected season
  useEffect(() => {
    if (mediaType !== 'tv' || selectedSeason == null) { setEpisodes([]); return; }
    let mounted = true;
    setEpisodesLoading(true);
    fetchSeasonEpisodes(movieId, selectedSeason)
      .then(s => mounted && setEpisodes(s.episodes))
      .catch(() => {})
      .finally(() => mounted && setEpisodesLoading(false));
    return () => { mounted = false; };
  }, [movieId, selectedSeason, mediaType]);

  const trailer = videos.find(v => v.type === 'Trailer') || videos.find(v => v.type === 'Teaser') || videos[0];
  const providerGroups = providers
    ? [
        { label: 'Stream', list: providers.flatrate },
        { label: 'Rent', list: providers.rent },
        { label: 'Buy', list: providers.buy },
      ].filter(g => g.list.length)
    : [];

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

        {/* hero — trailer embeds here on desktop; on mobile it plays in the content area instead */}
        <div className="relative h-[42vh] md:h-[360px] shrink-0">
          <img src={movie.backdropUrl} alt={movie.title} className={`w-full h-full object-cover ${showTrailer && trailer ? 'md:hidden' : ''}`} />
          {showTrailer && trailer && (
            <iframe
              key={trailer.key}
              src={`https://www.youtube-nocookie.com/embed/${trailer.key}?autoplay=1&rel=0`}
              title={trailer.name}
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
              allowFullScreen
              className="absolute inset-0 w-full h-full hidden md:block"
            />
          )}
          <div className="absolute inset-0 bg-gradient-to-t from-white dark:from-[#0f172a] via-white/10 dark:via-[#0f172a]/40 to-transparent pointer-events-none" />
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
            <div className="flex gap-2.5 mb-6">
              {/* Play — dark glass with amber accent; stays high-contrast against any poster */}
              <motion.button
                onClick={onPlay}
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.96 }}
                className="group/play relative flex-1 md:flex-none inline-flex items-center justify-center gap-2.5 overflow-hidden pl-2 pr-8 py-2 rounded-full font-bold text-white bg-slate-950/95 dark:bg-black/80 backdrop-blur ring-1 ring-amber-400/70 hover:ring-amber-300 shadow-lg shadow-amber-500/25 hover:shadow-amber-400/45 transition-all duration-300"
              >
                <span className="pointer-events-none absolute inset-y-0 left-0 w-1/3 bg-gradient-to-r from-transparent via-amber-200/40 to-transparent opacity-0 group-hover/play:opacity-100 group-hover/play:animate-shine" />
                <span className="relative w-8 h-8 grid place-items-center rounded-full bg-gradient-to-br from-amber-300 to-amber-500 text-black shadow-inner">
                  <Play size={15} fill="currentColor" className="ml-0.5" />
                </span>
                <span className="relative">Play</span>
              </motion.button>
              {trailer && (
                <motion.button
                  onClick={() => setShowTrailer(v => !v)}
                  whileTap={{ scale: 0.96 }}
                  className={`flex-1 md:flex-none inline-flex items-center justify-center gap-2 px-6 py-3 rounded-full font-bold ${showTrailer ? 'bg-slate-900 dark:bg-white text-white dark:text-black border border-slate-900 dark:border-white' : glassBtn}`}
                >
                  <PlayCircle size={18} /> {showTrailer ? 'Hide Trailer' : 'Trailer'}
                </motion.button>
              )}
              <motion.button whileTap={{ scale: 0.9 }} onClick={onAddToPlaylist} title="Add to playlist" className={`w-11 h-11 grid place-items-center rounded-full ${glassBtn}`}>
                <Plus size={18} />
              </motion.button>
              <motion.button whileTap={{ scale: 0.9 }} className={`w-11 h-11 grid place-items-center rounded-full ${glassBtn} hidden md:grid`} title="Like">
                <ThumbsUp size={16} />
              </motion.button>
              <motion.button
                whileTap={{ scale: 0.9 }}
                onClick={handleShare}
                title={copied ? 'Link copied!' : 'Copy shareable link'}
                className={`w-11 h-11 grid place-items-center rounded-full ${glassBtn} ${copied ? 'border-emerald-400/70 dark:border-emerald-400/50' : ''}`}
              >
                <AnimatePresence mode="wait" initial={false}>
                  <motion.span
                    key={copied ? 'check' : 'share'}
                    initial={{ scale: 0.4, opacity: 0 }}
                    animate={{ scale: 1, opacity: 1 }}
                    exit={{ scale: 0.4, opacity: 0 }}
                    transition={{ duration: 0.15 }}
                    className="grid place-items-center"
                  >
                    {copied ? <Check size={16} className="text-emerald-500" /> : <Share2 size={16} />}
                  </motion.span>
                </AnimatePresence>
              </motion.button>
            </div>

            {showTrailer && trailer && (
              <div className="md:hidden aspect-video rounded-2xl overflow-hidden bg-black mb-6">
                <iframe
                  key={`m-${trailer.key}`}
                  src={`https://www.youtube-nocookie.com/embed/${trailer.key}?autoplay=1&rel=0`}
                  title={trailer.name}
                  allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                  allowFullScreen
                  className="w-full h-full"
                />
              </div>
            )}

            <p className="text-[15px] leading-relaxed text-slate-700 dark:text-slate-300">{movie.description}</p>

            {/* watch providers */}
            {providerGroups.length > 0 && (
              <div className="mt-6 border-t border-slate-100 dark:border-white/5 pt-5">
                <div className="text-xs font-bold tracking-widest text-slate-400 mb-2">AVAILABLE ON ({getWatchRegion()})</div>
                <div className="flex flex-wrap items-center gap-3">
                  {providerGroups.map(g => (
                    <div key={g.label} className="flex items-center gap-1.5">
                      <span className="text-[10px] font-bold uppercase tracking-wider text-slate-500">{g.label}</span>
                      {g.list.slice(0, 5).map(p => (
                        <a key={`${g.label}-${p.id}`} href={providers!.link} target="_blank" rel="noreferrer" title={`${g.label} on ${p.name}`} className="group">
                          <img src={p.logoUrl} alt={p.name} className="w-8 h-8 rounded-lg border border-slate-200 dark:border-white/10 group-hover:ring-2 ring-amber-400 transition" />
                        </a>
                      ))}
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* top cast */}
            {credits && credits.cast.length > 0 && (
              <div className="mt-6 border-t border-slate-100 dark:border-white/5 pt-5">
                <div className="text-xs font-bold tracking-widest text-slate-400 mb-3">TOP BILLED CAST</div>
                <div className="flex gap-3 overflow-x-auto no-scrollbar pb-2">
                  {credits.cast.map(c => (
                    <div key={c.id} className="w-[76px] shrink-0">
                      {c.profileUrl
                        ? <img src={c.profileUrl} alt={c.name} loading="lazy" className="w-[76px] h-[76px] rounded-full object-cover border border-slate-200 dark:border-white/10" />
                        : <div className="w-[76px] h-[76px] rounded-full bg-slate-100 dark:bg-white/10 grid place-items-center text-xl font-bold text-slate-400 border border-slate-200 dark:border-white/10">{c.name[0]}</div>}
                      <div className="text-[11px] font-semibold mt-1.5 line-clamp-2 leading-tight text-slate-900 dark:text-white">{c.name}</div>
                      {c.character && <div className="text-[10px] text-slate-500 line-clamp-2 leading-tight">{c.character}</div>}
                    </div>
                  ))}
                </div>
              </div>
            )}

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
                <div className="text-slate-600 dark:text-slate-300 capitalize text-sm">{mediaType === 'tv' ? 'TV Series' : 'Movie'} • {movie.year}</div>
                {credits && credits.directors.length > 0 && (
                  <div className="text-xs text-slate-500 mt-1">{mediaType === 'tv' ? 'Created' : 'Directed'} by {credits.directors.slice(0, 2).join(', ')}</div>
                )}
                {mediaType === 'tv' && seasons.length > 0 && (
                  <div className="text-xs text-slate-500 mt-1">{seasons.length} season{seasons.length !== 1 ? 's' : ''} • {seasons.reduce((a, s) => a + s.episodeCount, 0)} episodes</div>
                )}
                <div className="text-xs text-slate-500 mt-1">Available in 4K • 5.1 Audio</div>
              </div>
            </div>

            {/* episodes */}
            {mediaType === 'tv' && seasons.length > 0 && (
              <div className="mt-6 border-t border-slate-100 dark:border-white/5 pt-5">
                <div className="flex items-center justify-between gap-3 mb-3">
                  <div className="text-xs font-bold tracking-widest text-slate-400">EPISODES</div>
                  <div className="flex gap-1.5 overflow-x-auto no-scrollbar max-w-[70%]">
                    {seasons.map(s => (
                      <button
                        key={s.seasonNumber}
                        onClick={() => setSelectedSeason(s.seasonNumber)}
                        className={`px-3 py-1 rounded-full text-xs font-semibold whitespace-nowrap border transition ${selectedSeason === s.seasonNumber ? 'bg-amber-500 text-black border-amber-500' : 'bg-slate-100 dark:bg-white/[0.06] border-slate-200 dark:border-white/10 text-slate-600 dark:text-slate-300 hover:bg-slate-200 dark:hover:bg-white/10'}`}
                        title={`${s.name} • ${s.episodeCount} episodes`}
                      >
                        S{s.seasonNumber}
                      </button>
                    ))}
                  </div>
                </div>
                {episodesLoading ? (
                  <div className="space-y-2">
                    {[...Array(4)].map((_, i) => <div key={i} className="h-16 rounded-xl bg-slate-100 dark:bg-white/5 animate-pulse" />)}
                  </div>
                ) : episodes.length === 0 ? (
                  <div className="text-sm text-slate-500 py-4 text-center">No episode data for this season.</div>
                ) : (
                  <div className="space-y-2 max-h-[320px] overflow-y-auto pr-1">
                    {episodes.map(ep => (
                      <div key={ep.id} className="flex gap-3 p-2 rounded-xl hover:bg-slate-50 dark:hover:bg-white/[0.04] transition">
                        <div className="w-28 h-16 rounded-lg overflow-hidden bg-slate-200 dark:bg-white/10 shrink-0">
                          {ep.stillUrl
                            ? <img src={ep.stillUrl} alt={ep.name} loading="lazy" className="w-full h-full object-cover" />
                            : <div className="w-full h-full grid place-items-center text-[10px] text-slate-400">E{ep.episodeNumber}</div>}
                        </div>
                        <div className="min-w-0 flex-1">
                          <div className="flex items-center gap-2">
                            <span className="text-xs font-bold text-slate-400">E{ep.episodeNumber}</span>
                            <span className="text-sm font-semibold line-clamp-1 text-slate-900 dark:text-white">{ep.name}</span>
                            {ep.rating > 0 && <span className="ml-auto inline-flex items-center gap-0.5 text-[11px] text-slate-500 shrink-0"><Star size={10} className="fill-amber-400 text-amber-400" />{ep.rating}</span>}
                          </div>
                          <div className="text-[11px] text-slate-500 mt-0.5">{formatAirDate(ep.airDate)}{ep.runtime ? ` • ${ep.runtime}m` : ''}</div>
                          {ep.overview && <p className="text-xs text-slate-500 dark:text-slate-400 line-clamp-2 mt-1 leading-snug">{ep.overview}</p>}
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            )}

            {/* similar titles */}
            {similar.length > 0 && (
              <div className="mt-6 border-t border-slate-100 dark:border-white/5 pt-5">
                <div className="text-xs font-bold tracking-widest text-slate-400 mb-3">MORE LIKE THIS</div>
                <div className="flex gap-3 overflow-x-auto no-scrollbar pb-2">
                  {similar.slice(0, 14).map((m, i) => (
                    <MovieCard
                      key={`${m.id}-${i}`}
                      movie={m}
                      onClick={(sel) => onSelectMovie ? onSelectMovie(sel) : undefined}
                      width="w-[110px] md:w-[130px]"
                    />
                  ))}
                </div>
              </div>
            )}
          </div>
        </div>

        {/* drag handle mobile */}
        <div className="md:hidden absolute top-2 left-1/2 -translate-x-1/2 w-10 h-1 rounded-full bg-white/80" />
      </motion.div>
    </div>
  );
};

export default MovieDetails;
