# AGENTS.md

## Commands
- `npm install` — install (no lockfile drift check needed)
- `npm run dev` — Vite dev server on `0.0.0.0:3000` (proxies `/api` → `http://localhost:8080` via `vite.config.ts:9`)
- `npm run build` — `vite build` → `dist/`
- `npm run preview` — serve built `dist/`
- Backend: `mvn -f backend/pom.xml package -DskipTests` → `backend/target/*.jar` (Java 21, Spring Boot 3.3.5)
- Docker (single image, API+SPA): `docker build -t homeflix:test .` then `docker run -p 8080:8080 -e TMDB_API_KEY=<token> homeflix:test`
- No test, lint, typecheck, or formatter scripts exist. Verify with `npx tsc --noEmit` if needed.
- Not a git repo — do not assume `git` history/hooks are available.

## Environment
- `TMDB_API_KEY` — **server-only** env var read by Spring (`backend/src/main/resources/application.yml:7`). Never hardcoded in frontend; `services/tmdbService.ts:6` calls relative `/api/*` via `VITE_API_URL` (empty → same origin). Old hardcoded Bearer in `tmdbService.ts:4` is removed — `dist/` must not contain `eyJhbG...`.
- `PORT` — Render injects `PORT`; Spring reads `server.port=${PORT:8080}`.
- `SELF_PING_ENABLED` / `SELF_PING_URL` / `RENDER_EXTERNAL_URL` — `backend/src/main/java/com/homeflix/scheduler/KeepAliveScheduler.java:12` pings `/api/health` every ~14 min (`homeflix.self-ping.interval-ms=840000`) to prevent Render free-tier sleep. Disabled when URL blank or `SELF_PING_ENABLED=false`.
- `index.html` has an `importmap` pointing to `aistudiocdn.com` for AI Studio deploys — local dev resolves from `node_modules` via Vite instead.
- GenAI disabled: `components/GeminiChat.tsx` and `services/geminiService.ts` removed; `@google/genai` dependency and `GEMINI_API_KEY` wiring are no longer used. `BACKEND.md:94` Gemini reference is stale.

## Architecture
- Vite + React 19 + TypeScript SPA + Spring Boot 3.3.5 (Java 21) in **single Docker image** (`Dockerfile:1` multi-stage: `node:20` → `maven:3.9` → `eclipse-temurin:21-jre`). Spring serves SPA from `classpath:/static` (`backend/src/main/java/com/homeflix/config/WebConfig.java:14` SPA fallback).
- Frontend entrypoints: `index.tsx:12` → `AuthProvider` (`context/AuthContext.tsx`) → `App.tsx`. `App.tsx` owns view routing via `ViewType = 'home'|'movies'|'series'|'popular'|'profile'` + search overlay — no `react-router`.
- `backend/src/main/java/com/homeflix/controller/TmdbProxyController.java:12` — proxies TMDB (`/api/trending`, `/{type}/popular`, `/movie/top_rated`, `/tv/top_rated`, `/{type}/{id:\d+}`, `/search`, `/genres/*`) with Caffeine 5-min cache (`application.yml:27`). `TomdbService.java:18` injects Bearer token server-side.
- `components/` — flat, one file per component (Navbar, Hero, MovieRow/Card, SearchResults, VideoPlayer, MovieDetails, AuthModal, AddToPlaylistModal, ProfileView).
- `services/tmdbService.ts` — frontend wrapper around `/api` (client-side 5-min `apiCache` retained, but source of truth is backend Caffeine cache).
- `types.ts:2` — canonical `Movie`/`User`/`Playlist` types.
- DB/Auth **temporarily disabled**: no `spring-boot-starter-data-jpa`/`Postgres`; `AuthContext` remains localStorage-only (`homeflix_session`, `homeflix_users`). `BACKEND.md` Postgres/JWT spec is deferred.

## Conventions & Gotchas
- Path alias `@/*` → repo root (`tsconfig.json:21`, `vite.config.ts:14`).
- Tailwind is via CDN + inline config in `index.html:7-38`, not a build step. No `tailwind.config.js`. Theme is `dark` by default (`homeflix_theme` in localStorage).
- `vite.config.ts:9` proxy is required for `npm run dev` — backend must run on `8080` or set `VITE_API_URL=http://localhost:8080` override.
- Render free tier: single container expects `TMDB_API_KEY` env set in Dashboard. No `Dockerfile` `ENV` default — fails with `503` if missing (see `TmdbService.java:32`).
- `tsconfig.json:7` has `allowImportingTsExtensions: true`.
- Build artifacts `dist/`, `backend/target/`, and `*.local` are gitignored. `backend/src/main/resources/static/` is generated at Docker build time — do not commit.
