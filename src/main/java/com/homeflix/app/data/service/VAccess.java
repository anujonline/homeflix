package com.homeflix.app.data.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface VAccess {

    List<VideoFile> videoList();

    VideoFile getVideoFile(String identifier);

}
