package com.bellamyphan.finora_2026_spring.mongodb.service;

import com.bellamyphan.finora_2026_spring.mongodb.entity.Movie;
import com.bellamyphan.finora_2026_spring.mongodb.repository.MovieRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Optional<Movie> findMovieByImdbId(String imdbId) {
        return movieRepository.findMovieByImdbId(imdbId);
    }
}
