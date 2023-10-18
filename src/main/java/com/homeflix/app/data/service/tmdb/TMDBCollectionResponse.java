package com.homeflix.app.data.service.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

record TMDBCollectionResponse(@JsonProperty("results") List<TMDBElement> results) {
}
