package io.spoud.jsonschemademo.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;

@Getter
@Setter
@ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", defaultImpl = BookModel.class)
@JsonSubTypes({
})
public class BookModel extends MediaEvent {
    String title;
    String author;
    int year;
    String genre;
    String isbn;

    public BookModel() {
        setType("book");
    }

    public BookModel(String title, String author, int year, String genre, String isbn) {
        setType("book");
        this.title = title;
        this.author = author;
        this.year = year;
        this.genre = genre;
        this.isbn = isbn;
    }
}
