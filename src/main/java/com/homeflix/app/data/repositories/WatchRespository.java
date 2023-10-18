package com.homeflix.app.data.repositories;

import com.homeflix.app.data.repositories.entities.WatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;


public interface WatchRespository extends JpaRepository<WatchHistory, Long> {
}
