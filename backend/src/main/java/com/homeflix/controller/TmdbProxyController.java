package com.homeflix.controller;

import com.homeflix.service.TmdbService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Proxies TMDB so the Bearer token never leaves the server.
 * Frontend calls /api/* instead of https://api.themoviedb.org/3.
 * No auth / DB required in this temporary mode (localStorage only).
 */
@RestController
@RequestMapping("/api")
public class TmdbProxyController {

    private final TmdbService tmdbService;

    public TmdbProxyController(TmdbService tmdbService) {
        this.tmdbService = tmdbService;
    }

    @GetMapping(value = "/health", produces = "application/json")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("{\"status\":\"ok\"}");
    }

    @GetMapping(value = "/trending", produces = "application/json")
    public ResponseEntity<String> trending() {
        return ResponseEntity.ok(tmdbService.trending());
    }

    // /api/movies/popular  and /api/tv/popular via ?type=movie|tv — keep compat with frontend
    @GetMapping(value = "/{type}/popular", produces = "application/json")
    public ResponseEntity<String> popular(@PathVariable String type) {
        if (!type.equals("movie") && !type.equals("tv")) {
            return ResponseEntity.badRequest().body("{\"error\":\"type must be movie or tv\"}");
        }
        return ResponseEntity.ok(tmdbService.popular(type));
    }

    @GetMapping(value = "/movie/top_rated", produces = "application/json")
    public ResponseEntity<String> topRatedMovies() {
        return ResponseEntity.ok(tmdbService.topRatedMovies());
    }

    @GetMapping(value = "/tv/top_rated", produces = "application/json")
    public ResponseEntity<String> topRatedTv() {
        return ResponseEntity.ok(tmdbService.topRatedTv());
    }

    @GetMapping(value = "/{type}/{id:\\d+}", produces = "application/json")
    public ResponseEntity<String> details(@PathVariable String type, @PathVariable String id) {
        if (!type.equals("movie") && !type.equals("tv")) {
            return ResponseEntity.badRequest().body("{\"error\":\"type must be movie or tv\"}");
        }
        return ResponseEntity.ok(tmdbService.details(type, id));
    }

    @GetMapping(value = "/search", produces = "application/json")
    public ResponseEntity<String> search(@RequestParam String query) {
        return ResponseEntity.ok(tmdbService.search(query));
    }

    @GetMapping(value = "/genres/movie", produces = "application/json")
    public ResponseEntity<String> genresMovie() {
        return ResponseEntity.ok(tmdbService.genresMovie());
    }

    @GetMapping(value = "/genres/tv", produces = "application/json")
    public ResponseEntity<String> genresTv() {
        return ResponseEntity.ok(tmdbService.genresTv());
    }
}
