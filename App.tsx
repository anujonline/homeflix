import React, { useState, useEffect, useCallback, useRef } from 'react';
import Navbar from './components/Navbar';
import Hero from './components/Hero';
import MovieRow from './components/MovieRow';
import MovieCard from './components/MovieCard';
import SearchResults from './components/SearchResults';
import VideoPlayer from './components/VideoPlayer';
import MovieDetails from './components/MovieDetails';
import AuthModal from './components/AuthModal';
import AddToPlaylistModal from './components/AddToPlaylistModal';
import ProfileView from './components/ProfileView';
import { 
  fetchTrendingAll, 
  fetchPopularByType,
  fetchTopRatedMovies, 
  fetchTopRatedTV,
  fetchMovieDetails, 
  searchContent 
} from './services/tmdbService';
import { Movie } from './types';
import { AnimatePresence, motion } from 'framer-motion';
import { useAuth } from './context/AuthContext';

type ViewType = 'home' | 'movies' | 'series' | 'popular' | 'profile';

const slugify = (s: string) => s.toLowerCase().trim().replace(/[^a-z0-9]+/g, '-').replace(/^-+|-+$/g, '').slice(0, 80) || 'untitled';

const App: React.FC = () => {
  const [playingMovie, setPlayingMovie] = useState<Movie | null>(null);
  const [selectedMovie, setSelectedMovie] = useState<Movie | null>(null);
  const [isAuthModalOpen, setIsAuthModalOpen] = useState(false);
  const [isPlaylistModalOpen, setIsPlaylistModalOpen] = useState(false);
  const [playlistMovie, setPlaylistMovie] = useState<Movie | null>(null);
  const [isDarkMode, setIsDarkMode] = useState(() => localStorage.getItem('homeflix_theme') !== 'light');
  
  const [currentView, setCurrentView] = useState<ViewType>('home');

  const [featuredMovie, setFeaturedMovie] = useState<Movie | null>(null);
  const [trendingMovies, setTrendingMovies] = useState<Movie[]>([]);
  const [popularMovies, setPopularMovies] = useState<Movie[]>([]);
  const [popularSeries, setPopularSeries] = useState<Movie[]>([]);
  const [topRatedMovies, setTopRatedMovies] = useState<Movie[]>([]);
  const [topRatedSeries, setTopRatedSeries] = useState<Movie[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState<Movie[]>([]);
  const [isSearching, setIsSearching] = useState(false);

  const { user, addToHistory } = useAuth();
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

  // URL → state on load + popstate (deep linking for play/search/view)
  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const q = params.get('q');
    if (q) setSearchQuery(q);

    const path = window.location.pathname;
    if (path === '/movies') setCurrentView('movies');
    else if (path === '/series') setCurrentView('series');
    else if (path === '/popular') setCurrentView('popular');
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
      else if (ph === '/profile') setCurrentView('profile');
      else if (ph === '/') setCurrentView('home');

      if (!ph.startsWith('/watch')) {
        setPlayingMovie(null);
      } else {
        const wm = ph.match(/^\/watch\/(movie|tv)\/(\d+)/);
        const wm2 = ph.match(/^\/watch\/(\d+)/);
        if (wm) {
          const [, t, id] = wm;
          fetchMovieDetails(Number(id), t as 'movie' | 'tv').then(setPlayingMovie).catch(() => {});
        } else if (wm2) {
          fetchMovieDetails(Number(wm2[1])).then(setPlayingMovie).catch(() => {});
        }
      }

      const dm = ph.match(/^\/(movie|tv)\/(\d+)/);
      if (!ph.startsWith('/watch')) {
        if (!dm) setSelectedMovie(null);
        else {
          const [, t, id] = dm;
          fetchMovieDetails(Number(id), t as 'movie' | 'tv').then(setSelectedMovie).catch(() => {});
        }
      } else {
        setSelectedMovie(null);
      }
    };
    window.addEventListener('popstate', onPopState);
    return () => window.removeEventListener('popstate', onPopState);
  }, []);

  // search query → URL (?q=) via replaceState (no history flood while typing)
  useEffect(() => {
    if (!hasSyncedUrl.current) return;
    const url = new URL(window.location.href);
    if (searchQuery) url.searchParams.set('q', searchQuery);
    else url.searchParams.delete('q');
    const newUrl = url.pathname + (url.search ? url.search : '') + url.hash;
    const current = window.location.pathname + window.location.search;
    if (newUrl !== current) window.history.replaceState({}, '', newUrl);
  }, [searchQuery]);

  const loadHomeData = useCallback(async () => {
    setIsLoading(true);
    try {
      const [trending, trMovies, popSeries] = await Promise.all([
        fetchTrendingAll(),
        fetchTopRatedMovies(),
        fetchPopularByType('tv')
      ]);

      setTrendingMovies(trending);
      setTopRatedMovies(trMovies);
      setPopularSeries(popSeries);

      if (trending.length > 0) {
        const featuredItem = trending[0];
        const featuredFull = await fetchMovieDetails(featuredItem.id, featuredItem.mediaType);
        setFeaturedMovie(featuredFull);
      }
    } catch (error) {
      console.error("Failed to fetch home data", error);
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
      } else if (currentView === 'series') {
        if (topRatedSeries.length === 0) {
          const series = await fetchTopRatedTV();
          setTopRatedSeries(series);
        }
      } else if (currentView === 'popular') {
        if (trendingMovies.length === 0) {
           const trending = await fetchTrendingAll();
           setTrendingMovies(trending);
        }
      }
    };
    fetchData();
  }, [currentView, loadHomeData, trendingMovies.length, popularMovies.length, topRatedSeries.length]);

  useEffect(() => {
    const timer = setTimeout(async () => {
      if (searchQuery.trim()) {
        setIsSearching(true);
        try {
          const results = await searchContent(searchQuery);
          setSearchResults(results);
        } catch (error) {
          console.error("Search failed", error);
        } finally {
          setIsSearching(false);
        }
      } else {
        setSearchResults([]);
      }
    }, 500);

    return () => clearTimeout(timer);
  }, [searchQuery]);

  const handlePlay = (movie: Movie) => {
    setPlayingMovie(movie);
    if (selectedMovie) setSelectedMovie(null);
    if (user) {
        addToHistory(movie);
    }
    const slug = slugify(movie.title);
    window.history.pushState({}, '', `/watch/${movie.mediaType || 'movie'}/${movie.id}/${slug}`);
  };

  const handleSelectMovie = (movie: Movie) => {
    setSelectedMovie(movie);
    const slug = slugify(movie.title);
    window.history.pushState({}, '', `/${movie.mediaType || 'movie'}/${movie.id}/${slug}`);
  };

  const handleAddToPlaylist = (movie: Movie) => {
    if (!user) {
        setIsAuthModalOpen(true);
        return;
    }
    setPlaylistMovie(movie);
    setIsPlaylistModalOpen(true);
  };

  const handleViewChange = (view: ViewType) => {
    if (view === 'profile' && !user) {
        setIsAuthModalOpen(true);
        return;
    }
    setSearchQuery('');
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
    // if URL was /movie/... or /tv/..., go back to current view/search
    if (window.location.pathname.match(/^\/(movie|tv)\/\d+/)) {
      const base = currentView === 'home' ? '/' : `/${currentView}`;
      const q = searchQuery ? `?q=${encodeURIComponent(searchQuery)}` : '';
      window.history.pushState({}, '', `${base}${q}`);
    } else {
      window.history.back();
    }
  };

  const toggleTheme = () => setIsDarkMode(!isDarkMode);

  const renderGridSection = (title: string, movies: Movie[]) => (
    <div className="max-w-[1400px] mx-auto px-4 md:px-12 py-16">
      <div className="flex items-center justify-between mb-12 border-b border-slate-200 dark:border-white/5 pb-6">
        <h2 className="text-3xl font-bold text-slate-900 dark:text-white tracking-tight">{title}</h2>
        <span className="text-slate-500 text-sm font-medium bg-slate-100 dark:bg-white/5 px-3 py-1 rounded-full">{movies.length} TITLES</span>
      </div>
      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-6 md:gap-8">
        {movies.map((movie) => (
          <MovieCard 
            key={movie.id} 
            movie={movie} 
            onClick={handleSelectMovie} 
            width="w-full"
          />
        ))}
      </div>
    </div>
  );

  const renderContent = () => {
    if (searchQuery) {
        return (
          <SearchResults 
            query={searchQuery}
            results={searchResults}
            onSelectMovie={handleSelectMovie}
            isLoading={isSearching}
          />
        );
    }

    if (currentView === 'profile') {
        return (
            <ProfileView 
                onBack={() => setCurrentView('home')} 
                onSelectMovie={handleSelectMovie}
            />
        );
    }

    if (currentView === 'movies') {
        return (
          <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="pt-24 pb-20">
            {renderGridSection("All Movies", popularMovies)}
          </motion.div>
        );
    }

    if (currentView === 'series') {
        return (
          <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="pt-24 pb-20">
            {renderGridSection("TV Series", popularSeries)}
          </motion.div>
        );
    }

    if (currentView === 'popular') {
        return (
          <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="pt-24 pb-20">
            {renderGridSection("Trending & Popular", trendingMovies)}
          </motion.div>
        );
    }

    return (
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        exit={{ opacity: 0 }}
        key="home"
      >
        <Hero 
          movie={featuredMovie} 
          isLoading={isLoading}
          onPlay={() => featuredMovie && handlePlay(featuredMovie)}
          onMoreInfo={handleSelectMovie}
        />
        
        <div className="-mt-32 relative z-10 space-y-2 pb-20">
            <div className="h-32 bg-gradient-to-t from-white dark:from-slate-950 to-transparent pointer-events-none" />
            
            <div className="w-full overflow-hidden">
              {user && user.history && user.history.length > 0 && (
                 <MovieRow 
                    title="Recently Watched" 
                    movies={user.history} 
                    onSelectMovie={handleSelectMovie}
                />
              )}

              <MovieRow title="Trending Now" movies={trendingMovies} onSelectMovie={handleSelectMovie} />
              <MovieRow title="Top Rated Movies" movies={topRatedMovies} onSelectMovie={handleSelectMovie} />
              <MovieRow title="Popular TV Series" movies={popularSeries} onSelectMovie={handleSelectMovie} />
            </div>
        </div>
      </motion.div>
    );
  };

  return (
    <div className="bg-slate-50 dark:bg-slate-950 min-h-screen text-slate-900 dark:text-slate-50 selection:bg-primary/30 transition-colors duration-300">
      <Navbar 
        activeView={currentView}
        onViewChange={handleViewChange}
        onSearch={setSearchQuery} 
        onOpenAuth={() => setIsAuthModalOpen(true)}
        isDarkMode={isDarkMode}
        toggleTheme={toggleTheme}
      />
      
      <main className="min-h-screen">
        <AnimatePresence mode="wait">
           {renderContent()}
        </AnimatePresence>
      </main>

      {!searchQuery && currentView !== 'profile' && (
        <footer className="py-16 border-t border-slate-200 dark:border-white/5 bg-slate-100 dark:bg-black/50 text-center text-slate-500 text-sm">
            <div className="max-w-[1400px] mx-auto px-4">
              <p className="font-bold text-slate-400 dark:text-slate-400 mb-6 tracking-tighter text-lg uppercase">HOMEFLIX</p>
              <div className="flex flex-wrap justify-center gap-8 mb-8">
                  <a href="#" className="hover:text-primary transition-colors">Audio Description</a>
                  <a href="#" className="hover:text-primary transition-colors">Help Center</a>
                  <a href="#" className="hover:text-primary transition-colors">Gift Cards</a>
                  <a href="#" className="hover:text-primary transition-colors">Media Center</a>
                  <a href="#" className="hover:text-primary transition-colors">Investor Relations</a>
                  <a href="#" className="hover:text-primary transition-colors">Jobs</a>
                  <a href="#" className="hover:text-primary transition-colors">Terms of Use</a>
                  <a href="#" className="hover:text-primary transition-colors">Privacy</a>
              </div>
              <p className="text-xs opacity-50">&copy; 1997-2024 Homeflix, Inc. All rights reserved.</p>
            </div>
        </footer>
      )}

      <AnimatePresence>
        {playingMovie && (
            <VideoPlayer 
                key="player"
                movie={playingMovie} 
                onClose={handleClosePlayer} 
            />
        )}
      </AnimatePresence>

      <AnimatePresence>
        {selectedMovie && (
            <MovieDetails 
                key="details"
                movie={selectedMovie} 
                onClose={handleCloseDetails}
                onPlay={() => handlePlay(selectedMovie)}
                onAddToPlaylist={() => handleAddToPlaylist(selectedMovie)}
            />
        )}
      </AnimatePresence>

      <AnimatePresence>
        {isAuthModalOpen && (
          <AuthModal 
            key="auth"
            isOpen={isAuthModalOpen} 
            onClose={() => setIsAuthModalOpen(false)} 
          />
        )}
      </AnimatePresence>

      <AnimatePresence>
        {isPlaylistModalOpen && (
            <AddToPlaylistModal
                key="playlist"
                isOpen={isPlaylistModalOpen}
                onClose={() => { setIsPlaylistModalOpen(false); setPlaylistMovie(null); }}
                movie={playlistMovie}
            />
        )}
      </AnimatePresence>
    </div>
  );
};

export default App;
