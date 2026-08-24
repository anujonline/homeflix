
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