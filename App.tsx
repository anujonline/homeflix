import React, { useState, useEffect, useCallback, useRef, useMemo } from 'react';
import Sidebar from './components/Sidebar';
import BottomNav from './components/BottomNav';
import HeroCarousel from './components/HeroCarousel';
import MovieRow from './components/MovieRow';
import MovieCard from './components/MovieCard';
import GenreBar from './components/GenreBar';
import SearchResults from './components/SearchResults';
import VideoPlayer from './components/VideoPlayer';
import MovieDetails from './components/MovieDetails';
import AuthModal from './components/AuthModal';
import AddToPlaylistModal from './components/AddToPlaylistModal';
import BrowseView from './components/BrowseView';
import { fetchTrendingAll, fetchPopularByType, fetchTopRatedMovies, fetchTopRatedTV, fetchMovieDetails, searchContent, fetchGenres, fetchRecommendations } from './services/tmdbService';
import { Movie } from './types';
import { AnimatePresence, motion } from 'framer-motion';
import { useAuth } from './context/AuthContext';
import { Search, Bell, Sun, Moon, Sparkles, Clock3, Bookmark, History, Trash2, Play, LogIn, LogOut } from 'lucide-react';

type ViewType = 'home' | 'movies' | 'series' | 'popular' | 'browse' | 'profile';

const slugify = (s: string) => s.toLowerCase().trim().replace(/[^a-z0-9]+/g, '-').replace(/^-+|-+$/g, '').slice(0, 80) || 'untitled';

