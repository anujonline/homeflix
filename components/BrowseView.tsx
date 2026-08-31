import React, { useCallback, useEffect, useState } from 'react';
import { ChevronDown, SlidersHorizontal } from 'lucide-react';
import { Movie, Genre } from '../types';
import MovieCard from './MovieCard';
import { discoverContent, fetchGenresByType } from '../services/tmdbService';

interface BrowseViewProps {
  onSelectMovie: (movie: Movie) => void;
}

const CURRENT_YEAR = new Date().getFullYear();
const YEARS = Array.from({ length: CURRENT_YEAR - 1969 }, (_, i) => CURRENT_YEAR - i);

const sortOptions = (type: 'movie' | 'tv') => {
  const opts = [
    { value: 'popularity.desc', label: 'Most Popular' },
    { value: 'vote_average.desc', label: 'Highest Rated' },
  ];
  if (type === 'movie') opts.push({ value: 'revenue.desc', label: 'Box Office' });
  opts.push({ value: type === 'movie' ? 'primary_release_date.desc' : 'first_air_date.desc', label: 'Newest' });
  return opts;
};

const FilterSelect: React.FC<{
  value: string;
  onChange: (v: string) => void;
  children: React.ReactNode;
  ariaLabel: string;
}> = ({ value, onChange, children, ariaLabel }) => (
  <div className="relative">
    <select
      aria-label={ariaLabel}
      value={value}
      onChange={(e) => onChange(e.target.value)}
      className="appearance-none w-full bg-white dark:bg-white/[0.06] border border-slate-200 dark:border-white/10 rounded-full pl-4 pr-9 py-2 text-sm font-medium text-slate-700 dark:text-slate-200 outline-none focus:border-amber-500 cursor-pointer"
    >
      {children}
    </select>
    <ChevronDown size={14} className="absolute right-3.5 top-1/2 -translate-y-1/2 pointer-events-none text-slate-400" />
  </div>
);

