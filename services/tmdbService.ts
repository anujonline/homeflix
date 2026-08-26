
import { Movie, Genre, Video, CastMember, WatchProvider, WatchProviderResult, SeasonSummary, Episode, DiscoverPage } from '../types';

// In production (single Docker image) frontend and API share origin -> relative /api.
// For dev, Vite proxies /api to Spring Boot (see vite.config.ts). Override with VITE_API_URL if needed.
const API_BASE = (import.meta as any).env?.VITE_API_URL || '';
const BACKEND_BASE = `${API_BASE}/api`;
const IMAGE_BASE_URL = 'https://image.tmdb.org/t/p';

interface TMDBMovie {
  id: number;
  title?: string;
  name?: string;
  overview: string;
  vote_average: number;
  release_date?: string;
  first_air_date?: string;
  poster_path: string;
  backdrop_path: string;
  genre_ids: number[];
  media_type?: 'movie' | 'tv' | 'person';
}

// Caching layer
let genresPromise: Promise<Genre[]> | null = null;
const apiCache = new Map<string, { data: any; timestamp: number }>();
const CACHE_TTL = 1000 * 60 * 5; // 5 minutes

const getCachedData = (key: string) => {
  const cached = apiCache.get(key);
  if (cached && Date.now() - cached.timestamp < CACHE_TTL) {
    return cached.data;
  }
  return null;
};

const setCachedData = (key: string, data: any) => {
  apiCache.set(key, { data, timestamp: Date.now() });
};

export const fetchGenres = (): Promise<Genre[]> => {
  if (genresPromise) return genresPromise;

  genresPromise = (async () => {
    try {
      const [movieRes, tvRes] = await Promise.all([
        fetch(`${BACKEND_BASE}/genres/movie`),
        fetch(`${BACKEND_BASE}/genres/tv`)
      ]);

      const [movieData, tvData] = await Promise.all([movieRes.json(), tvRes.json()]);
      const combined = [...(movieData.genres || []), ...(tvData.genres || [])];

      const map = new Map();
      combined.forEach(g => map.set(g.id, g));
      return Array.from(map.values());
    } catch (error) {
      console.error('Error fetching genres:', error);
      genresPromise = null; // Reset on error so it can retry
      return [];
    }
  })();

  return genresPromise;
};

const mapMovie = (item: TMDBMovie, genres: Genre[], forcedType?: 'movie' | 'tv'): Movie => {
  const movieGenres = item.genre_ids
    ? item.genre_ids.map((id) => genres.find((g) => g.id === id)?.name || '').filter(Boolean)
    : [];

  const title = item.title || item.name || 'Untitled';
  const date = item.release_date || item.first_air_date;
  const year = date ? new Date(date).getFullYear() : 0;
  const mediaType = (item.media_type as 'movie' | 'tv') || forcedType || 'movie';

  return {
    id: item.id,
    title,
    description: item.overview || 'No description available.',
    rating: Number((item.vote_average || 0).toFixed(1)),
    year,
    genre: movieGenres,
    imageUrl: item.poster_path ? `${IMAGE_BASE_URL}/w500${item.poster_path}` : 'https://via.placeholder.com/500x750?text=No+Image',
    backdropUrl: item.backdrop_path ? `${IMAGE_BASE_URL}/original${item.backdrop_path}` : 'https://via.placeholder.com/1920x1080?text=No+Image',
    mediaType,
  };
};

const fetchAndCacheList = async (backendPath: string, type?: 'movie' | 'tv'): Promise<Movie[]> => {
  const cached = getCachedData(backendPath);
  if (cached) return cached;

  const genres = await fetchGenres();
  const response = await fetch(`${BACKEND_BASE}${backendPath}`);
  const data = await response.json();

  const results = data.results
    .filter((m: TMDBMovie) => m.media_type !== 'person')
    .map((m: TMDBMovie) => mapMovie(m, genres, type));

  setCachedData(backendPath, results);
  return results;
};

