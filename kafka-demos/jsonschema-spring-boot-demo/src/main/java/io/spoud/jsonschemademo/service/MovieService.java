package io.spoud.jsonschemademo.service;

import io.spoud.jsonschemademo.config.KafkaConfiguration;
import io.spoud.jsonschemademo.model.MovieModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class MovieService {
    private final ConcurrentHashMap<String, MovieModel> movies = new ConcurrentHashMap<>();

    @Autowired
    KafkaTemplate<String, MovieModel> movieKafkaTemplate;

    @Value(KafkaConfiguration.PROP_MOVIE_TOPIC_NAME)
    private String movieTopicName;

    @KafkaListener(topics = KafkaConfiguration.PROP_MOVIE_TOPIC_NAME,
        containerFactory = "movieListenerContainerFactory")
    void onMovie(@Payload(required = false) MovieModel model,
                 @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        if (model == null) {
            log.info("Received tombstone for movie with key {}", key);
            movies.remove(key);
        } else {
            log.info("Create/update movie with key {}: {}", key, model);
            movies.put(key, model);
        }
    }

    public String addMovie(MovieModel movie) {
        log.info("Adding movie: {}", movie);
        String id = UUID.randomUUID().toString();
        movieKafkaTemplate.send(movieTopicName, id, movie);
        return id;
    }

    public List<MovieModel> getMovies() {
        log.debug("Getting all movies");
        return new ArrayList<>(movies.values());
    }

    public MovieModel getMovie(String id) {
        log.debug("Getting movie with id: {}", id);
        return movies.get(id);
    }

    public void deleteMovie(String id) {
        log.info("Deleting movie with id: {}", id);
        movieKafkaTemplate.send(movieTopicName, id, null);
    }

    public void updateMovie(String id, MovieModel movie) {
        log.info("Updating movie with id: {} to {}", id, movie);
        if (!movies.containsKey(id)) {
            throw new IllegalArgumentException("Movie with id " + id + " does not exist");
        }
        movies.put(id, movie);
        movieKafkaTemplate.send(movieTopicName, id, movie);
    }
}
