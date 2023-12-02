package com.homeflix.app.data.service;

import com.homeflix.app.data.repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
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
        return new VideoFile().setName(movieDatabase.getName()).setFullPath(movieDatabase.getLink().toString());
    }
}
