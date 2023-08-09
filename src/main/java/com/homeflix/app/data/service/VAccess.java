package com.homeflix.app.data.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface VAccess {

    @Cacheable("video-list")
    List<VideoFile> videoList();

    @Cacheable("display-name")
    String getDisplayName(String identifier);

    VideoFile getVideoFile(String identifier);

    ResponseEntity<?> getVideoStream(String fileName, String range);
}
