package io.spoud.jsonschemademo.api;

import io.spoud.jsonschemademo.model.BookModel;
import io.spoud.jsonschemademo.service.MediaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class BookResource {
    @Autowired
    private MediaService bookService;

    @PostMapping("/api/book")
    public String addBook(@RequestBody BookModel bookModel) {
        return bookService.addBook(bookModel);
    }

    @GetMapping("/api/book")
    public List<BookModel> getAllBooks() {
        return bookService.getBooks();
    }

    @GetMapping("/api/book/{id}")
    public BookModel getBook(@PathVariable String id) {
        return bookService.getBook(id);
    }

    @PutMapping("/api/book/{id}")
    public ResponseEntity updateBook(@PathVariable String id, @RequestBody BookModel bookModel) {
        try {
            bookService.updateBook(id, bookModel);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/api/book/{id}")
    public void deleteBook(@PathVariable String id) {
        bookService.deleteBook(id);
    }
}
