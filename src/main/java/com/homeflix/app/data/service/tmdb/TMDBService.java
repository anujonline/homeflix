package com.homeflix.app.data.service.tmdb;

import com.homeflix.app.data.models.VideoData;
import com.homeflix.app.data.models.VideoDataWrapper;
import lombok.SneakyThrows;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class TMDBService {
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();
    private static final String AUTH_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI0NzQzYjM2YmU2NWNmZWUzZWE5NzlkZmM5ZTIwZDY4YSIsInN1YiI6IjY0ZDcyNThkZjQ5NWVlMDI5NDJmYWRhMyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.x4EMfvuP8Qd8ab9CGx_UmihVOJIfSQyOuDL3kuFB2w8";
    private static final HttpHeaders HTTP_HEADERS = new HttpHeaders();
    private static final HttpEntity<String> HTTP_ENTITY = new HttpEntity<>(HTTP_HEADERS);

    static {
        HTTP_HEADERS.set("Authorization", AUTH_TOKEN);
    }

    private static VideoData getVideoData(TMDBElement r, String title, String type) {
        return new VideoData(r.id(), r.poster_path(), title, type);
    }

    public VideoDataWrapper popularMovies() {
        return new VideoDataWrapper("Popular Movies", REST_TEMPLATE.exchange("https://api.themoviedb.org/3/movie/popular?language=en-US&page=1", HttpMethod.GET, HTTP_ENTITY, TMDBCollectionResponse.class).getBody().results().stream().map(r -> getVideoData(r, r.original_title(), "movie")).toList());
    }

    public VideoDataWrapper trendingMovies() {
        var videoData = REST_TEMPLATE.exchange("https://api.themoviedb.org/3/trending/movie/day?language=en-US", HttpMethod.GET, HTTP_ENTITY, TMDBCollectionResponse.class).getBody().results().stream().map(r -> getVideoData(r, r.original_title(), "movie")).toList();
        return new VideoDataWrapper("Trending Movies", videoData);
    }

    public VideoDataWrapper topRatedTVSeries() {
        return new VideoDataWrapper("Top Rated TV Series", REST_TEMPLATE.exchange("https://api.themoviedb.org/3/tv/popular?language=en-US&page=1", HttpMethod.GET, HTTP_ENTITY, TMDBCollectionResponse.class).getBody().results().stream().map(r -> getVideoData(r, r.original_name(), "tv")).toList());
    }

    public VideoDataWrapper onTheAir() {
        return new VideoDataWrapper("On the Air", REST_TEMPLATE.exchange("https://api.themoviedb.org/3/tv/on_the_air?language=en-US&page=1", HttpMethod.GET, HTTP_ENTITY, TMDBCollectionResponse.class).getBody().results().stream().map(r -> getVideoData(r, r.original_name(), "tv")).toList());
    }

    public VideoDataWrapper homeflixFavMovies() {
        return new VideoDataWrapper("Homeflix's favourite movies", REST_TEMPLATE.exchange("https://api.themoviedb.org/3/account/20288329/watchlist/movies?language=en-US&page=1&sort_by=created_at.asc", HttpMethod.GET, HTTP_ENTITY, TMDBCollectionResponse.class).getBody().results().stream().map(r -> getVideoData(r, r.original_name(), "movie")).toList());
    }

    @SneakyThrows
    public List<VideoData> search(String query) {

        var movieLookup = CompletableFuture.supplyAsync(() -> REST_TEMPLATE.exchange("https://api.themoviedb.org/3/search/movie?query=%s&include_adult=false&language=en-US&page=1".formatted(query), HttpMethod.GET, HTTP_ENTITY, TMDBCollectionResponse.class).getBody());
        var tvLookup = CompletableFuture.supplyAsync(() -> REST_TEMPLATE.exchange("https://api.themoviedb.org/3/search/tv?query=%s&include_adult=false&language=en-US&page=1".formatted(query), HttpMethod.GET, HTTP_ENTITY, TMDBCollectionResponse.class).getBody());
        var movieList = movieLookup.thenApply(TMDBCollectionResponse -> TMDBCollectionResponse.results().stream().map(r -> getVideoData(r, r.original_title(), "movie")).toList());
        var tvList = tvLookup.thenApply(TMDBCollectionResponse -> TMDBCollectionResponse.results().stream().map(r -> getVideoData(r, r.original_name(), "tv")).toList());

        return movieList.thenCombine(tvList, (videoData, videoData2) -> {
            var finalList = new ArrayList<VideoData>();
            finalList.addAll(videoData);
            finalList.addAll(videoData2);
            return finalList;
        }).join();
    }

    public VideoDataWrapper homeflixFavTv() {
        return new VideoDataWrapper("Homeflix's favourite Series", REST_TEMPLATE.exchange("https://api.themoviedb.org/3/account/20288329/watchlist/tv?language=en-US&page=1&sort_by=created_at.asc", HttpMethod.GET, HTTP_ENTITY, TMDBCollectionResponse.class).getBody().results().stream().map(r -> getVideoData(r, r.original_name(), "tv")).toList());
    }
}
