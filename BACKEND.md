# Homeflix Backend Architecture Specification

This document outlines the architecture, database schema, and API requirements for migrating the Homeflix frontend to a robust, production-ready backend.

## 1. Tech Stack
- **Framework**: Spring Boot 3.x (Java 21+)
- **Security**: Spring Security 6 with JWT (JSON Web Tokens)
- **Database**: PostgreSQL 15+
- **Persistence**: Spring Data JPA / Hibernate
- **API Documentation**: SpringDoc OpenAPI (Swagger UI)
- **Build Tool**: Maven/Gradle

## 2. Database Schema (PostgreSQL)

### Users Table
| Column | Type | Constraints |
| :--- | :--- | :--- |
| id | UUID | PRIMARY KEY |
| name | VARCHAR(100) | NOT NULL |
| email | VARCHAR(255) | UNIQUE, NOT NULL |
| password_hash | VARCHAR(255) | NOT NULL |
| avatar_url | TEXT | |
| created_at | TIMESTAMP | DEFAULT NOW() |

### Playlists Table
| Column | Type | Constraints |
| :--- | :--- | :--- |
| id | UUID | PRIMARY KEY |
| user_id | UUID | FOREIGN KEY (users.id) ON DELETE CASCADE |
| name | VARCHAR(100) | NOT NULL |
| created_at | TIMESTAMP | DEFAULT NOW() |

### Playlist_Movies (Mapping Table)
| Column | Type | Constraints |
| :--- | :--- | :--- |
| playlist_id | UUID | FOREIGN KEY (playlists.id) ON DELETE CASCADE |
| tmdb_id | INTEGER | NOT NULL |
| media_type | VARCHAR(10) | CHECK (media_type IN ('movie', 'tv')) |
| added_at | TIMESTAMP | DEFAULT NOW() |
| PRIMARY KEY | (playlist_id, tmdb_id) | |

### Watch_History Table
| Column | Type | Constraints |
| :--- | :--- | :--- |
| user_id | UUID | FOREIGN KEY (users.id) ON DELETE CASCADE |
| tmdb_id | INTEGER | NOT NULL |
| media_type | VARCHAR(10) | NOT NULL |
| watched_at | TIMESTAMP | DEFAULT NOW() |
| PRIMARY KEY | (user_id, tmdb_id) | |

## 3. Security & Authorization
### JWT Implementation
- **Authentication**: Stateless session management.
- **Header**: `Authorization: Bearer <JWT_TOKEN>`
- **Claims**: User ID, Email, Role (USER/ADMIN).
- **Expiry**: 24 hours (configurable).

### Spring Security Configuration
- Password Hashing: `BCryptPasswordEncoder`.
- CORS Configuration: Restrict to specific frontend domains.
- CSRF: Disabled for stateless JWT usage.
- Exception Handling: Custom `AuthenticationEntryPoint` for 401/403 errors.

## 4. API Endpoints

### Auth Controller
- `POST /api/auth/signup`: Registers a new user.
- `POST /api/auth/login`: Authenticates user and returns JWT.
- `POST /api/auth/logout`: (Optional) Invalidate token on server-side blacklist.

### User/Profile Controller
- `GET /api/user/me`: Retrieves current user profile and stats.
- `PUT /api/user/me`: Updates avatar or name.

### Content Controller (Proxying TMDB)
- `GET /api/movies/trending`: Backend proxies TMDB calls to hide API keys from the client.
- `GET /api/movies/search?q=...`: Cached search results.

### Library Controller
- `GET /api/library/history`: Paginated watch history.
- `POST /api/library/history`: Adds a movie to history (upsert logic to move to top).
- `DELETE /api/library/history`: Clears user history.
- `GET /api/library/playlists`: Lists all user playlists.
- `POST /api/library/playlists`: Creates a new playlist.
- `POST /api/library/playlists/{id}/add`: Adds TMDB content to a playlist.
- `DELETE /api/library/playlists/{id}/movies/{movieId}`: Removes content.

## 5. Security Hardening & Improvements
1. **API Key Concealment**: All TMDB and Gemini API keys moved to server-side environment variables. Frontend never sees the raw keys.
2. **Rate Limiting**: Implementation of Bucket4j or Spring Cloud Gateway to prevent API abuse.
3. **Input Validation**: Use `@Valid` and JSR-303 annotations to sanitize all incoming user data.
4. **Global Exception Handler**: Standardized JSON error responses (status, timestamp, message).
5. **Database Indexing**: Indexes on `user_id` and `tmdb_id` for fast history and playlist lookups.
6. **Gemini Integration**: The backend will host the Chat API, allowing for better context management and preventing prompt injection by sanitizing inputs before sending to Google GenAI.

## 6. Deployment Strategy
- **Containerization**: Dockerfile for the Spring Boot application.
- **Environment**: PostgreSQL managed instance (AWS RDS / Supabase).
- **CI/CD**: GitHub Actions to build JAR and push to Registry.
