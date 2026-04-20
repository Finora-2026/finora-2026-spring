package com.bellamyphan.finora_2026_spring.mongodb.controller;

import com.bellamyphan.finora_2026_spring.mongodb.entity.Movie;
import com.bellamyphan.finora_2026_spring.mongodb.service.MovieService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/movies")
@AllArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public ResponseEntity<List<Movie>> allMovies() {
        return new ResponseEntity<>(movieService.getAllMovies(), HttpStatus.OK);
    }

    @GetMapping("/{imdbId}")
    public ResponseEntity<Optional<Movie>> findMovieByImdbId(@PathVariable String imdbId) {
        return new ResponseEntity<>(movieService.findMovieByImdbId(imdbId), HttpStatus.OK);
    }
}
