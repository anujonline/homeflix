package com.homeflix.app.data.service.tmdb;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.homeflix.app.data.models.VideoData;
import com.homeflix.app.data.models.VideoDataWrapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class TMDBService {
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();
    private static final String AUTH_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI0NzQzYjM2YmU2NWNmZWUzZWE5NzlkZmM5ZTIwZDY4YSIsInN1YiI6IjY0ZDcyNThkZjQ5NWVlMDI5NDJmYWRhMyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.x4EMfvuP8Qd8ab9CGx_UmihVOJIfSQyOuDL3kuFB2w8";
    private static final HttpHeaders HTTP_HEADERS = new HttpHeaders();
    private static final HttpEntity<String> HTTP_ENTITY = new HttpEntity<>(HTTP_HEADERS);

    static {
        HTTP_HEADERS.set("Authorization", AUTH_TOKEN);
    }

    private final LoadingCache<Request, List<VideoData>> cache;

    public TMDBService() {
        var loader = new CacheLoader<Request, List<VideoData>>() {
            @Override
            public List<VideoData> load(Request key) {
                return callTMDB(key);
            }
        };

        this.cache = CacheBuilder.newBuilder().expireAfterAccess(Duration.of(6L, ChronoUnit.HOURS)).build(loader);

    }

    private static VideoData getVideoData(TMDBElement r, String title, String type) {
        return new VideoData(r.id(), r.poster_path(), title, type, r.overview(), r.voteAverage(), r.releaseDate());
    }

    private static List<VideoData> getData(TMDBCollectionResponse REST_TEMPLATE, String tv) {
        return REST_TEMPLATE.results().stream().map(r -> getVideoData(r, r.original_name(), tv)).toList();
    }

    public VideoDataWrapper popularMovies() {
        return new VideoDataWrapper("Popular Movies", cache.getUnchecked(new Request("https://api.themoviedb.org/3/movie/popular?language=en-US&page=1", "movie")));
    }

    private List<VideoData> callTMDB(Request request) {
        try {
            return REST_TEMPLATE.exchange(request.url(), HttpMethod.GET, HTTP_ENTITY, TMDBCollectionResponse.class).getBody().results().stream().map(r -> getVideoData(r, r.original_title(), request.type())).toList();
        }
        catch (Exception e){
            log.error(request.toString(), e);
            throw e;
        }
    }

    public VideoDataWrapper trendingMovies() {
        return new VideoDataWrapper("Trending Movies", cache.getUnchecked(new Request("https://api.themoviedb.org/3/trending/movie/day?language=en-US", "movie")));
    }

    public VideoDataWrapper similarMovies(Long tmdbId, String type){
        return new VideoDataWrapper("Similar Movies", cache.getUnchecked(new Request("https://api.themoviedb.org/3/%s/%s/recommendations?language=en-US&page=1".formatted(type,tmdbId),type)));
    }

    public VideoDataWrapper topRatedTVSeries() {
        return new VideoDataWrapper("Top Rated TV Series", cache.getUnchecked(new Request("https://api.themoviedb.org/3/tv/popular?language=en-US&page=1", "tv")));
    }

    public VideoDataWrapper onTheAir() {
        return new VideoDataWrapper("On the Air", cache.getUnchecked(new Request("https://api.themoviedb.org/3/tv/on_the_air?language=en-US&page=1", "tv")));
    }

    public VideoDataWrapper homeflixFavMovies() {
        return new VideoDataWrapper("Homeflix's favourite movies",
                cache.getUnchecked(new Request("https://api.themoviedb.org/3/account/20288329/watchlist/movies?language=en-US&page=1&sort_by=created_at.asc", "movie")));
    }
    public VideoDataWrapper homeflixFavTv() {
        return new VideoDataWrapper("Homeflix's favourite Series",
                cache.getUnchecked(new Request("https://api.themoviedb.org/3/account/20288329/watchlist/tv?language=en-US&page=1&sort_by=created_at.asc", "tv")));
    }

    @SneakyThrows
    public List<VideoData> search(String query) {

        var movieLookup = CompletableFuture.supplyAsync(() -> REST_TEMPLATE.exchange("https://api.themoviedb.org/3/search/movie?query=%s&include_adult=false&language=en-US&page=1".formatted(query), HttpMethod.GET, HTTP_ENTITY, TMDBCollectionResponse.class).getBody());
        var tvLookup = CompletableFuture.supplyAsync(() -> REST_TEMPLATE.exchange("https://api.themoviedb.org/3/search/tv?query=%s&include_adult=false&language=en-US&page=1".formatted(query), HttpMethod.GET, HTTP_ENTITY, TMDBCollectionResponse.class).getBody());
        var movieList = movieLookup.thenApply(TMDBCollectionResponse -> TMDBCollectionResponse.results().stream().map(r -> getVideoData(r, r.original_title(), "movie")).toList());
        var tvList = tvLookup.thenApply(TMDBCollectionResponse -> getData(TMDBCollectionResponse, "tv"));

        return movieList.thenCombine(tvList, (videoData, videoData2) -> {
            var finalList = new ArrayList<VideoData>();
            finalList.addAll(videoData);
            finalList.addAll(videoData2);
            return finalList;
        }).join().stream().filter(videoData -> StringUtils.isNoneEmpty(videoData.poster())).toList();
    }


    public Boolean checkAvailability(String url) {
        return true;
    }

    record Request(String url, String type) {
    }
}