export const fetchTrendingAll = () => fetchAndCacheList('/trending');
export const fetchPopularByType = (type: 'movie' | 'tv') => fetchAndCacheList(`/${type}/popular`, type);
export const fetchTopRatedMovies = () => fetchAndCacheList('/movie/top_rated', 'movie');
export const fetchTopRatedTV = () => fetchAndCacheList('/tv/top_rated', 'tv');

export const fetchMovieDetails = async (id: number, type: 'movie' | 'tv' = 'movie'): Promise<Movie> => {
  const cacheKey = `details-${type}-${id}`;
  const cached = getCachedData(cacheKey);
  if (cached) return cached;

  const endpoint = type === 'tv' ? 'tv' : 'movie';
  const response = await fetch(`${BACKEND_BASE}/${endpoint}/${id}`);
  
  if (!response.ok) throw new Error('Failed to fetch details');

  const data = await response.json();
  const movieGenres = data.genres ? data.genres.map((g: any) => g.name) : [];
  
  let duration = '';
  if (type === 'movie') {
    const hours = Math.floor((data.runtime || 0) / 60);
    const minutes = (data.runtime || 0) % 60;
    duration = `${hours}h ${minutes}m`;
  } else {
    const runTime = data.episode_run_time?.[0];
    duration = runTime ? `${runTime}m` : `${data.number_of_seasons} Season${data.number_of_seasons !== 1 ? 's' : ''}`;
  }

  const result: Movie = {
    id: data.id,
    title: data.title || data.name,
    description: data.overview,
    rating: Number(data.vote_average.toFixed(1)),
    year: data.release_date || data.first_air_date ? new Date(data.release_date || data.first_air_date).getFullYear() : 0,
    duration,
    genre: movieGenres,
    imageUrl: data.poster_path ? `${IMAGE_BASE_URL}/w500${data.poster_path}` : '',
    backdropUrl: data.backdrop_path ? `${IMAGE_BASE_URL}/original${data.backdrop_path}` : '',
    mediaType: type,
  };

  setCachedData(cacheKey, result);
  return result;
};

export const searchContent = async (query: string): Promise<Movie[]> => {
  if (!query) return [];
  // We don't cache search results as strictly because queries vary wildly
  const genres = await fetchGenres();
  const response = await fetch(`${BACKEND_BASE}/search?query=${encodeURIComponent(query)}`);
  const data = await response.json();

  return data.results
    .filter((item: TMDBMovie) => item.media_type !== 'person' && item.poster_path)
    .map((item: TMDBMovie) => mapMovie(item, genres));
};

// ---------------------------------------------------------------------------
// Discover / Browse — /api/discover/{type} (genre, year, sort, page)
// ---------------------------------------------------------------------------

export interface DiscoverParams {
  type: 'movie' | 'tv';
  genreId?: string; // numeric TMDB genre id (comma/pipe combos allowed)
  year?: string;    // YYYY
  sort?: string;    // popularity.desc | vote_average.desc | revenue.desc | *_date.desc
  page?: number;
}

const mapDiscoverList = async (data: any, type: 'movie' | 'tv'): Promise<DiscoverPage> => {
  const genres = await fetchGenres();
  const results: Movie[] = (data.results || [])
    .filter((m: TMDBMovie) => m.poster_path)
    .map((m: TMDBMovie) => mapMovie(m, genres, type));
  return {
    results,
    page: data.page || 1,
    totalPages: Math.min(data.total_pages || 1, 500),
    totalResults: data.total_results || results.length,
  };
};

export const discoverContent = async (params: DiscoverParams): Promise<DiscoverPage> => {
  const { type, genreId = '', year = '', sort = 'popularity.desc', page = 1 } = params;
  const qs = new URLSearchParams();
  if (genreId) qs.set('genre', genreId);
  if (year) qs.set('year', year);
  if (sort) qs.set('sort', sort);
  qs.set('page', String(page));

  const backendPath = `/discover/${type}?${qs.toString()}`;
  const cached = getCachedData(backendPath);
  if (cached) return cached;

  const response = await fetch(`${BACKEND_BASE}${backendPath}`);
  if (!response.ok) throw new Error('Failed to fetch discover results');
  const data = await response.json();
  const pageData = await mapDiscoverList(data, type);
  setCachedData(backendPath, pageData);
  return pageData;
};

