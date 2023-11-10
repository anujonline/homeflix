package com.homeflix.app.data.controllers;

import com.homeflix.app.data.repositories.MovieRepository;
import com.homeflix.app.data.repositories.WatchRespository;
import com.homeflix.app.data.repositories.entities.MovieDatabase;
import com.homeflix.app.data.repositories.entities.WatchHistory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/adm")
public class AdminController {
    private final MovieRepository movieRepository;
    private final WatchRespository watchRespository;

    public AdminController(MovieRepository movieRepository, WatchRespository watchRespository) {
        this.movieRepository = movieRepository;
        this.watchRespository = watchRespository;
    }

    private static String createIdentifier(String url) {
        var uri = URI.create(url);
        var split = uri.getPath().split("/");
        return split[2];
    }

    @PostMapping("/add-movie")
    ResponseEntity<?> addMovie(@RequestBody Movie movie) {
        saveMovie(movie.name(), movie.link(), movie.posterLink());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    List<Movie> findMovies() {
        return movieRepository.findAll().stream().map(movieDatabase -> new Movie(movieDatabase.getId(), movieDatabase.getName(), movieDatabase.getLink().toString(), movieDatabase.getPosterLink(), movieDatabase.isActive())).toList();
    }

    @PutMapping("/activate")
    @Transactional
    public ResponseEntity<?> activate(@RequestBody StatusChangeRequest statusChangeRequest) {
        var movieDatabase = movieRepository.findByName(statusChangeRequest.movieName());
        movieDatabase.setActive(true);
        movieRepository.save(movieDatabase);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/deactivate")
    @Transactional
    public ResponseEntity<?> deactivate(@RequestBody StatusChangeRequest statusChangeRequest) {
        var movieDatabase = movieRepository.findByName(statusChangeRequest.movieName());
        movieDatabase.setActive(false);
        movieRepository.save(movieDatabase);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        movieRepository.deleteById(id);
    }

    private void saveMovie(String name, String url, String posterLink) {
        MovieDatabase movie = new MovieDatabase();
        movie.setName(name);
        movie.setIdentifier(createIdentifier(url));
        movie.setLink(URI.create(url));
        movie.setPosterLink(posterLink);
        movieRepository.save(movie);
    }

    @Async
    public void addHistory(String address, String imdbID) {
        var watchHistory = new WatchHistory();
        watchHistory.setTime(LocalDateTime.now());
        watchHistory.setAddress(address);
        watchHistory.setImdbId(imdbID);
        watchRespository.save(watchHistory);
    }

    @GetMapping("/history")
    public List<WatchHistory> watchHistories() {
        return watchRespository.findAll();
    }

    record StatusChangeRequest(String movieName) {
    }

    record Movie(Long id, String name, String link, String posterLink, boolean status) {
    }
}
