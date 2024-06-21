package io.spoud.jsonschemademo.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;

@Getter
@Setter
@ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", defaultImpl = MovieModel.class)
@JsonSubTypes({
})
public class MovieModel extends MediaEvent {
    String title;
    int year;
    String genre;

    public MovieModel() {
        setType("movie");
    }

    public MovieModel(String type, String title, int year, String genre) {
        setType("movie");
        this.title = title;
        this.year = year;
        this.genre = genre;
    }
}
