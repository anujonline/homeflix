package com.homeflix.app.data.repositories;

import com.homeflix.app.data.repositories.entities.MovieDatabase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<MovieDatabase, Long> {
    MovieDatabase findByIdentifier(String identifier);

    List<MovieDatabase> findByActiveOrderByNameAsc(boolean active);

    MovieDatabase findByName(String name);


}