const BrowseView: React.FC<BrowseViewProps> = ({ onSelectMovie }) => {
  const [type, setType] = useState<'movie' | 'tv'>('movie');
  const [genreId, setGenreId] = useState('');
  const [year, setYear] = useState('');
  const [sort, setSort] = useState('popularity.desc');

  const [genres, setGenres] = useState<Genre[]>([]);
  const [items, setItems] = useState<Movie[]>([]);
  const [meta, setMeta] = useState({ page: 0, totalPages: 1, totalResults: 0 });
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);

  useEffect(() => {
    let mounted = true;
    setGenres([]);
    fetchGenresByType(type)
      .then((g) => mounted && setGenres(g))
      .catch(() => {});
    return () => { mounted = false; };
  }, [type]);

  const loadPage = useCallback(async (page: number, append: boolean) => {
    append ? setLoadingMore(true) : setLoading(true);
    try {
      const data = await discoverContent({ type, genreId, year, sort, page });
      setItems((prev) => {
        if (!append) return data.results;
        const seen = new Set(prev.map((m) => m.id));
        return [...prev, ...data.results.filter((m) => !seen.has(m.id))];
      });
      setMeta({ page: data.page, totalPages: data.totalPages, totalResults: data.totalResults });
    } catch (e) {
      console.error(e);
      if (!append) setItems([]);
    } finally {
      setLoading(false);
      setLoadingMore(false);
    }
  }, [type, genreId, year, sort]);

  // any filter change re-runs this (loadPage identity changes) and resets to page 1
  useEffect(() => { loadPage(1, false); }, [loadPage]);

  const switchType = (t: 'movie' | 'tv') => {
    if (t === type) return;
    setType(t);
    setGenreId('');
    // date/revenue sorts are type-specific — fall back to the safe default
    const valid = sortOptions(t).some((o) => o.value === sort);
    if (!valid) setSort('popularity.desc');
  };

  const activeGenre = genres.find((g) => String(g.id) === genreId);
  const activeSortLabel = sortOptions(type).find((o) => o.value === sort)?.label;
  const hasMore = meta.page < meta.totalPages;

  return (
    <div className="pb-24">
      <div className="max-w-[1440px] mx-auto px-4 md:px-8 pt-4">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-3 mb-4">
          <div className="text-center md:text-left">
            <h1 className="text-2xl md:text-3xl font-bold tracking-tight text-slate-900 dark:text-white flex items-center justify-center md:justify-start gap-2">
              <SlidersHorizontal size={24} className="text-amber-500" /> Browse
            </h1>
            <p className="text-xs text-slate-500 mt-1">
              {loading ? 'Searching TMDB…' : `${meta.totalResults.toLocaleString()} titles${activeGenre ? ` • ${activeGenre.name}` : ''}${year ? ` • ${year}` : ''} • ${activeSortLabel}`}
            </p>
          </div>
          <div className="flex justify-center md:justify-end">
            <div className="flex items-center gap-1 p-1 rounded-full bg-slate-100 dark:bg-white/[0.06] border border-slate-200 dark:border-white/5">
              <button onClick={() => switchType('movie')} className={`px-4 py-1.5 rounded-full text-sm font-medium ${type === 'movie' ? 'bg-slate-900 dark:bg-white text-white dark:text-black' : 'text-slate-600 dark:text-slate-400'}`}>Movies</button>
              <button onClick={() => switchType('tv')} className={`px-4 py-1.5 rounded-full text-sm font-medium ${type === 'tv' ? 'bg-slate-900 dark:bg-white text-white dark:text-black' : 'text-slate-600 dark:text-slate-400'}`}>Series</button>
            </div>
          </div>
        </div>

        <div className="max-w-2xl mx-auto md:max-w-none">
          <div className="grid grid-cols-2 md:grid-cols-3 gap-2.5 md:gap-3 mb-4">
            <FilterSelect value={genreId} onChange={setGenreId} ariaLabel="Filter by genre">
              <option value="">All Genres</option>
              {genres.map((g) => (
                <option key={g.id} value={String(g.id)}>{g.name}</option>
              ))}
            </FilterSelect>
            <FilterSelect value={year} onChange={setYear} ariaLabel="Filter by year">
              <option value="">Any Year</option>
              {YEARS.map((y) => (
                <option key={y} value={String(y)}>{y}</option>
              ))}
            </FilterSelect>
            <FilterSelect value={sort} onChange={setSort} ariaLabel="Sort results">
              {sortOptions(type).map((o) => (
                <option key={o.value} value={o.value}>{o.label}</option>
              ))}
            </FilterSelect>
          </div>
        </div>
      </div>

      <div className="max-w-[1440px] mx-auto px-4 md:px-8 py-6">
        {loading ? (
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-4 md:gap-6">
            {[...Array(18)].map((_, i) => (
              <div key={i} className="aspect-[2/3] bg-slate-200 dark:bg-white/5 rounded-2xl animate-pulse" />
            ))}
          </div>
        ) : items.length === 0 ? (
          <div className="py-16 text-center bg-white dark:bg-white/[0.04] rounded-2xl border border-dashed border-slate-200 dark:border-white/10">
            <div className="w-14 h-14 rounded-full bg-slate-100 dark:bg-white/5 grid place-items-center mx-auto mb-3 text-2xl">🎬</div>
            <p className="font-semibold text-slate-900 dark:text-white">Nothing matches these filters</p>
            <p className="text-sm text-slate-500 mt-1">Try a different genre, year or sort.</p>
          </div>
        ) : (
          <>
            <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-4 md:gap-6">
              {items.map((m) => (
                <MovieCard key={`${m.id}-${m.title}`} movie={m} onClick={onSelectMovie} width="w-full" />
              ))}
            </div>
            {hasMore && (
              <div className="flex justify-center mt-8">
                <button
                  onClick={() => loadPage(meta.page + 1, true)}
                  disabled={loadingMore}
                  className="px-8 py-3 rounded-full bg-slate-900 dark:bg-white text-white dark:text-black text-sm font-bold hover:opacity-90 disabled:opacity-50"
                >
                  {loadingMore ? 'Loading…' : `Load more (page ${meta.page + 1} of ${meta.totalPages})`}
                </button>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
};

export default BrowseView;
