import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X, Plus, ListMusic, Check } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { Movie } from '../types';

interface AddToPlaylistModalProps {
  isOpen: boolean;
  onClose: () => void;
  movie: Movie | null;
}

const AddToPlaylistModal: React.FC<AddToPlaylistModalProps> = ({ isOpen, onClose, movie }) => {
  const { user, createPlaylist, addToPlaylist } = useAuth();
  const [newPlaylistName, setNewPlaylistName] = useState('');
  const [isCreating, setIsCreating] = useState(false);

  if (!isOpen || !movie || !user) return null;

  const handleCreate = (e: React.FormEvent) => {
    e.preventDefault();
    if (newPlaylistName.trim()) {
      createPlaylist(newPlaylistName);
      setNewPlaylistName('');
      setIsCreating(false);
    }
  };

  const handleSelect = (playlistId: string) => {
    addToPlaylist(playlistId, movie);
    onClose();
  };

  return (
    <div className="fixed inset-0 z-[200] flex items-center justify-center p-4">
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        exit={{ opacity: 0 }}
        onClick={onClose}
        className="absolute inset-0 bg-black/80 backdrop-blur-sm"
      />

      <motion.div
        initial={{ opacity: 0, scale: 0.95, y: 20 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        exit={{ opacity: 0, scale: 0.95, y: 20 }}
        className="relative w-full max-w-md bg-white dark:bg-slate-900 border border-slate-200 dark:border-white/10 rounded-2xl shadow-2xl overflow-hidden"
      >
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-slate-400 hover:text-primary transition-colors"
        >
          <X size={20} />
        </button>

        <div className="p-6">
          <h2 className="text-xl font-bold text-slate-900 dark:text-white mb-1">Add to Playlist</h2>
          <p className="text-slate-500 dark:text-slate-400 text-sm mb-6">Select a playlist for <span className="text-primary font-bold">{movie.title}</span></p>

          <div className="space-y-2 mb-6 max-h-[300px] overflow-y-auto pr-2">
            {user.playlists.length === 0 && !isCreating && (
              <p className="text-slate-400 text-center py-4 text-sm italic">No playlists found. Create one below!</p>
            )}
            
            {user.playlists.map((playlist) => {
                const isAlreadyIn = playlist.movies.some(m => m.id === movie.id);
                return (
                    <button
                        key={playlist.id}
                        onClick={() => !isAlreadyIn && handleSelect(playlist.id)}
                        disabled={isAlreadyIn}
                        className={`w-full flex items-center justify-between p-3 rounded-xl transition-all ${
                            isAlreadyIn 
                            ? 'bg-slate-50 dark:bg-slate-800/50 text-slate-400 cursor-default' 
                            : 'bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 text-slate-900 dark:text-white'
                        }`}
                    >
                        <div className="flex items-center gap-3">
                            <ListMusic size={18} />
                            <span className="font-medium">{playlist.name}</span>
                        </div>
                        {isAlreadyIn ? (
                            <span className="text-xs flex items-center gap-1 text-primary"><Check size={12}/> Added</span>
                        ) : (
                            <span className="text-xs text-slate-400">{playlist.movies.length} movies</span>
                        )}
                    </button>
                );
            })}
          </div>

          {!isCreating ? (
            <button
              onClick={() => setIsCreating(true)}
              className="w-full py-3 border border-dashed border-slate-300 dark:border-slate-600 rounded-xl text-slate-500 hover:text-primary hover:border-primary transition-all flex items-center justify-center gap-2 text-sm"
            >
              <Plus size={16} />
              Create New Playlist
            </button>
          ) : (
            <form onSubmit={handleCreate} className="relative">
              <input
                autoFocus
                type="text"
                placeholder="Playlist name..."
                value={newPlaylistName}
                onChange={(e) => setNewPlaylistName(e.target.value)}
                className="w-full bg-slate-100 dark:bg-slate-800 border border-primary rounded-xl py-3 pl-4 pr-12 text-slate-900 dark:text-white focus:outline-none text-sm"
              />
              <button
                type="submit"
                disabled={!newPlaylistName.trim()}
                className="absolute right-2 top-1/2 -translate-y-1/2 p-1.5 bg-primary text-white rounded-lg hover:bg-primary-500 disabled:opacity-50 transition-colors"
              >
                <Plus size={16} />
              </button>
              <button
                 type="button"
                 onClick={() => setIsCreating(false)}
                 className="absolute -top-8 right-0 text-xs text-slate-500 hover:text-primary"
              >
                 Cancel
              </button>
            </form>
          )}
        </div>
      </motion.div>
    </div>
  );
};

export default AddToPlaylistModal;