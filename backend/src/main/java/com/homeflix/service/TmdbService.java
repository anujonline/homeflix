package com.homeflix.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class TmdbService {

    private static final Logger log = LoggerFactory.getLogger(TmdbService.class);

    private final RestTemplate restTemplate;
    private final String tmdbBaseUrl;
    private final String apiKey;

    // simple in-memory fallback for genres merging — actual cache handled by Spring Cache (caffeine 5min)
    public TmdbService(RestTemplate restTemplate,
                       @Value("${tmdb.base-url}") String tmdbBaseUrl,
                       @Value("${tmdb.api-key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.tmdbBaseUrl = tmdbBaseUrl;
        this.apiKey = apiKey;
        log.info("TMDB API KEY {}", apiKey);
        log.info("BASE URL {} ", tmdbBaseUrl);
    }

    private HttpHeaders headers() {
        HttpHeaders h = new HttpHeaders();
        if (apiKey != null && !apiKey.isBlank()) {
            h.set("Authorization", "Bearer " + apiKey);
        }
        h.set("Content-Type", "application/json");
        return h;
    }

    private String forward(String path, Map<String, String> queryParams) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("TMDB_API_KEY not set - returning error for {}", path);
            throw new IllegalStateException("TMDB not configured");
        }
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(tmdbBaseUrl + path);
        if (queryParams != null) {
            queryParams.forEach(builder::queryParam);
        }
        String url = builder.build().toUriString();
        log.debug("Proxy TMDB {} ", url);
        HttpEntity<Void> entity = new HttpEntity<>(headers());
        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return resp.getBody();
    }

    @Cacheable(value = "tmdb", key = "#path + #queryParams.toString()")
    public String cachedForward(String path, Map<String, String> queryParams) {
        return forward(path, queryParams);
    }

    public String trending() {
        return cachedForward("/trending/all/week", Map.of());
    }

    public String popular(String type) {
        return cachedForward("/" + type + "/popular", Map.of());
    }

    public String topRatedMovies() {
        return cachedForward("/movie/top_rated", Map.of());
    }

    public String topRatedTv() {
        return cachedForward("/tv/top_rated", Map.of());
    }

    public String details(String type, String id) {
        String endpoint = type.equals("tv") ? "tv" : "movie";
        return cachedForward("/" + endpoint + "/" + id, Map.of());
    }

    public String search(String query) {
        return forward("/search/multi", Map.of(
                "query", query,
                "include_adult", "false",
                "language", "en-US",
                "page", "1"
        ));
    }

    public String genresMovie() {
        return cachedForward("/genre/movie/list", Map.of());
    }

    public String genresTv() {
        return cachedForward("/genre/tv/list", Map.of());
    }
}
