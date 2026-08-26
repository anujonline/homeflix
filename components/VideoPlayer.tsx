import React, { useState, useEffect, useRef } from 'react';
import { X, AlertCircle, Shield, ExternalLink } from 'lucide-react';
import { motion } from 'framer-motion';
import { Movie } from '../types';

interface VideoPlayerProps {
  onClose: () => void;
  movie: Movie;
}

type ServerKey = 'primary' | 'alt' | 'backup';

const getSrc = (server: ServerKey, type: string, id: number) => {
  switch (server) {
    case 'alt':
      return `https://vidsrc.to/embed/${type}/${id}`;
    case 'backup':
      return `https://player.videasy.net/${type}/${id}`;
    case 'primary':
    default:
      return `https://vidsrc-embed.su/embed/${type}/${id}`;
  }
};

const VideoPlayer: React.FC<VideoPlayerProps> = ({ onClose, movie }) => {
  const [server, setServer] = useState<ServerKey>('primary');
  const [armored, setArmored] = useState(true); // overlay that blocks first-click ad
  const type = movie.mediaType === 'tv' ? 'tv' : 'movie';
  const src = getSrc(server, type, movie.id);
  const iframeRef = useRef<HTMLIFrameElement>(null);

  useEffect(() => {
    document.body.style.overflow = 'hidden';
    return () => { document.body.style.overflow = 'unset'; };
  }, []);

  useEffect(() => {
    const onKey = (e: KeyboardEvent) => e.key === 'Escape' && onClose();
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [onClose]);

  // When server changes, re-arm the click shield for 3s
  useEffect(() => { setArmored(true); const t = setTimeout(() => setArmored(false), 3500); return () => clearTimeout(t); }, [server]);

  // Light popup defense without sandbox: block window.open from parent
  // (iframe's own window.open is cross-origin, but many ad scripts use window.top.open)
  useEffect(() => {
    const origOpen = window.open;
    const block = (...args: any[]) => { console.warn('[Homeflix] blocked popup', args[0]); return null as any; };
    // @ts-ignore
    window.open = block as any;
    return () => { window.open = origOpen; };
  }, []);

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      className="fixed inset-0 z-[200] bg-black flex flex-col"
    >
      {/* top bar */}
      <div className="flex items-center justify-between gap-3 px-4 md:px-6 py-3 bg-[#0a0a0a] border-b border-white/10 shrink-0">
        <div className="flex items-center gap-3 min-w-0">
          <span className="hidden md:inline-flex items-center gap-1.5 text-xs font-bold px-2.5 py-1 rounded-full bg-amber-500/15 text-amber-400 border border-amber-500/20">
            <Shield size={12} /> {armored ? 'Shield ON' : 'Shield idle'}
          </span>
          <span className="text-sm font-medium text-white truncate max-w-[42vw] md:max-w-none">{movie.title}</span>
          <span className="hidden md:inline text-xs text-white/50">{type.toUpperCase()} • {movie.year}</span>
        </div>

        <div className="flex items-center gap-2 shrink-0">
          <div className="hidden md:flex items-center gap-1 p-1 rounded-full bg-white/10 border border-white/10">
            {(['primary','alt','backup'] as ServerKey[]).map(k => (
              <button key={k} onClick={() => setServer(k)} className={`px-3 py-1 rounded-full text-xs font-semibold capitalize ${server===k ? 'bg-white text-black' : 'text-white/70 hover:text-white'}`}>{k==='primary'?'Server 1':k==='alt'?'Server 2':'Server 3'}</button>
            ))}
          </div>
          <a href={src} target="_blank" rel="noopener noreferrer" className="hidden md:inline-flex items-center gap-1 text-xs text-white/60 hover:text-white px-2 py-1"><ExternalLink size={12} /> pop-out</a>
          <button onClick={onClose} className="w-9 h-9 grid place-items-center rounded-full bg-white/10 hover:bg-white/15 text-white border border-white/10" aria-label="Close"><X size={18} /></button>
        </div>
      </div>

      {/* player */}
      <div className="flex-1 relative bg-black overflow-hidden flex items-center justify-center">
        {/* 
          NO sandbox attribute — providers explicitly block sandboxed frames (you saw "can't be embedded in a sandboxed frame").
          Ad suppression now via:
          1) window.open block in parent (stops top-level popups)
          2) referrerPolicy no-referrer (cuts tracking)
          3) click-shield overlay that absorbs the first ad click for 3.5s after load/server switch
          In-video overlay ads inside cross-origin iframe can't be stripped without a backend proxy — this is the max client-side.
        */}
        <iframe
          ref={iframeRef}
          key={`${server}-${movie.id}`}
          src={src}
          className="w-full h-full border-0"
          allow="autoplay; encrypted-media; fullscreen; picture-in-picture"
          allowFullScreen
          referrerPolicy="no-referrer"
          title={`Streaming ${movie.title}`}
        />

        {/* Click shield — transparent, blocks the first click that ad networks hijack.
            Auto-hides after 3.5s, user can re-enable via shield button if preroll appears. */}
        {armored && (
          <button
            onClick={() => setArmored(false)}
            className="absolute inset-0 z-10 bg-black/0 cursor-pointer flex items-start justify-center pt-10"
            aria-label="Activate player"
          >
            <span className="px-4 py-2 rounded-full bg-black/70 backdrop-blur text-white text-xs font-bold border border-white/15 shadow-lg flex items-center gap-2">
              <Shield size={14} className="text-amber-400" /> Tap to activate player — shield expires in 3s
            </span>
          </button>
        )}

        {/* small re-arm button when idle */}
        {!armored && (
          <button onClick={() => setArmored(true)} className="absolute top-3 left-3 z-10 hidden md:inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-black/60 backdrop-blur text-white text-xs border border-white/15">
            <Shield size={12} /> Re-arm shield
          </button>
        )}

        <div className="md:hidden absolute bottom-4 left-1/2 -translate-x-1/2 flex items-center gap-1 p-1 rounded-full bg-black/70 backdrop-blur border border-white/15 z-10">
          {(['primary','alt','backup'] as ServerKey[]).map(k => (
            <button key={k} onClick={() => setServer(k)} className={`px-3 py-1.5 rounded-full text-xs font-bold capitalize ${server===k ? 'bg-white text-black' : 'text-white/80'}`}>{k}</button>
          ))}
        </div>
      </div>

      <div className="hidden md:flex items-center justify-center gap-2 px-4 py-2 bg-[#0a0a0a] border-t border-white/5 text-[11px] text-white/40">
        Sandbox removed — providers block it. Shield + window.open block stops popups. For zero ads use Brave/uBlock or a backend proxy that strips ad scripts from m3u8.
      </div>
    </motion.div>
  );
};

export default VideoPlayer;
