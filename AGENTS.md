# AGENTS.md

## Commands
- `npm install` — install (no lockfile drift check needed)
- `npm run dev` — Vite dev server on `0.0.0.0:3000` (proxies `/api` → `http://localhost:8080` via `vite.config.ts:9`)
- `npm run build` — `vite build` → `dist/`; `npm run preview` — serve built `dist/`
- Backend: `mvn -f backend/pom.xml package -DskipTests` → `backend/target/*.jar` (Java 21, Spring Boot 3.3.5 via `backend/pom.xml:8`)
- Docker (single image API+SPA): `docker build -t homeflix:test .` then `docker run -p 8080:8080 -e TMDB_API_KEY=<token> homeflix:test` (`Dockerfile:1` multi-stage `node:20` → `maven:3.9` → `eclipse-temurin:21-jre`)
- No test/lint/typecheck/formatter scripts exist (`package.json:6`). Verify with `npx tsc --noEmit`.

## Architecture
- Single Docker image: Spring Boot serves SPA from `classpath:/static` with SPA fallback in `backend/src/main/java/com/homeflix/config/WebConfig.java:18` (returns `index.html` for non-`/api` routes, enabling client-side routing).
- Frontend entry: `index.tsx:12` → `AuthProvider` (`context/AuthContext.tsx`) → `App.tsx`. `App.tsx:24` owns view routing via `ViewType = 'home'|'movies'|'series'|'popular'|'profile'` + search overlay — no `react-router`. Uses `window.history.pushState`/`replaceState`/`popstate` for deep links: `/watch/:type/:id/:slug`, `/:type/:id/:slug`, `/{movies,series,popular,profile}`, `?q=` (search).
- Backend proxy: `backend/src/main/java/com/homeflix/controller/TmdbProxyController.java:13` proxies TMDB (`/api/trending`, `/{type}/popular`, `/movie/top_rated`, `/tv/top_rated`, `/{type}/{id:\d+}`, `/search?query=`, `/genres/*`) with Caffeine 5-min cache (`backend/src/main/resources/application.yml:28` `maximumSize=500,expireAfterWrite=300s`). `TmdbService.java:32` injects Bearer token server-side.
- `backend/src/main/java/com/homeflix/controller/FallbackController.java` — handles `POST/HEAD/OPTIONS /api/health` and `POST /api/{event,send,collect,track}` with 204 to suppress 405 WARN from trackers/uptime checkers.
- `services/tmdbService.ts` — frontend wrapper around `/api` via `VITE_API_URL` (empty → same origin). Retains client-side 5-min `apiCache` but source of truth is backend Caffeine cache.
- `components/` — flat, one file per component (Navbar, Hero, MovieRow/Card, SearchResults, VideoPlayer, MovieDetails, AuthModal, AddToPlaylistModal, ProfileView).
- `types.ts:2` — canonical `Movie`/`User`/`Playlist` types.
- DB/Auth **temporarily disabled**: no `spring-boot-starter-data-jpa`/Postgres (not in `backend/pom.xml`). `AuthContext` is localStorage-only (`homeflix_session`, `homeflix_users`). `BACKEND.md` Postgres/JWT spec is deferred/async; `README.md:18` Gemini setup is stale.

## Conventions & Gotchas
- Path alias `@/*` → repo root (`tsconfig.json:21`, `vite.config.ts:17`).
- Tailwind is via CDN + inline config in `index.html:7-38`, not a build step. No `tailwind.config.js`. Theme defaults to `dark` (`homeflix_theme` in localStorage; `index.html` syncs `documentElement.classList`).
- `index.html:79` has `importmap` pointing to `aistudiocdn.com` for AI Studio deploys — local dev resolves from `node_modules` via Vite instead.
- `vite.config.ts:9` proxy requires backend on `8080` for `npm run dev`; override with `VITE_API_URL=http://localhost:8080` if backend runs elsewhere.
- `tsconfig.json:26` has `allowImportingTsExtensions: true` — imports may use `.ts` extensions.
- `services/mockData.ts` is deprecated (TMDB is source of truth).
- Environment: `PORT` → `server.port=${PORT:8080}` (`application.yml:2`). `SELF_PING_ENABLED`/`SELF_PING_URL`/`RENDER_EXTERNAL_URL` control `KeepAliveScheduler.java:26` pinging `/api/health` every `homeflix.self-ping.interval-ms` (actual default `10000`ms in `application.yml:16`; comment says 14 min but code/default is 10s, scheduler `initialDelay=60000`). Disabled when URL blank or `SELF_PING_ENABLED=false`.
- TMDB key: hardcoded fallback in `backend/src/main/resources/application.yml:5` (`tmdb.api-key: eyJhbG...`), not `${TMDB_API_KEY}` env placeholder. `TmdbService.java:36` logs `TMDB_API_KEY not set` and throws `503` only if blank. Frontend `services/tmdbService.ts` must never contain token — frontend `dist/` must not contain `eyJhbG...` (backend jar will, by design).
- GenAI disabled: `components/GeminiChat.tsx` and `services/geminiService.ts` removed; `@google/genai` and `GEMINI_API_KEY` wiring no longer used (`.env.local` placeholder is stale; `BACKEND.md:94` Gemini ref is stale).
- Build artifacts: `dist/` and `*.local` are gitignored; `backend/target/` and `backend/src/main/resources/static/` are **not** gitignored but must not be committed — `static/` is generated at Docker build time (`Dockerfile:19` copies `dist` into `src/main/resources/static`).
