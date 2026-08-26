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

    // /api/discover/movie?genre=28&year=2024&sort=popularity.desc&page=1
    @GetMapping(value = "/discover/{type}", produces = "application/json")
    public ResponseEntity<String> discover(@PathVariable String type,
                                           @RequestParam(required = false) String genre,
                                           @RequestParam(required = false) String year,
                                           @RequestParam(required = false) String sort,
                                           @RequestParam(defaultValue = "1") String page) {
        if (!type.equals("movie") && !type.equals("tv")) {
            return ResponseEntity.badRequest().body("{\"error\":\"type must be movie or tv\"}");
        }
        if (genre != null && !genre.isBlank() && !genre.matches("^\\d+((,|\\|)\\d+)*$")) {
            return ResponseEntity.badRequest().body("{\"error\":\"genre must be numeric id(s)\"}");
        }
        if (year != null && !year.isBlank() && !year.matches("^\\d{4}$")) {
            return ResponseEntity.badRequest().body("{\"error\":\"year must be YYYY\"}");
        }
        return ResponseEntity.ok(tmdbService.discover(type, genre, year, sort, page));
    }

    @GetMapping(value = "/{type}/{id:\\d+}/watch/providers", produces = "application/json")
    public ResponseEntity<String> watchProviders(@PathVariable String type, @PathVariable String id,
                                                 @RequestParam(defaultValue = "US") String watch_region) {
        if (!type.equals("movie") && !type.equals("tv")) {
            return ResponseEntity.badRequest().body("{\"error\":\"type must be movie or tv\"}");
        }
        return ResponseEntity.ok(tmdbService.watchProviders(type, id, watch_region));
    }

    @GetMapping(value = "/{type}/{id:\\d+}/videos", produces = "application/json")
    public ResponseEntity<String> videos(@PathVariable String type, @PathVariable String id) {
        if (!type.equals("movie") && !type.equals("tv")) {
            return ResponseEntity.badRequest().body("{\"error\":\"type must be movie or tv\"}");
        }
        return ResponseEntity.ok(tmdbService.videos(type, id));
    }

    @GetMapping(value = "/{type}/{id:\\d+}/credits", produces = "application/json")
    public ResponseEntity<String> credits(@PathVariable String type, @PathVariable String id) {
        if (!type.equals("movie") && !type.equals("tv")) {
            return ResponseEntity.badRequest().body("{\"error\":\"type must be movie or tv\"}");
        }
        return ResponseEntity.ok(tmdbService.credits(type, id));
    }

    @GetMapping(value = "/{type}/{id:\\d+}/similar", produces = "application/json")
    public ResponseEntity<String> similar(@PathVariable String type, @PathVariable String id) {
        if (!type.equals("movie") && !type.equals("tv")) {
            return ResponseEntity.badRequest().body("{\"error\":\"type must be movie or tv\"}");
        }
        return ResponseEntity.ok(tmdbService.similar(type, id));
    }

    @GetMapping(value = "/{type}/{id:\\d+}/recommendations", produces = "application/json")
    public ResponseEntity<String> recommendations(@PathVariable String type, @PathVariable String id) {
        if (!type.equals("movie") && !type.equals("tv")) {
            return ResponseEntity.badRequest().body("{\"error\":\"type must be movie or tv\"}");
        }
        return ResponseEntity.ok(tmdbService.recommendations(type, id));
    }

    @GetMapping(value = "/tv/{id:\\d+}/season/{season:\\d+}", produces = "application/json")
    public ResponseEntity<String> season(@PathVariable String id, @PathVariable String season) {
        return ResponseEntity.ok(tmdbService.season(id, season));
    }

    @GetMapping(value = "/configuration", produces = "application/json")
    public ResponseEntity<String> configuration() {
        return ResponseEntity.ok(tmdbService.configuration());
    }

    @GetMapping(value = "/search/person", produces = "application/json")
    public ResponseEntity<String> searchPerson(@RequestParam String query) {
        return ResponseEntity.ok(tmdbService.searchPerson(query));
    }
}