export const fetchGenresByType = async (type: 'movie' | 'tv'): Promise<Genre[]> => {
  const cacheKey = `genres-${type}`;
  const cached = getCachedData(cacheKey);
  if (cached) return cached;
  const response = await fetch(`${BACKEND_BASE}/genres/${type}`);
  if (!response.ok) throw new Error('Failed to fetch genres');
  const data = await response.json();
  const genres: Genre[] = data.genres || [];
  setCachedData(cacheKey, genres);
  return genres;
};

// ---------------------------------------------------------------------------
// Enriched details — videos, credits, watch providers
// ---------------------------------------------------------------------------

export const fetchVideos = async (id: number, type: 'movie' | 'tv' = 'movie'): Promise<Video[]> => {
  const cacheKey = `videos-${type}-${id}`;
  const cached = getCachedData(cacheKey);
  if (cached) return cached;

  const response = await fetch(`${BACKEND_BASE}/${type}/${id}/videos`);
  if (!response.ok) throw new Error('Failed to fetch videos');
  const data = await response.json();
  const videos: Video[] = (data.results || [])
    .filter((v: any) => v.site === 'YouTube')
    .map((v: any) => ({ id: v.id, key: v.key, name: v.name, type: v.type }));
  setCachedData(cacheKey, videos);
  return videos;
};

export const fetchCredits = async (id: number, type: 'movie' | 'tv' = 'movie'): Promise<{ cast: CastMember[]; directors: string[] }> => {
  const cacheKey = `credits-${type}-${id}`;
  const cached = getCachedData(cacheKey);
  if (cached) return cached;

  const response = await fetch(`${BACKEND_BASE}/${type}/${id}/credits`);
  if (!response.ok) throw new Error('Failed to fetch credits');
  const data = await response.json();
  const result = {
    cast: (data.cast || []).slice(0, 12).map((c: any) => ({
      id: c.id,
      name: c.name,
      character: c.character || '',
      profileUrl: c.profile_path ? `${IMAGE_BASE_URL}/w185${c.profile_path}` : null,
    })),
    directors: (data.crew || [])
      .filter((c: any) => (type === 'tv' ? c.job === 'Creator' : c.job === 'Director') && c.name)
      .map((c: any) => c.name)
      .filter((name: string, i: number, arr: string[]) => arr.indexOf(name) === i),
  };
  setCachedData(cacheKey, result);
  return result;
};

// Region for watch-provider lookups: derived from the browser locale, falls back to US.
export const getWatchRegion = (): string => {
  try {
    const locale = new Intl.Locale(navigator.language) as any;
    const region = locale.region || locale.max?.region;
    if (region && /^[A-Za-z]{2}$/.test(region)) return String(region).toUpperCase();
  } catch { /* fall through */ }
  return 'US';
};

const mapProviders = (list: any[] | undefined): WatchProvider[] =>
  (list || []).map((p) => ({
    id: p.provider_id,
    name: p.provider_name,
    logoUrl: p.logo_path ? `${IMAGE_BASE_URL}/w154${p.logo_path}` : '',
  }));

export const fetchWatchProviders = async (id: number, type: 'movie' | 'tv' = 'movie', region?: string): Promise<WatchProviderResult | null> => {
  const r = region || getWatchRegion();
  const cacheKey = `providers-${type}-${id}-${r}`;
  const cached = getCachedData(cacheKey);
  if (cached) return cached;

  const response = await fetch(`${BACKEND_BASE}/${type}/${id}/watch/providers?watch_region=${r}`);
  if (!response.ok) throw new Error('Failed to fetch watch providers');
  const data = await response.json();
  const regionData = (data.results && (data.results[r] || Object.values(data.results).find((v: any) => v && (v.flatrate || v.rent || v.buy)))) as any;
  const result: WatchProviderResult | null = regionData
    ? {
        link: regionData.link || `https://www.themoviedb.org/${type}/${id}/watch`,
        flatrate: mapProviders(regionData.flatrate),
        rent: mapProviders(regionData.rent),
        buy: mapProviders(regionData.buy),
      }
    : null;
  setCachedData(cacheKey, result);
  return result;
};

