
import { Movie } from '../types';

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

interface Genre {
  id: number;
  name: string;
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
