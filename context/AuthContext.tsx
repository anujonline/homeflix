
import React, { createContext, useContext, useState, useEffect } from 'react';
import { User, Movie, Playlist } from '../types';

interface AuthContextType {
  user: User | null;
  login: (email: string, password: string) => Promise<void>;
  signup: (email: string, password: string, name: string) => Promise<void>;
  logout: () => void;
  createPlaylist: (name: string) => void;
  addToPlaylist: (playlistId: string, movie: Movie) => void;
  removeFromPlaylist: (playlistId: string, movieId: number) => void;
  deletePlaylist: (playlistId: string) => void;
  addToHistory: (movie: Movie) => void;
  clearHistory: () => void;
  isAuthenticated: boolean;
  isLoading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // Check for active session on mount
    const storedUser = localStorage.getItem('homeflix_session');
    if (storedUser) {
      const parsedUser = JSON.parse(storedUser);
      // Ensure arrays exist for legacy data
      if (!parsedUser.playlists) parsedUser.playlists = [];
      if (!parsedUser.history) parsedUser.history = [];
      setUser(parsedUser);
    }
    setIsLoading(false);
  }, []);

  const updateUserPersistence = (updatedUser: User) => {
    // Update session
    localStorage.setItem('homeflix_session', JSON.stringify(updatedUser));
    
    // Update main users database
    const users = JSON.parse(localStorage.getItem('homeflix_users') || '[]');
    const updatedUsers = users.map((u: any) => u.id === updatedUser.id ? { ...u, ...updatedUser } : u);
    localStorage.setItem('homeflix_users', JSON.stringify(updatedUsers));
    
    setUser(updatedUser);
  };

  const login = async (email: string, password: string) => {
    // Simulate API delay
    await new Promise(resolve => setTimeout(resolve, 800));

    const users = JSON.parse(localStorage.getItem('homeflix_users') || '[]');
    const foundUser = users.find((u: any) => u.email === email && u.password === password);

    if (foundUser) {
      // Ensure data structures exist on legacy data
      if (!foundUser.playlists) foundUser.playlists = [];
      if (!foundUser.history) foundUser.history = [];
      
      const { password, ...userWithoutPass } = foundUser;
      setUser(userWithoutPass);
      localStorage.setItem('homeflix_session', JSON.stringify(userWithoutPass));
    } else {
      throw new Error('Invalid email or password');
    }
  };

  const signup = async (email: string, password: string, name: string) => {
    // Simulate API delay
    await new Promise(resolve => setTimeout(resolve, 800));

    const users = JSON.parse(localStorage.getItem('homeflix_users') || '[]');
    
    if (users.some((u: any) => u.email === email)) {
      throw new Error('User already exists');
    }

    const newUser = {
      id: Date.now().toString(),
      email,
      password,
      name,
      avatar: `https://api.dicebear.com/7.x/avataaars/svg?seed=${name}`,
      playlists: [],
      history: []
    };

    users.push(newUser);
    localStorage.setItem('homeflix_users', JSON.stringify(users));

    const { password: _, ...userWithoutPass } = newUser;
    setUser(userWithoutPass);
    localStorage.setItem('homeflix_session', JSON.stringify(userWithoutPass));
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('homeflix_session');
  };

  const createPlaylist = (name: string) => {
    if (!user) return;
    const newPlaylist: Playlist = {
      id: Date.now().toString(),
      name,
      movies: [],
      createdAt: Date.now()
    };
    const updatedUser = { ...user, playlists: [...user.playlists, newPlaylist] };
    updateUserPersistence(updatedUser);
  };

  const addToPlaylist = (playlistId: string, movie: Movie) => {
    if (!user) return;
    const updatedPlaylists = user.playlists.map(pl => {
      if (pl.id === playlistId) {
        // Prevent duplicates
        if (pl.movies.some(m => m.id === movie.id)) return pl;
        return { ...pl, movies: [...pl.movies, movie] };
      }
      return pl;
    });
    updateUserPersistence({ ...user, playlists: updatedPlaylists });
  };

  const removeFromPlaylist = (playlistId: string, movieId: number) => {
    if (!user) return;
    const updatedPlaylists = user.playlists.map(pl => {
      if (pl.id === playlistId) {
        return { ...pl, movies: pl.movies.filter(m => m.id !== movieId) };
      }
      return pl;
    });
    updateUserPersistence({ ...user, playlists: updatedPlaylists });
  };

  const deletePlaylist = (playlistId: string) => {
    if (!user) return;
    const updatedPlaylists = user.playlists.filter(pl => pl.id !== playlistId);
    updateUserPersistence({ ...user, playlists: updatedPlaylists });
  };

  const addToHistory = (movie: Movie) => {
    if (!user) return;
    
    // Remove if exists to prevent duplicates and move to top
    const filteredHistory = user.history.filter(m => m.id !== movie.id);
    // Add to front, limit to 20 items
    const newHistory = [movie, ...filteredHistory].slice(0, 20);
    
    updateUserPersistence({ ...user, history: newHistory });
  };

  const clearHistory = () => {
    if (!user) return;
    updateUserPersistence({ ...user, history: [] });
  };

  return (
    <AuthContext.Provider value={{ 
      user, 
      login, 
      signup, 
      logout, 
      isAuthenticated: !!user, 
      isLoading,
      createPlaylist,
      addToPlaylist,
      removeFromPlaylist,
      deletePlaylist,
      addToHistory,
      clearHistory
    }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};