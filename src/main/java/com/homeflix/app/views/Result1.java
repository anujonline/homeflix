package com.homeflix.app.views;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Result1(@JsonProperty("poster_path") String poster_path, @JsonProperty("id") Long id,
                      @JsonProperty("original_title") String original_title,
                      @JsonProperty("first_air_date") String first_air_date, @JsonProperty("name") String name,
                      @JsonProperty("original_name") String original_name) {
}
