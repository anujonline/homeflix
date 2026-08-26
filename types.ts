
export interface Movie {
  id: number;
  title: string;
  description: string;
  rating: number;
  year: number;
  duration?: string; // Optional because list endpoints don't return runtime
  genre: string[];
  imageUrl: string;
  backdropUrl: string;
  mediaType?: 'movie' | 'tv';
}

export interface Playlist {
  id: string;
  name: string;
  movies: Movie[];
  createdAt: number;
}

export interface User {
  id: string;
  name: string;
  email: string;
  avatar?: string;
  playlists: Playlist[];
  history: Movie[];
}

export interface Genre {
  id: number;
  name: string;
}

export interface Video {
  id: string;
  key: string; // YouTube key
  name: string;
  type: string; // Trailer, Teaser, Clip, ...
}

export interface CastMember {
  id: number;
  name: string;
  character: string;
  profileUrl: string | null;
}

export interface WatchProvider {
  id: number;
  name: string;
  logoUrl: string;
}

export interface WatchProviderResult {
  link: string;
  flatrate: WatchProvider[];
  rent: WatchProvider[];
  buy: WatchProvider[];
}

export interface SeasonSummary {
  seasonNumber: number;
  name: string;
  episodeCount: number;
  airDate: string | null;
  posterUrl: string | null;
}

export interface Episode {
  id: number;
  episodeNumber: number;
  name: string;
  overview: string;
  airDate: string | null;
  stillUrl: string | null;
  rating: number;
  runtime: number | null;
}

export interface DiscoverPage {
  results: Movie[];
  page: number;
  totalPages: number;
  totalResults: number;
}