const App: React.FC = () => {
  const [playingMovie, setPlayingMovie] = useState<Movie | null>(null);
  const [selectedMovie, setSelectedMovie] = useState<Movie | null>(null);
  const [isAuthModalOpen, setIsAuthModalOpen] = useState(false);
  const [isPlaylistModalOpen, setIsPlaylistModalOpen] = useState(false);
  const [playlistMovie, setPlaylistMovie] = useState<Movie | null>(null);
  const [isDarkMode, setIsDarkMode] = useState(() => localStorage.getItem('homeflix_theme') !== 'light');
  const [currentView, setCurrentView] = useState<ViewType>('home');
  const [activeGenre, setActiveGenre] = useState<string | null>(null);
  const [movieTab, setMovieTab] = useState<'popular' | 'top'>('popular');
  const [seriesTab, setSeriesTab] = useState<'popular' | 'top'>('popular');

  const [featuredMovies, setFeaturedMovies] = useState<Movie[]>([]);
  const [trendingMovies, setTrendingMovies] = useState<Movie[]>([]);
  const [popularMovies, setPopularMovies] = useState<Movie[]>([]);
  const [popularSeries, setPopularSeries] = useState<Movie[]>([]);
  const [topRatedMovies, setTopRatedMovies] = useState<Movie[]>([]);
  const [topRatedSeries, setTopRatedSeries] = useState<Movie[]>([]);
  const [recommended, setRecommended] = useState<Movie[]>([]);
  const [recommendSeed, setRecommendSeed] = useState<Movie | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [allGenres, setAllGenres] = useState<string[]>([]);

  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState<Movie[]>([]);
  const [isSearching, setIsSearching] = useState(false);
  const searchInputRef = useRef<HTMLInputElement>(null);

  const { user, addToHistory, logout } = useAuth();
  const hasSyncedUrl = useRef(false);

  useEffect(() => {
    if (isDarkMode) {
      document.documentElement.classList.add('dark');
      localStorage.setItem('homeflix_theme', 'dark');
    } else {
      document.documentElement.classList.remove('dark');
      localStorage.setItem('homeflix_theme', 'light');
    }
  }, [isDarkMode]);

  useEffect(() => {
    const handler = (e: KeyboardEvent) => {
      if ((e.metaKey || e.ctrlKey) && e.key.toLowerCase() === 'k') {
        e.preventDefault();
        searchInputRef.current?.focus();
      }
      if (e.key === '/' && !(e.target instanceof HTMLInputElement) && !(e.target instanceof HTMLTextAreaElement)) {
        e.preventDefault();
        searchInputRef.current?.focus();
      }
    };
    window.addEventListener('keydown', handler);
    return () => window.removeEventListener('keydown', handler);
  }, []);

  // URL sync
  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const q = params.get('q');
    if (q) setSearchQuery(q);
    const path = window.location.pathname;
    if (path === '/movies') setCurrentView('movies');
    else if (path === '/series') setCurrentView('series');
    else if (path === '/popular') setCurrentView('popular');
    else if (path === '/browse') setCurrentView('browse');
    else if (path === '/profile') setCurrentView('profile');
    const watchMatch = path.match(/^\/watch\/(movie|tv)\/(\d+)/);
    const watchMatch2 = path.match(/^\/watch\/(\d+)/);
    const detailMatch = path.match(/^\/(movie|tv)\/(\d+)/);
    if (watchMatch) {
      const [, type, id] = watchMatch;
      fetchMovieDetails(Number(id), type as 'movie' | 'tv').then(setPlayingMovie).catch(() => {});
    } else if (watchMatch2) {
      const [, id] = watchMatch2;
      fetchMovieDetails(Number(id)).then(setPlayingMovie).catch(() => {});
    } else if (detailMatch && !path.startsWith('/watch')) {
      const [, type, id] = detailMatch;
      fetchMovieDetails(Number(id), type as 'movie' | 'tv').then(setSelectedMovie).catch(() => {});
    }
    hasSyncedUrl.current = true;
    const onPopState = () => {
      const p = new URLSearchParams(window.location.search);
      setSearchQuery(p.get('q') || '');
      const ph = window.location.pathname;
      if (ph === '/movies') setCurrentView('movies');
      else if (ph === '/series') setCurrentView('series');
      else if (ph === '/popular') setCurrentView('popular');
      else if (ph === '/browse') setCurrentView('browse');
      else if (ph === '/profile') setCurrentView('profile');
      else if (ph === '/') setCurrentView('home');
      if (!ph.startsWith('/watch')) setPlayingMovie(null);
      else {
        const wm = ph.match(/^\/watch\/(movie|tv)\/(\d+)/);
        const wm2 = ph.match(/^\/watch\/(\d+)/);
        if (wm) fetchMovieDetails(Number(wm[2]), wm[1] as 'movie' | 'tv').then(setPlayingMovie).catch(() => {});
        else if (wm2) fetchMovieDetails(Number(wm2[1])).then(setPlayingMovie).catch(() => {});
      }
      const dm = ph.match(/^\/(movie|tv)\/(\d+)/);
      if (!ph.startsWith('/watch')) {
        if (!dm) setSelectedMovie(null);
        else fetchMovieDetails(Number(dm[2]), dm[1] as 'movie' | 'tv').then(setSelectedMovie).catch(() => {});
      } else setSelectedMovie(null);
    };
    window.addEventListener('popstate', onPopState);
    return () => window.removeEventListener('popstate', onPopState);
  }, []);

  useEffect(() => {
    if (!hasSyncedUrl.current) return;
    const url = new URL(window.location.href);
    if (searchQuery) url.searchParams.set('q', searchQuery); else url.searchParams.delete('q');
    const newUrl = url.pathname + (url.search ? url.search : '') + url.hash;
    const current = window.location.pathname + window.location.search;
    if (newUrl !== current) window.history.replaceState({}, '', newUrl);
  }, [searchQuery]);

  const loadHomeData = useCallback(async () => {
    setIsLoading(true);
    try {
      const [trending, trMovies, popSeries, genres] = await Promise.all([
        fetchTrendingAll(),
        fetchTopRatedMovies(),
        fetchPopularByType('tv'),
        fetchGenres().catch(() => []),
      ]);
      setTrendingMovies(trending);
      setTopRatedMovies(trMovies);
      setPopularSeries(popSeries);
      setFeaturedMovies(trending.slice(0, 5));
      if (genres.length) setAllGenres(genres.map(g => g.name).slice(0, 10));
      else {
        const set = new Set<string>();
        [...trending, ...trMovies, ...popSeries].forEach(m => m.genre.forEach(g => set.add(g)));
        setAllGenres(Array.from(set).slice(0, 10));
      }
    } catch (e) {
      console.error(e);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    const fetchData = async () => {
      if (currentView === 'home') {
        if (trendingMovies.length === 0) loadHomeData();
      } else if (currentView === 'movies') {
        if (popularMovies.length === 0) {
          const movies = await fetchPopularByType('movie');
          setPopularMovies(movies);
        }
        if (topRatedMovies.length === 0) {
          const top = await fetchTopRatedMovies();
          setTopRatedMovies(top);
        }
      } else if (currentView === 'series') {
        if (popularSeries.length === 0) {
          const s = await fetchPopularByType('tv');
          setPopularSeries(s);
        }
        if (topRatedSeries.length === 0) {
          const top = await fetchTopRatedTV();
          setTopRatedSeries(top);
        }
      } else if (currentView === 'popular') {
        if (trendingMovies.length === 0) {
          const trending = await fetchTrendingAll();
          setTrendingMovies(trending);
          setFeaturedMovies(trending.slice(0, 5));
        }
      }
    };
    fetchData();
  }, [currentView, loadHomeData, trendingMovies.length, popularMovies.length, topRatedSeries.length, popularSeries.length, topRatedMovies.length]);

  // "Because you watched" — recommendations seeded from the most recent history entry
  useEffect(() => {
    if (!user || user.history.length === 0 || recommended.length > 0) return;
    const seed = user.history[0];
    setRecommendSeed(seed);
    fetchRecommendations(seed.id, seed.mediaType || 'movie')
      .then((list) => setRecommended(list.slice(0, 20)))
      .catch(() => {});
  }, [user, recommended.length]);

  useEffect(() => {
    const timer = setTimeout(async () => {
      if (searchQuery.trim()) {
        setIsSearching(true);
        try {
          const results = await searchContent(searchQuery);
          setSearchResults(results);
        } catch (e) { console.error(e); } finally { setIsSearching(false); }
      } else setSearchResults([]);
    }, 400);
    return () => clearTimeout(timer);
  }, [searchQuery]);

  const handlePlay = (movie: Movie) => {
    setPlayingMovie(movie);
    if (selectedMovie) setSelectedMovie(null);
    if (user) addToHistory(movie);
    window.history.pushState({}, '', `/watch/${movie.mediaType || 'movie'}/${movie.id}/${slugify(movie.title)}`);
  };

  const handleSelectMovie = (movie: Movie) => {
    setSelectedMovie(movie);
    window.history.pushState({}, '', `/${movie.mediaType || 'movie'}/${movie.id}/${slugify(movie.title)}`);
  };

  const handleAddToPlaylist = (movie: Movie) => {
    if (!user) { setIsAuthModalOpen(true); return; }
    setPlaylistMovie(movie);
    setIsPlaylistModalOpen(true);
  };

  const handleSignOut = () => {
    logout();
    setCurrentView('home');
    window.history.pushState({}, '', '/');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleViewChange = (view: ViewType) => {
    if (view === 'profile' && !user) { setIsAuthModalOpen(true); return; }
    setSearchQuery('');
    setActiveGenre(null);
    setCurrentView(view);
    const base = view === 'home' ? '/' : `/${view}`;
    window.history.pushState({}, '', base);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleClosePlayer = () => {
    setPlayingMovie(null);
    const base = currentView === 'home' ? '/' : `/${currentView}`;
    const q = searchQuery ? `?q=${encodeURIComponent(searchQuery)}` : '';
    window.history.pushState({}, '', `${base}${q}`);
  };

  const handleCloseDetails = () => {
    setSelectedMovie(null);
    if (window.location.pathname.match(/^\/(movie|tv)\/\d+/)) {
      const base = currentView === 'home' ? '/' : `/${currentView}`;
      const q = searchQuery ? `?q=${encodeURIComponent(searchQuery)}` : '';
      window.history.pushState({}, '', `${base}${q}`);
    } else window.history.back();
  };

  const filterByGenre = useCallback((list: Movie[]) => {
    if (!activeGenre) return list;
    return list.filter(m => m.genre.includes(activeGenre));
  }, [activeGenre]);

  const trendingFiltered = useMemo(() => filterByGenre(trendingMovies), [filterByGenre, trendingMovies]);
  const topMoviesFiltered = useMemo(() => filterByGenre(topRatedMovies), [filterByGenre, topRatedMovies]);
  const popularMoviesFiltered = useMemo(() => filterByGenre(popularMovies), [filterByGenre, popularMovies]);

  // grid data for movies/series
  const moviesGridData = movieTab === 'popular' ? popularMoviesFiltered : topMoviesFiltered;
  const seriesGridData = seriesTab === 'popular' ? filterByGenre(popularSeries) : filterByGenre(topRatedSeries);

  const renderGridSection = (title: string, movies: Movie[], emptyText?: string) => (
    <div className="max-w-[1440px] mx-auto px-4 md:px-8 py-6">
      <div className="flex items-end justify-between gap-4 mb-4">
        <h2 className="text-xl md:text-2xl font-bold tracking-tight text-slate-900 dark:text-white">{title}</h2>
        <span className="text-xs font-medium px-2.5 py-1 rounded-full bg-slate-100 dark:bg-white/10 border border-slate-200 dark:border-white/10 text-slate-600 dark:text-slate-300">{movies.length} titles</span>
      </div>
      {movies.length === 0 ? (
        <div className="py-12 text-center bg-white dark:bg-white/[0.04] rounded-2xl border border-dashed border-slate-200 dark:border-white/10 text-slate-500 text-sm">{emptyText || `No titles for ${activeGenre || 'this filter'}.`}</div>
      ) : (
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-4 md:gap-6">
          {movies.map(m => <MovieCard key={m.id} movie={m} onClick={handleSelectMovie} width="w-full" />)}
        </div>
      )}
    </div>
  );

  const renderLibrary = () => {
    if (!user) return null;
    return (
      <div className="max-w-[1440px] mx-auto px-4 md:px-8 pt-6 pb-24">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-6">
          <div className="flex items-center gap-4">
            {user.avatar ? <img src={user.avatar} alt={user.name} className="w-14 h-14 rounded-2xl object-cover border border-slate-200 dark:border-white/10" /> : <div className="w-14 h-14 rounded-2xl bg-amber-500 grid place-items-center text-white font-bold text-xl">{user.name[0]}</div>}
            <div>
              <h1 className="text-xl font-bold leading-none text-slate-900 dark:text-white">{user.name}</h1>
              <div className="text-sm text-slate-500">{user.email}</div>
              <div className="flex items-center gap-2 mt-1 text-xs">
                <span className="px-2 py-0.5 rounded-full bg-slate-900 dark:bg-white text-white dark:text-black font-bold">{user.history.length} watched</span>
                <span className="px-2 py-0.5 rounded-full bg-white dark:bg-white/10 border border-slate-200 dark:border-white/10">{user.playlists.length} lists</span>
              </div>
            </div>
          </div>
          <div className="flex gap-2 items-center">
            <div className="hidden md:flex items-center gap-2 text-xs bg-white dark:bg-white/[0.06] border border-slate-200 dark:border-white/10 rounded-full px-3 py-2"><Sparkles size={14} className="text-amber-500" /> {user.history.length + user.playlists.length} items saved</div>
            <button
              onClick={handleSignOut}
              className="inline-flex items-center gap-2 px-4 py-2 rounded-full text-sm font-semibold text-slate-600 dark:text-slate-300 bg-white dark:bg-white/[0.06] border border-slate-200 dark:border-white/10 hover:border-red-400/60 hover:text-red-500 dark:hover:text-red-400 transition-colors"
            >
              <LogOut size={15} /> Sign out
            </button>
          </div>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-3 gap-3 mb-8">
          <div className="rounded-2xl bg-white dark:bg-white/[0.04] border border-slate-200 dark:border-white/5 p-4">
            <div className="w-8 h-8 rounded-xl bg-violet-500/15 text-violet-600 grid place-items-center"><History size={16} /></div>
            <div className="text-2xl font-bold mt-3 text-slate-900 dark:text-white">{user.history.length}</div>
            <div className="text-xs text-slate-500">Watched</div>
          </div>
          <div className="rounded-2xl bg-white dark:bg-white/[0.04] border border-slate-200 dark:border-white/5 p-4">
            <div className="w-8 h-8 rounded-xl bg-amber-500/15 text-amber-600 grid place-items-center"><Bookmark size={16} /></div>
            <div className="text-2xl font-bold mt-3 text-slate-900 dark:text-white">{user.playlists.reduce((a,p)=>a+p.movies.length,0)}</div>
            <div className="text-xs text-slate-500">Saved</div>
          </div>
          <div className="rounded-2xl bg-white dark:bg-white/[0.04] border border-slate-200 dark:border-white/5 p-4">
            <div className="w-8 h-8 rounded-xl bg-emerald-500/15 text-emerald-600 grid place-items-center"><Clock3 size={16} /></div>
            <div className="text-2xl font-bold mt-3 text-slate-900 dark:text-white">{user.playlists.length}</div>
            <div className="text-xs text-slate-500">Playlists</div>
          </div>
        </div>

        {/* History */}
        <div className="bg-white dark:bg-white/[0.04] rounded-2xl border border-slate-200 dark:border-white/5 p-5 mb-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-semibold flex items-center gap-2"><History size={16} className="text-violet-500" /> Watch History</h3>
            {user.history.length > 0 && <button onClick={() => window.confirm('Clear history?') && (useAuth as any).clearHistory?.()} className="text-xs text-slate-500 hover:text-red-500 flex items-center gap-1"><Trash2 size={12} /> Clear</button>}
          </div>
          {user.history.length === 0 ? (
            <div className="py-10 text-center text-sm text-slate-500">No history yet — play something to see it here.</div>
          ) : (
            <div className="flex gap-3 overflow-x-auto no-scrollbar pb-2">
              {user.history.map(m => (
                <div key={m.id} className="min-w-[148px] cursor-pointer group" onClick={() => handleSelectMovie(m)}>
                  <div className="aspect-[2/3] rounded-xl overflow-hidden bg-slate-100 dark:bg-[#0f172a] relative">
                    <img src={m.imageUrl} className="w-full h-full object-cover group-hover:scale-105 transition duration-500" />
                    <span className="absolute bottom-1 left-1 right-1 h-1 bg-white/30 rounded-full overflow-hidden"><span className="block h-full w-[68%] bg-amber-400" /></span>
                  </div>
                  <div className="text-xs font-medium mt-1.5 line-clamp-1 text-slate-900 dark:text-white">{m.title}</div>
                  <div className="text-[11px] text-slate-500">{m.year} • {m.genre[0] || m.mediaType}</div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Playlists */}
        <div className="space-y-4">
          <h3 className="font-semibold flex items-center gap-2"><Bookmark size={16} className="text-amber-500" /> Playlists</h3>
          {user.playlists.length === 0 ? (
            <div className="py-12 text-center bg-white dark:bg-white/[0.04] rounded-2xl border border-dashed border-slate-200 dark:border-white/10">
              <div className="w-12 h-12 rounded-full bg-slate-100 dark:bg-white/5 grid place-items-center mx-auto mb-3">📁</div>
              <div className="font-medium text-slate-900 dark:text-white">No playlists yet</div>
              <div className="text-sm text-slate-500">Add movies via the details screen.</div>
            </div>
          ) : (
            <div className="grid md:grid-cols-2 gap-4">
              {user.playlists.map(pl => (
                <div key={pl.id} className="rounded-2xl bg-white dark:bg-white/[0.04] border border-slate-200 dark:border-white/5 p-4">
                  <div className="flex items-center justify-between">
                    <div className="font-semibold text-slate-900 dark:text-white">{pl.name}</div>
                    <span className="text-xs px-2 py-1 rounded-full bg-slate-100 dark:bg-white/10">{pl.movies.length}</span>
                  </div>
                  {pl.movies.length === 0 ? (
                    <div className="text-sm text-slate-500 mt-3">Empty — add titles to see them here.</div>
                  ) : (
                    <div className="flex gap-2 overflow-x-auto no-scrollbar mt-3">
                      {pl.movies.map(m => (
                        <img key={m.id} src={m.imageUrl} onClick={() => handleSelectMovie(m)} className="w-16 h-24 object-cover rounded-xl cursor-pointer hover:opacity-90" />
                      ))}
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    );
  };

  const renderContent = () => {
    if (searchQuery) {
      return <SearchResults query={searchQuery} results={searchResults} onSelectMovie={handleSelectMovie} isLoading={isSearching} />;
    }
    if (currentView === 'profile') return renderLibrary();
    if (currentView === 'movies') {
      return (
        <div className="pb-24">
          <div className="max-w-[1440px] mx-auto px-4 md:px-8 pt-4">
            <div className="flex flex-wrap items-center justify-between gap-3">
              <h1 className="text-2xl font-bold tracking-tight text-slate-900 dark:text-white">Movies</h1>
              <div className="flex items-center gap-1 p-1 rounded-full bg-slate-100 dark:bg-white/[0.06] border border-slate-200 dark:border-white/5">
                <button onClick={() => setMovieTab('popular')} className={`px-4 py-1.5 rounded-full text-sm font-medium ${movieTab === 'popular' ? 'bg-slate-900 dark:bg-white text-white dark:text-black' : 'text-slate-600 dark:text-slate-400'}`}>Popular</button>
                <button onClick={() => setMovieTab('top')} className={`px-4 py-1.5 rounded-full text-sm font-medium ${movieTab === 'top' ? 'bg-slate-900 dark:bg-white text-white dark:text-black' : 'text-slate-600 dark:text-slate-400'}`}>Top Rated</button>
              </div>
            </div>
            <div className="mt-3"><GenreBar genres={allGenres} active={activeGenre} onSelect={setActiveGenre} /></div>
          </div>
          {renderGridSection(movieTab === 'popular' ? 'Popular Movies' : 'Top Rated Movies', moviesGridData)}
        </div>
      );
    }
    if (currentView === 'series') {
      return (
        <div className="pb-24">
          <div className="max-w-[1440px] mx-auto px-4 md:px-8 pt-4">
            <div className="flex flex-wrap items-center justify-between gap-3">
              <h1 className="text-2xl font-bold tracking-tight text-slate-900 dark:text-white">Series</h1>
              <div className="flex items-center gap-1 p-1 rounded-full bg-slate-100 dark:bg-white/[0.06] border border-slate-200 dark:border-white/5">
                <button onClick={() => setSeriesTab('popular')} className={`px-4 py-1.5 rounded-full text-sm font-medium ${seriesTab === 'popular' ? 'bg-slate-900 dark:bg-white text-white dark:text-black' : 'text-slate-600 dark:text-slate-400'}`}>Popular</button>
                <button onClick={() => setSeriesTab('top')} className={`px-4 py-1.5 rounded-full text-sm font-medium ${seriesTab === 'top' ? 'bg-slate-900 dark:bg-white text-white dark:text-black' : 'text-slate-600 dark:text-slate-400'}`}>Top Rated</button>
              </div>
            </div>
            <div className="mt-3"><GenreBar genres={allGenres} active={activeGenre} onSelect={setActiveGenre} /></div>
          </div>
          {renderGridSection(seriesTab === 'popular' ? 'Popular Series' : 'Top Rated Series', seriesGridData)}
        </div>
      );
    }
    if (currentView === 'browse') {
      return <BrowseView onSelectMovie={handleSelectMovie} />;
    }
    if (currentView === 'popular') {
      return (
        <div className="pb-24">
          <HeroCarousel movies={trendingMovies} onPlay={handlePlay} onMore={handleSelectMovie} />
          <div className="max-w-[1440px] mx-auto px-4 md:px-8 pt-4"><GenreBar genres={allGenres} active={activeGenre} onSelect={setActiveGenre} /></div>
          {renderGridSection('Trending This Week', trendingFiltered)}
        </div>
      );
    }
    // home
    return (
      <div className="pb-24">
        <HeroCarousel movies={featuredMovies.length ? featuredMovies : trendingMovies} onPlay={handlePlay} onMore={handleSelectMovie} />
        <div className="max-w-[1440px] mx-auto px-4 md:px-8 pt-4">
          <GenreBar genres={allGenres} active={activeGenre} onSelect={setActiveGenre} />
        </div>

        {/* quick stats */}
        {user && (
          <div className="max-w-[1440px] mx-auto px-4 md:px-8 mt-2 grid grid-cols-3 gap-3">
            <div className="rounded-2xl bg-white dark:bg-white/[0.04] border border-slate-200 dark:border-white/5 p-3 flex items-center gap-3">
              <span className="w-9 h-9 rounded-xl bg-violet-500/15 grid place-items-center text-violet-600"><Play size={16} /></span>
              <div><div className="text-sm font-bold leading-none text-slate-900 dark:text-white">{user.history.length}</div><div className="text-xs text-slate-500">Watched</div></div>
            </div>
            <div className="rounded-2xl bg-white dark:bg-white/[0.04] border border-slate-200 dark:border-white/5 p-3 flex items-center gap-3">
              <span className="w-9 h-9 rounded-xl bg-amber-500/15 grid place-items-center text-amber-600"><Bookmark size={16} /></span>
              <div><div className="text-sm font-bold leading-none text-slate-900 dark:text-white">{user.playlists.length}</div><div className="text-xs text-slate-500">Playlists</div></div>
            </div>
            <div className="rounded-2xl bg-white dark:bg-white/[0.04] border border-slate-200 dark:border-white/5 p-3 flex items-center gap-3">
              <span className="w-9 h-9 rounded-xl bg-emerald-500/15 grid place-items-center text-emerald-600"><History size={16} /></span>
              <div><div className="text-sm font-bold leading-none text-slate-900 dark:text-white">{trendingMovies.length}</div><div className="text-xs text-slate-500">Trending</div></div>
            </div>
          </div>
        )}

        {user && user.history.length > 0 && <MovieRow title="Continue Watching" movies={filterByGenre(user.history)} onSelectMovie={handleSelectMovie} onSeeAll={() => handleViewChange('profile')} />}
        {recommendSeed && recommended.length > 0 && <MovieRow title={`Because you watched ${recommendSeed.title}`} movies={recommended} onSelectMovie={handleSelectMovie} />}
        <MovieRow title="Trending Now" movies={trendingFiltered.slice(0, 20)} onSelectMovie={handleSelectMovie} onSeeAll={() => handleViewChange('popular')} />
        <MovieRow title="Top Rated Movies" movies={topMoviesFiltered.slice(0, 20)} onSelectMovie={handleSelectMovie} onSeeAll={() => { setMovieTab('top'); handleViewChange('movies'); }} />
        <MovieRow title="Popular Series" movies={filterByGenre(popularSeries).slice(0, 20)} onSelectMovie={handleSelectMovie} onSeeAll={() => { setSeriesTab('popular'); handleViewChange('series'); }} />

        {activeGenre && (
          <div className="max-w-[1440px] mx-auto px-4 md:px-8 py-6">
            <div className="rounded-2xl bg-slate-900 dark:bg-white text-white dark:text-black p-6 flex items-center justify-between">
              <div>
                <div className="text-xs tracking-widest font-bold opacity-60">FILTERED BY</div>
                <div className="text-xl font-bold">{activeGenre}</div>
                <div className="text-sm opacity-70">{trendingFiltered.length + topMoviesFiltered.length} titles match</div>
              </div>
              <button onClick={() => setActiveGenre(null)} className="bg-white dark:bg-black text-black dark:text-white px-4 py-2 rounded-full text-sm font-bold">Clear filter</button>
            </div>
          </div>
        )}
      </div>
    );
  };

  return (
    <div className="min-h-screen bg-[#f8fafc] dark:bg-[#020617] text-slate-900 dark:text-slate-50 flex">
      <Sidebar activeView={currentView} onViewChange={handleViewChange} />
      <div className="flex-1 min-w-0 flex flex-col">
        {/* Top bar */}
        <header className="sticky top-0 z-30 bg-white/85 dark:bg-[#020617]/85 backdrop-blur-xl border-b border-slate-200 dark:border-white/[0.06]">
          <div className="flex items-center gap-3 px-4 md:px-8 py-3">
            <div className="flex items-center gap-3 lg:hidden">
              <div className="w-8 h-8 rounded-xl bg-gradient-to-br from-amber-400 to-amber-600 grid place-items-center text-black font-black text-sm">H</div>
              <span className="font-display font-bold tracking-tight hidden sm:block">HOMEFLIX</span>
            </div>
            <div className="flex-1 flex justify-center">
              <div className="w-full max-w-[640px] relative">
                <Search size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
                <input
                  ref={searchInputRef}
                  value={searchQuery}
                  onChange={e => setSearchQuery(e.target.value)}
                  placeholder="Search movies, series, people..."
                  className="w-full bg-slate-100 dark:bg-white/[0.06] border border-slate-200 dark:border-white/10 rounded-full py-2.5 pl-10 pr-10 text-sm outline-none focus:border-amber-500 focus:ring-2 focus:ring-amber-500/20 text-slate-900 dark:text-white placeholder:text-slate-400"
                />
                {searchQuery && (
                  <button onClick={() => setSearchQuery('')} className="absolute right-3 top-1/2 -translate-y-1/2 w-6 h-6 grid place-items-center rounded-full bg-slate-200 dark:bg-white/10">
                    <span className="text-xs">×</span>
                  </button>
                )}
              </div>
            </div>
            <div className="flex items-center gap-1.5">
              <button onClick={() => setIsDarkMode(!isDarkMode)} className="w-9 h-9 grid place-items-center rounded-full bg-slate-100 dark:bg-white/[0.06] border border-slate-200 dark:border-white/10 text-slate-600 dark:text-slate-300">
                {isDarkMode ? <Sun size={16} /> : <Moon size={16} />}
              </button>
              <button className="hidden md:grid w-9 h-9 place-items-center rounded-full bg-slate-100 dark:bg-white/[0.06] border border-slate-200 dark:border-white/10 text-slate-600 dark:text-slate-400">
                <Bell size={16} />
              </button>
              {user ? (
                <button onClick={() => handleViewChange('profile')} className={`hidden md:flex items-center gap-2 pl-1 pr-3 py-1 rounded-full ${currentView === 'profile' ? 'bg-slate-900 dark:bg-white text-white dark:text-black' : 'bg-slate-100 dark:bg-white/[0.06] border border-slate-200 dark:border-white/10'}`}>
                  {user.avatar ? <img src={user.avatar} className="w-7 h-7 rounded-full" /> : <div className="w-7 h-7 rounded-full bg-amber-500 grid place-items-center text-white text-xs font-bold">{user.name[0]}</div>}
                  <span className="text-sm font-medium max-w-[100px] truncate">{user.name.split(' ')[0]}</span>
                </button>
              ) : (
                <button onClick={() => setIsAuthModalOpen(true)} className="group/signin hidden md:inline-flex relative overflow-hidden items-center gap-2 pl-1.5 pr-4 py-1.5 rounded-full text-sm font-bold text-white bg-slate-950/95 dark:bg-black/80 ring-1 ring-amber-400/70 hover:ring-amber-300 shadow-md shadow-amber-500/25 hover:shadow-amber-400/40 transition-all active:scale-95">
                  <span className="pointer-events-none absolute inset-y-0 left-0 w-1/3 bg-gradient-to-r from-transparent via-amber-200/40 to-transparent opacity-0 group-hover/signin:opacity-100 group-hover/signin:animate-shine" />
                  <span className="relative w-6 h-6 grid place-items-center rounded-full bg-gradient-to-br from-amber-300 to-amber-500 text-black"><LogIn size={13} /></span>
                  <span className="relative">Sign in</span>
                </button>
              )}
            </div>
          </div>
        </header>

        <main className="flex-1 min-h-[60vh]">
          <AnimatePresence mode="wait">{renderContent()}</AnimatePresence>
        </main>

        <footer className="hidden lg:block border-t border-slate-200 dark:border-white/5 bg-white dark:bg-[#070b18] py-6 text-center text-xs text-slate-500">
          <div className="flex justify-center gap-6">
            <span>Audio Description</span><span>Help Center</span><span>Privacy</span><span>Terms</span>
          </div>
          <div className="opacity-60 mt-2">© 2024 Homeflix • {trendingMovies.length + popularMovies.length + popularSeries.length} titles indexed • Caffeine 5m cache</div>
        </footer>
      </div>

      <BottomNav activeView={currentView} onViewChange={handleViewChange} />

      <AnimatePresence>{playingMovie && <VideoPlayer key="player" movie={playingMovie} onClose={handleClosePlayer} />}</AnimatePresence>
      <AnimatePresence>{selectedMovie && <MovieDetails key={`details-${selectedMovie.id}`} movie={selectedMovie} onClose={handleCloseDetails} onPlay={() => handlePlay(selectedMovie)} onAddToPlaylist={() => handleAddToPlaylist(selectedMovie)} onSelectMovie={handleSelectMovie} />}</AnimatePresence>
      <AnimatePresence>{isAuthModalOpen && <AuthModal key="auth" isOpen={isAuthModalOpen} onClose={() => setIsAuthModalOpen(false)} />}</AnimatePresence>
      <AnimatePresence>{isPlaylistModalOpen && <AddToPlaylistModal key="playlist" isOpen={isPlaylistModalOpen} onClose={() => { setIsPlaylistModalOpen(false); setPlaylistMovie(null); }} movie={playlistMovie} />}</AnimatePresence>
    </div>
  );
};

export default App;
