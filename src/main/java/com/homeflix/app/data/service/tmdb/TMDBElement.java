package com.homeflix.app.data.service.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TMDBElement(@JsonProperty("poster_path") String poster_path, @JsonProperty("id") Long id,
                          @JsonProperty("original_title") String original_title,
                          @JsonProperty("first_air_date") String first_air_date, @JsonProperty("name") String name,
                          @JsonProperty("original_name") String original_name,
                          @JsonProperty("overview") String overview,
                          @JsonProperty("vote_average") String voteAverage,
                          @JsonProperty("release_date") String releaseDate
                          ) {
}
