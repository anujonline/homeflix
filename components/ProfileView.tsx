import React from 'react';
import { motion } from 'framer-motion';
import { ArrowLeft, Trash2, User as UserIcon, History, PlayCircle } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { Movie } from '../types';
import MovieRow from './MovieRow';

interface ProfileViewProps {
  onBack: () => void;
  onSelectMovie: (movie: Movie) => void;
}

const ProfileView: React.FC<ProfileViewProps> = ({ onBack, onSelectMovie }) => {
  const { user, deletePlaylist, clearHistory } = useAuth();

  if (!user) return null;

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -20 }}
      className="min-h-screen bg-slate-50 dark:bg-slate-950 pt-24 pb-20 px-4 md:px-8"
    >
      {/* Header */}
      <div className="max-w-7xl mx-auto">
        <button 
          onClick={onBack}
          className="flex items-center gap-2 text-slate-500 hover:text-primary transition-colors mb-8"
        >
          <ArrowLeft size={20} />
          Back to Browse
        </button>

        <div className="flex items-center gap-6 mb-12">
            <div className="relative">
                 {user.avatar ? (
                    <img src={user.avatar} alt={user.name} className="w-24 h-24 rounded-full border-4 border-primary shadow-xl shadow-primary/20" />
                ) : (
                    <div className="w-24 h-24 rounded-full bg-primary flex items-center justify-center text-white text-3xl font-bold border-4 border-primary-400">
                        {user.name.charAt(0)}
                    </div>
                )}
            </div>
            <div>
                <h1 className="text-4xl font-bold text-slate-900 dark:text-white mb-1">{user.name}</h1>
                <p className="text-slate-500 dark:text-slate-400">{user.email}</p>
                <div className="flex items-center gap-4 text-slate-400 text-sm mt-2">
                    <span className="font-medium text-primary">{user.playlists.length} Playlists</span>
                    <span className="w-1 h-1 rounded-full bg-slate-300 dark:bg-slate-600"></span>
                    <span className="font-medium text-primary">{user.history.length} Watched</span>
                </div>
            </div>
        </div>

        {/* Watch History Section */}
        <div className="mb-16">
            <div className="flex items-center justify-between border-b border-slate-200 dark:border-white/10 pb-4 mb-6">
                <div className="flex items-center gap-2">
                    <History className="text-primary" size={24} />
                    <h2 className="text-2xl font-bold text-slate-900 dark:text-white">Watch History</h2>
                </div>
                {user.history.length > 0 && (
                    <button 
                        onClick={() => {
                            if (window.confirm('Clear all watch history?')) {
                                clearHistory();
                            }
                        }}
                        className="text-xs text-slate-400 hover:text-red-500 transition-colors"
                    >
                        Clear History
                    </button>
                )}
            </div>
            
            {user.history.length > 0 ? (
                <MovieRow 
                    title="" 
                    movies={user.history} 
                    onSelectMovie={onSelectMovie} 
                />
            ) : (
                <div className="text-center py-12 bg-slate-100 dark:bg-white/5 rounded-2xl border border-dashed border-slate-300 dark:border-white/10">
                    <PlayCircle size={40} className="mx-auto text-slate-300 dark:text-slate-600 mb-3" />
                    <p className="text-slate-500 dark:text-slate-400">You haven't watched anything yet.</p>
                </div>
            )}
        </div>

        {/* Playlists Section */}
        <div className="space-y-12">
            <h2 className="text-2xl font-bold text-slate-900 dark:text-white border-b border-slate-200 dark:border-white/10 pb-4">My Playlists</h2>
            
            {user.playlists.length === 0 ? (
                <div className="text-center py-20 bg-slate-100 dark:bg-white/5 rounded-2xl border border-dashed border-slate-300 dark:border-white/10">
                    <p className="text-slate-600 dark:text-slate-400 text-lg">You haven't created any playlists yet.</p>
                    <p className="text-slate-400 text-sm mt-2">Start adding movies from their description page!</p>
                </div>
            ) : (
                <div className="space-y-10">
                    {user.playlists.map(playlist => (
                        <div key={playlist.id} className="relative group/playlist">
                            <div className="flex items-center justify-between px-4 md:px-12 mb-[-20px]">
                                 <div className="flex items-center gap-4">
                                     <h3 className="text-xl font-semibold text-primary">{playlist.name}</h3>
                                     <span className="text-xs text-slate-400 dark:text-slate-500 bg-slate-200 dark:bg-white/5 px-2 py-0.5 rounded-full">{playlist.movies.length} items</span>
                                 </div>
                                 <button 
                                    onClick={() => {
                                        if (window.confirm(`Delete playlist "${playlist.name}"?`)) {
                                            deletePlaylist(playlist.id);
                                        }
                                    }}
                                    className="p-2 text-slate-300 hover:text-red-500 transition-colors opacity-0 group-hover/playlist:opacity-100"
                                    title="Delete Playlist"
                                 >
                                    <Trash2 size={18} />
                                 </button>
                            </div>
                            
                            {playlist.movies.length > 0 ? (
                                <MovieRow 
                                    title="" 
                                    movies={playlist.movies} 
                                    onSelectMovie={onSelectMovie} 
                                />
                            ) : (
                                <div className="px-12 py-8 text-slate-400 italic text-sm">
                                    No movies in this playlist yet.
                                </div>
                            )}
                        </div>
                    ))}
                </div>
            )}
        </div>
      </div>
    </motion.div>
  );
};

export default ProfileView;