package com.homeflix.app.data.entity;

import org.springframework.data.jpa.repository.JpaRepository;


public interface WatchRespository extends JpaRepository<WatchHistory, Long> {
}
