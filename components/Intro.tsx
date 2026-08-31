import React, { useEffect, useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Sparkles, Spotlight, Image as ImageIcon } from 'lucide-react';

interface IntroProps {
  onComplete: () => void;
  movies?: any[];
  animationType?: string; // Allow external control of animation type
}

// Animation type definitions
export type AnimationType = 'spotlight' | 'montage' | 'poster';

export const ALL_ANIMATIONS: AnimationType[] = ['spotlight', 'montage', 'poster'];

export const INTRO_CONFIG = {
  spotlight: { name: 'Spotlight', icon: Spotlight, duration: 3500 },
  montage: { name: 'Montage', icon: ImageIcon, duration: 4000 },
  poster: { name: 'Poster', icon: ImageIcon, duration: 3800 },
};

const Intro: React.FC<IntroProps> = ({ onComplete, movies = [], animationType }) => {
  const [stage, setStage] = useState<'initial' | 'revealing' | 'logo' | 'tagline' | 'complete'>('initial');
  
  // Use provided animationType or random selection
  const selectedAnimation = animationType || ALL_ANIMATIONS[Math.floor(Math.random() * ALL_ANIMATIONS.length)];

  useEffect(() => {
    const timeline = async () => {
      await new Promise(resolve => setTimeout(resolve, 100));
      setStage('revealing');
      
      const config = INTRO_CONFIG[selectedAnimation as AnimationType];
      await new Promise(resolve => setTimeout(resolve, config.duration * 0.3));
      setStage('logo');
      
      await new Promise(resolve => setTimeout(resolve, config.duration * 0.4));
      setStage('tagline');
      
      await new Promise(resolve => setTimeout(resolve, config.duration * 0.3));
      setStage('complete');
      
      await new Promise(resolve => setTimeout(resolve, 700));
      onComplete();
    };

    timeline();
  }, [selectedAnimation, onComplete]);

  // Animation components
  const renderBackgroundEffect = () => {
    switch (animationType) {
      case 'spotlight':
        return (
          <AnimatePresence>
            {stage === 'revealing' && (
              <motion.div
                initial={{ scale: 0, opacity: 0 }}
                animate={{ scale: 1, opacity: [0, 0.8, 0.5, 0.9, 0.4] }}
                exit={{ opacity: 0 }}
                transition={{ duration: 1.5, ease: 'easeOut' }}
                className="absolute inset-0 flex items-center justify-center pointer-events-none"
              >
                <div className="relative w-[800px] h-[800px]">
                  <div className="absolute inset-0 bg-gradient-radial from-amber-300 via-amber-500 to-transparent rounded-full blur-3xl" />
                  <motion.div
                    animate={{ rotate: 360 }}
                    transition={{ duration: 6, repeat: Infinity, ease: 'linear' }}
                    className="absolute inset-0 border-2 border-amber-400/30 rounded-full scale-75"
                  />
                  <motion.div
                    animate={{ rotate: -360 }}
                    transition={{ duration: 10, repeat: Infinity, ease: 'linear' }}
                    className="absolute inset-0 border border-amber-300/20 rounded-full scale-50"
                  />
                </div>
              </motion.div>
            )}
          </AnimatePresence>
        );

      case 'montage':
        // Full-screen trending backdrops — 2x3 grid on mobile (portrait fill), 3x2 on larger screens
        const montageImages = movies.slice(0, 6).map(m => m.backdropUrl || m.imageUrl);
        return (
          <div className="absolute inset-0">
            <div className="absolute inset-0 grid grid-cols-2 grid-rows-3 md:grid-cols-3 md:grid-rows-2">
              {montageImages.map((img, i) => (
                <motion.div
                  key={i}
                  initial={{ opacity: 0, scale: 1.25 }}
                  animate={stage === 'revealing' || stage === 'logo' || stage === 'tagline' || stage === 'complete' ? {
                    opacity: 1,
                    scale: 1
                  } : {}}
                  transition={{
                    duration: 1.4,
                    delay: i * 0.12,
                    ease: 'easeOut'
                  }}
                  className="relative overflow-hidden"
                >
                  <img
                    src={img}
                    alt=""
                    className="absolute inset-0 w-full h-full object-cover"
                  />
                </motion.div>
              ))}
            </div>

            {/* Darkening scrim so logo/text stay readable, images remain visible edge-to-edge */}
            <div className="absolute inset-0 bg-gradient-to-t from-black/90 via-black/40 to-black/50" />

            {/* Overlay text */}
            <motion.div
              initial={{ opacity: 0 }}
              animate={stage === 'revealing' ? { opacity: 0.25 } : {}}
              transition={{ delay: 0.8, duration: 1 }}
              className="absolute inset-0 flex items-center justify-center pointer-events-none"
            >
              <div className="text-6xl md:text-8xl font-display font-black text-white/20 tracking-wider">
                TRENDING
              </div>
            </motion.div>
          </div>
        );

      case 'poster':
        // Full-screen fanned posters — tall 2/3 posters spread edge-to-edge, full screen height
        const posterImages = movies.slice(0, 5).map(m => m.imageUrl);
        return (
          <div className="absolute inset-0 overflow-hidden bg-black">
            <div className="absolute inset-0 flex items-stretch justify-center">
              {posterImages.map((img, i) => {
                // Spread posters across the full width, slight fan rotation
                const offset = i - (posterImages.length - 1) / 2;
                return (
                  <motion.div
                    key={i}
                    initial={{ opacity: 0, y: '100%', rotate: 0 }}
                    animate={stage === 'revealing' || stage === 'logo' || stage === 'tagline' || stage === 'complete' ? {
                      opacity: 1,
                      y: 0,
                      rotate: offset * 6
                    } : {}}
                    transition={{
                      duration: 1.1,
                      delay: 0.15 + Math.abs(offset) * 0.1,
                      ease: 'easeOut'
                    }}
                    className="relative h-full flex-1 min-w-0 -mx-3 overflow-hidden shadow-2xl"
                    style={{ zIndex: 10 - Math.abs(offset) }}
                  >
                    <img src={img} alt="" className="absolute inset-0 w-full h-full object-cover" />
                    <div className="absolute inset-0 bg-gradient-to-t from-black/70 via-black/10 to-black/40" />
                  </motion.div>
                );
              })}
            </div>

            {/* Extra scrim behind center content for readability */}
            <div className="absolute inset-0 bg-gradient-to-t from-black/85 via-transparent to-black/40" />
          </div>
        );

      default:
        return null;
    }
  };

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      transition={{ duration: 0.6, ease: 'easeInOut' }}
      className="fixed inset-0 z-[300] bg-gradient-to-br from-slate-950 via-slate-900 to-black overflow-hidden"
    >
      {/* Dynamic background effect */}
      {renderBackgroundEffect()}

      {/* Main content - PERFECTLY CENTERED */}
      <div className="absolute inset-0 flex items-center justify-center px-4">
        <div className="text-center w-full max-w-2xl mx-auto">
          {/* Logo Container - CENTERED */}
          <motion.div
            initial={{ scale: 0.5, opacity: 0 }}
            animate={stage === 'logo' ? { scale: 1, opacity: 1 } : {}}
            transition={{ duration: 0.8, ease: 'easeOut' }}
            className="flex justify-center mb-10"
          >
            {/* Enhanced glow effect */}
            <motion.div
              animate={stage === 'logo' ? { 
                scale: [1, 1.3, 1],
                opacity: [0.6, 1, 0.6],
              } : {}}
              transition={{ duration: 2, repeat: Infinity, ease: 'easeInOut' }}
              className="absolute inset-0 flex items-center justify-center"
            >
              <div className="w-32 h-32 md:w-40 md:h-40 bg-gradient-radial from-amber-300 via-amber-500 to-amber-700 rounded-3xl blur-2xl" />
            </motion.div>

            {/* Main H Icon - PERFECTLY CENTERED */}
            <motion.div
              initial={{ rotateY: -90, rotateX: 45, opacity: 0, scale: 0.8 }}
              animate={stage === 'logo' ? { 
                rotateY: 0, 
                rotateX: 0, 
                opacity: 1, 
                scale: 1 
              } : {}}
              transition={{ 
                duration: 0.8, 
                delay: 0.2, 
                ease: 'easeOut',
                rotateY: { type: 'spring', stiffness: 100, damping: 15 }
              }}
              className="relative w-28 h-28 md:w-36 md:h-36 rounded-2xl bg-gradient-to-br from-amber-200 via-amber-400 to-amber-600 grid place-items-center text-black font-black text-5xl md:text-6xl shadow-2xl shadow-amber-500/60 border-3 border-amber-300/60"
              style={{ 
                transformStyle: 'preserve-3d',
                perspective: 1000
              }}
            >
              H
            </motion.div>

            {/* Enhanced sparkle effects */}
            <AnimatePresence>
              {stage === 'logo' && (
                <>
                  {[...Array(8)].map((_, i) => (
                    <motion.div
                      key={i}
                      initial={{ scale: 0, opacity: 0 }}
                      animate={{ 
                        scale: [0, 1.2, 0],
                        opacity: [0, 1, 0],
                        x: Math.cos(i * 45 * Math.PI / 180) * 100,
                        y: Math.sin(i * 45 * Math.PI / 180) * 100
                      }}
                      transition={{ duration: 1.8, delay: i * 0.08, ease: 'easeOut' }}
                      className="absolute top-1/2 left-1/2 w-3 h-3"
                    >
                      <Sparkles size={20} className="text-amber-300 fill-amber-300" />
                    </motion.div>
                  ))}
                </>
              )}
            </AnimatePresence>
          </motion.div>

          {/* Brand Name - PERFECTLY CENTERED */}
          <motion.div
            initial={{ opacity: 0, y: 30, filter: 'blur(10px)' }}
            animate={stage === 'logo' ? { 
              opacity: 1, 
              y: 0, 
              filter: 'blur(0px)' 
            } : {}}
            transition={{ duration: 0.8, delay: 0.4, ease: 'easeOut' }}
            className="flex justify-center"
          >
            <h1 className="font-display font-bold text-6xl md:text-8xl tracking-tight text-white drop-shadow-2xl">
              HOMEFLIX
            </h1>
          </motion.div>

          {/* Tagline - PERFECTLY CENTERED */}
          <motion.div
            initial={{ opacity: 0, y: 15 }}
            animate={stage === 'tagline' ? { opacity: 1, y: 0 } : {}}
            transition={{ duration: 0.6 }}
            className="flex justify-center mt-6"
          >
            <div className="flex items-center justify-center gap-4">
              <motion.div 
                initial={{ width: 0 }}
                animate={stage === 'tagline' ? { width: '60px' } : {}}
                transition={{ duration: 0.6, delay: 0.2 }}
                className="h-px bg-gradient-to-r from-transparent to-amber-400" 
              />
              <p className="text-xs md:text-sm tracking-[0.25em] font-bold text-amber-400/95 uppercase">
                Cinema Experience
              </p>
              <motion.div 
                initial={{ width: 0 }}
                animate={stage === 'tagline' ? { width: '60px' } : {}}
                transition={{ duration: 0.6, delay: 0.2 }}
                className="h-px bg-gradient-to-l from-transparent to-amber-400" 
              />
            </div>
          </motion.div>

          {/* Dynamic icon based on animation type */}
          <motion.div
            initial={{ opacity: 0, scale: 0.8 }}
            animate={stage === 'tagline' ? { opacity: 1, scale: 1 } : {}}
            transition={{ duration: 0.5, delay: 0.4 }}
            className="flex justify-center mt-10"
          >
            <div className="flex items-center justify-center gap-3 text-amber-400/80 text-sm">
              {selectedAnimation === 'spotlight' && <Sparkles size={18} />}
              {selectedAnimation === 'montage' && <ImageIcon size={18} />}
              {selectedAnimation === 'poster' && <ImageIcon size={18} />}
              <span className="font-medium">Your cinematic journey begins...</span>
            </div>
          </motion.div>
        </div>
      </div>

      {/* Bottom accent - PERFECTLY CENTERED */}
      <motion.div
        initial={{ scaleX: 0 }}
        animate={stage === 'tagline' ? { scaleX: 1 } : {}}
        transition={{ duration: 0.8, ease: 'easeInOut' }}
        className="absolute bottom-0 left-1/2 -translate-x-1/2 w-1/2 h-1 bg-gradient-to-r from-transparent via-amber-500 to-transparent"
      />

      {/* Corner accents - PERFECTLY POSITIONED */}
      <motion.div
        initial={{ opacity: 0, scale: 0.8 }}
        animate={{ opacity: 0.4, scale: 1 }}
        transition={{ delay: 1.2, duration: 0.8 }}
        className="absolute top-8 left-8 w-24 h-24 border-l-2 border-t-2 border-amber-500/40 rounded-tl-2xl"
      />
      <motion.div
        initial={{ opacity: 0, scale: 0.8 }}
        animate={{ opacity: 0.4, scale: 1 }}
        transition={{ delay: 1.2, duration: 0.8 }}
        className="absolute bottom-8 right-8 w-24 h-24 border-r-2 border-b-2 border-amber-500/40 rounded-br-2xl"
      />
    </motion.div>
  );
};

export default Intro;