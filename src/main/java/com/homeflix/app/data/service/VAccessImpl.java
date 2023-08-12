package com.homeflix.app.data.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VAccessImpl implements VAccess {
    private final MovieRepository movieRepository;

    @Override
    public List<VideoFile> videoList() {
        var movieDatabases = movieRepository.findByActiveOrderByNameAsc(true);
        return movieDatabases
                .stream()
                .map(movieDatabase -> new VideoFile()
                        .setIdentifier(movieDatabase.getIdentifier())
                        .setPosterLink(movieDatabase.getPosterLink())
                        .setName(movieDatabase.getName())
                        .setFullPath(movieDatabase.getLink().toString())).toList();

    }
    @Override
    public VideoFile getVideoFile(String identifier) {
        var movieDatabase = movieRepository.findByIdentifier(identifier);
        return new VideoFile().setFullPath(movieDatabase.getLink().toString());
    }
}
