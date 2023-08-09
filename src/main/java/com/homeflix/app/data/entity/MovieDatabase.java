package com.homeflix.app.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;

@Entity
@Getter
@Setter

public class MovieDatabase extends AbstractEntity {

    private String name;
    @Column(length = 100000)
    private URI link;
    @Column(length = 100000)
    private String identifier;
    @Column(length = 100000)
    private String posterLink;

    private boolean active;
}
