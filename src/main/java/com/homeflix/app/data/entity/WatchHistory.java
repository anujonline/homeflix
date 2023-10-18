package com.homeflix.app.data.entity;

import com.homeflix.app.data.entity.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class WatchHistory extends AbstractEntity {

    private String address;
    @Column(length = 1000) private String imdbId;
    private LocalDateTime time;
}