// ---------------------------------------------------------------------------
// Similar / Recommendations
// ---------------------------------------------------------------------------

export const fetchSimilar = (id: number, type: 'movie' | 'tv' = 'movie'): Promise<Movie[]> =>
  fetchAndCacheList(`/${type}/${id}/similar`, type);

export const fetchRecommendations = (id: number, type: 'movie' | 'tv' = 'movie'): Promise<Movie[]> =>
  fetchAndCacheList(`/${type}/${id}/recommendations`, type);

// ---------------------------------------------------------------------------
// TV seasons & episodes
// ---------------------------------------------------------------------------

export const fetchTvSeasons = async (tvId: number): Promise<SeasonSummary[]> => {
  const cacheKey = `tv-seasons-${tvId}`;
  const cached = getCachedData(cacheKey);
  if (cached) return cached;

  const response = await fetch(`${BACKEND_BASE}/tv/${tvId}`);
  if (!response.ok) throw new Error('Failed to fetch tv seasons');
  const data = await response.json();
  const seasons: SeasonSummary[] = (data.seasons || [])
    .filter((s: any) => s.season_number > 0)
    .map((s: any) => ({
      seasonNumber: s.season_number,
      name: s.name || `Season ${s.season_number}`,
      episodeCount: s.episode_count || 0,
      airDate: s.air_date || null,
      posterUrl: s.poster_path ? `${IMAGE_BASE_URL}/w300${s.poster_path}` : null,
    }));
  setCachedData(cacheKey, seasons);
  return seasons;
};

export const fetchSeasonEpisodes = async (tvId: number, seasonNumber: number): Promise<{ name: string; episodes: Episode[] }> => {
  const cacheKey = `season-${tvId}-${seasonNumber}`;
  const cached = getCachedData(cacheKey);
  if (cached) return cached;

  const response = await fetch(`${BACKEND_BASE}/tv/${tvId}/season/${seasonNumber}`);
  if (!response.ok) throw new Error('Failed to fetch season');
  const data = await response.json();
  const result = {
    name: data.name || `Season ${seasonNumber}`,
    episodes: (data.episodes || []).map((e: any) => ({
      id: e.id,
      episodeNumber: e.episode_number,
      name: e.name || `Episode ${e.episode_number}`,
      overview: e.overview || '',
      airDate: e.air_date || null,
      stillUrl: e.still_path ? `${IMAGE_BASE_URL}/w300${e.still_path}` : null,
      rating: Number((e.vote_average || 0).toFixed(1)),
      runtime: e.runtime || null,
    })),
  };
  setCachedData(cacheKey, result);
  return result;
};

// ---------------------------------------------------------------------------
// Misc — configuration, person search
// ---------------------------------------------------------------------------

let configurationPromise: Promise<any> | null = null;

export const fetchConfiguration = (): Promise<any> => {
  if (configurationPromise) return configurationPromise;
  configurationPromise = fetch(`${BACKEND_BASE}/configuration`)
    .then((r) => r.json())
    .catch((e) => {
      configurationPromise = null;
      throw e;
    });
  return configurationPromise;
};

export interface PersonResult {
  id: number;
  name: string;
  knownFor: string;
  profileUrl: string | null;
}

export const searchPeople = async (query: string): Promise<PersonResult[]> => {
  if (!query) return [];
  const response = await fetch(`${BACKEND_BASE}/search/person?query=${encodeURIComponent(query)}`);
  if (!response.ok) throw new Error('Failed to search people');
  const data = await response.json();
  return (data.results || []).slice(0, 10).map((p: any) => ({
    id: p.id,
    name: p.name,
    knownFor: (p.known_for_department || '').replace('_', ' '),
    profileUrl: p.profile_path ? `${IMAGE_BASE_URL}/w185${p.profile_path}` : null,
  }));
};
