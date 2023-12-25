package com.homeflix.app.views.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeflix.app.data.models.VideoData;
import com.homeflix.app.data.models.VideoDataWrapper;
import com.homeflix.app.data.service.tmdb.TMDBService;
import com.vaadin.flow.component.page.WebStorage;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class MaintainHistory {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final AtomicReference<VideoDataWrapper> VIDEO_DATA_WRAPPER = new AtomicReference<>();
    private static final Map<Integer, Supplier<VideoDataWrapper>> WRAPPER_MAP = new TreeMap<>();

    static {
        VIDEO_DATA_WRAPPER.set(new VideoDataWrapper("Recently watched", new ArrayList<>()));
    }

    private final TMDBService service;

    public MaintainHistory(TMDBService service) {
        this.service = service;
        WRAPPER_MAP.put(1, service::trendingMovies);
        WRAPPER_MAP.put(2, service::popularMovies);
        WRAPPER_MAP.put(3, service::homeflixFavMovies);
        WRAPPER_MAP.put(4, service::homeflixFavTv);
        WRAPPER_MAP.put(5, service::topRatedTVSeries);
    }

    private void addRecentlyWatchedMovie(VideoData movie) {
        // Ensure the list contains a maximum of 10 elements
        var watchedMovies = VIDEO_DATA_WRAPPER.get().videoData();
        if (!watchedMovies.contains(movie)) {
            // Ensure the list contains a maximum of 10 elements
            if (watchedMovies.size() >= 10) {
                watchedMovies.remove(9); // Remove the oldest movie (10th element)
            }
            watchedMovies.add(0, movie); // Add the new movie at the beginning, shifting existing elements to the right
        }
    }

    public void reference(Consumer<Collection<VideoDataWrapper>> videoDataWrapperListFunction) {

        WebStorage.getItem(WebStorage.Storage.LOCAL_STORAGE, "watched", s -> {
            if (StringUtils.isNoneEmpty()) {
                try {
                    var localStorage = OBJECT_MAPPER.readValue(s, new TypeReference<List<VideoData>>() {
                    });
                    WRAPPER_MAP.put(0, () -> {
                        var videoDataWrapper = new VideoDataWrapper("Recently Watched", localStorage);
                        VIDEO_DATA_WRAPPER.set(videoDataWrapper);
                        return videoDataWrapper;
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            videoDataWrapperListFunction.accept(WRAPPER_MAP.values().parallelStream().map(Supplier::get).toList());
        });
    }

    @SneakyThrows
    public void addToList(VideoData videoData) {
        addRecentlyWatchedMovie(videoData);
        var vd = VIDEO_DATA_WRAPPER.get().videoData();
        WebStorage.setItem(WebStorage.Storage.LOCAL_STORAGE, "watched", OBJECT_MAPPER.writeValueAsString(vd));

    }

    public void clearHistory() {
        VIDEO_DATA_WRAPPER.get().videoData().clear();
        WebStorage.clear(WebStorage.Storage.LOCAL_STORAGE);
    }

    public Function<String, Boolean> checkAvailability() {
        return service::checkAvailability;
    }
}
