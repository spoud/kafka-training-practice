package io.spoud.jsonschemademo.api;

import io.spoud.jsonschemademo.model.MovieModel;
import io.spoud.jsonschemademo.service.MediaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class MovieResource {
    @Autowired
    private MediaService movieService;

    @PostMapping("/api/movie")
    public String addMovie(@RequestBody MovieModel movieModel) {
        return movieService.addMovie(movieModel);
    }

    @GetMapping("/api/movie")
    public List<MovieModel> getAllMovies() {
        return movieService.getMovies();
    }

    @GetMapping("/api/movie/{id}")
    public MovieModel getMovie(@PathVariable String id) {
        return movieService.getMovie(id);
    }

    @PutMapping("/api/movie/{id}")
    public ResponseEntity updateMovie(@PathVariable String id, @RequestBody MovieModel movieModel) {
        try {
            movieService.updateMovie(id, movieModel);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/api/movie/{id}")
    public void deleteMovie(@PathVariable String id) {
        movieService.deleteMovie(id);
    }
}
