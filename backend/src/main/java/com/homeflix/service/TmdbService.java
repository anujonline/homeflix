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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Service
public class TmdbService {

    private static final Logger log = LoggerFactory.getLogger(TmdbService.class);

    private final RestTemplate restTemplate;
    private final String tmdbBaseUrl;
    private final String apiKey;

    // simple in-memory fallback for genres merging — actual cache handled by Spring Cache (caffeine 6h, search uncached)
    public TmdbService(RestTemplate restTemplate,
                       @Value("${tmdb.base-url}") String tmdbBaseUrl,
                       @Value("${tmdb.api-key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.tmdbBaseUrl = tmdbBaseUrl;
        this.apiKey = apiKey;
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("TMDB_API_KEY not set - TMDB proxy will return 503");
        } else {
            log.info("TMDB proxy configured (baseUrl={})", tmdbBaseUrl);
        }
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

    // sort values TMDB accepts for /discover that we allow through the proxy
    public static final Set<String> ALLOWED_SORTS = Set.of(
            "popularity.desc", "vote_average.desc", "revenue.desc",
            "primary_release_date.desc", "first_air_date.desc", "title.desc", "name.desc"
    );

    private static final int MAX_PAGE = 500;

    /**
     * /discover/{movie|tv} with genre/year/sort/page filters. Conditional params go in a
     * LinkedHashMap so the cache key (path + params.toString()) is deterministic.
     */
    public String discover(String type, String genre, String year, String sort, String page) {
        String yearParam = type.equals("tv") ? "first_air_date_year" : "primary_release_year";
        Map<String, String> params = new LinkedHashMap<>();
        params.put("language", "en-US");
        params.put("include_adult", "false");
        if (genre != null && !genre.isBlank()) params.put("with_genres", genre);
        if (year != null && !year.isBlank()) params.put(yearParam, year);
        if (sort != null && ALLOWED_SORTS.contains(sort)) params.put("sort_by", sort);
        else params.put("sort_by", "popularity.desc");
        if ("vote_average.desc".equals(sort)) params.put("vote_count.gte", "200"); // avoid niche high scores
        int p = 1;
        try { p = Math.min(Math.max(Integer.parseInt(page == null ? "1" : page), 1), MAX_PAGE); } catch (NumberFormatException ignored) {}
        params.put("page", String.valueOf(p));
        return cachedForward("/discover/" + type, params);
    }

    public String watchProviders(String type, String id, String region) {
        Map<String, String> params = new LinkedHashMap<>();
        if (region == null || !region.matches("^[A-Za-z]{2}$")) region = "US";
        params.put("watch_region", region.toUpperCase());
        return cachedForward("/" + type + "/" + id + "/watch/providers", params);
    }

    public String videos(String type, String id) {
        return cachedForward("/" + type + "/" + id + "/videos", Map.of("language", "en-US"));
    }

    public String credits(String type, String id) {
        return cachedForward("/" + type + "/" + id + "/credits", Map.of());
    }

    public String similar(String type, String id) {
        return cachedForward("/" + type + "/" + id + "/similar", Map.of("language", "en-US", "page", "1"));
    }

    public String recommendations(String type, String id) {
        return cachedForward("/" + type + "/" + id + "/recommendations", Map.of("language", "en-US", "page", "1"));
    }

    public String season(String tvId, String seasonNumber) {
        return cachedForward("/tv/" + tvId + "/season/" + seasonNumber, Map.of("language", "en-US"));
    }

    public String configuration() {
        return cachedForward("/configuration", Map.of());
    }

    public String searchPerson(String query) {
        return forward("/search/person", Map.of(
                "query", query,
                "include_adult", "false",
                "language", "en-US",
                "page", "1"
        ));
    }
}
