package io.spoud.jsonschemademo.service;

import io.spoud.jsonschemademo.config.KafkaConfiguration;
import io.spoud.jsonschemademo.model.BookModel;
import io.spoud.jsonschemademo.model.MediaEvent;
import io.spoud.jsonschemademo.model.MovieModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
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
@KafkaListener(topics = KafkaConfiguration.MEDIA_TOPIC_NAME, containerFactory = "mediaListenerContainerFactory")
public class MediaService {
    private final ConcurrentHashMap<String, MovieModel> movies = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, BookModel> books = new ConcurrentHashMap<>();

    @Autowired
    KafkaTemplate<String, MediaEvent> mediaEventKafkaTemplate;

    @Value(KafkaConfiguration.MEDIA_TOPIC_NAME)
    private String mediaTopicName;

    @KafkaHandler
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

    @KafkaHandler
    void onBook(@Payload(required = false) BookModel model,
                 @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        if (model == null) {
            log.info("Received tombstone for book with key {}", key);
            books.remove(key);
        } else {
            log.info("Create/update book with key {}: {}", key, model);
            books.put(key, model);
        }
    }

    @KafkaHandler(isDefault = true)
    void onOther(@Payload(required = false) Object model,
                 @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.warn("Received unknown message with key {}: {}", key, model);
    }

    public String addMovie(MovieModel movie) {
        log.info("Adding movie: {}", movie);
        String id = UUID.randomUUID().toString();
        mediaEventKafkaTemplate.send(mediaTopicName, id, movie);
        return id;
    }

    public String addBook(BookModel book) {
        log.info("Adding book: {}", book);
        String id = UUID.randomUUID().toString();
        mediaEventKafkaTemplate.send(mediaTopicName, id, book);
        return id;
    }

    public List<MovieModel> getMovies() {
        log.debug("Getting all movies");
        return new ArrayList<>(movies.values());
    }

    public List<BookModel> getBooks() {
        log.debug("Getting all books");
        return new ArrayList<>(books.values());
    }

    public MovieModel getMovie(String id) {
        log.debug("Getting movie with id: {}", id);
        return movies.get(id);
    }

    public BookModel getBook(String id) {
        log.debug("Getting book with id: {}", id);
        return books.get(id);
    }

    public void deleteMovie(String id) {
        log.info("Deleting movie with id: {}", id);
        mediaEventKafkaTemplate.send(mediaTopicName, id, null);
    }

    public void deleteBook(String id) {
        log.info("Deleting book with id: {}", id);
        mediaEventKafkaTemplate.send(mediaTopicName, id, null);
    }

    public void updateMovie(String id, MovieModel movie) {
        log.info("Updating movie with id: {} to {}", id, movie);
        if (!movies.containsKey(id)) {
            throw new IllegalArgumentException("Movie with id " + id + " does not exist");
        }
        movies.put(id, movie);
        mediaEventKafkaTemplate.send(mediaTopicName, id, movie);
    }

    public void updateBook(String id, BookModel book) {
        log.info("Updating book with id: {} to {}", id, book);
        if (!books.containsKey(id)) {
            throw new IllegalArgumentException("Book with id " + id + " does not exist");
        }
        books.put(id, book);
        mediaEventKafkaTemplate.send(mediaTopicName, id, book);
    }
}
