package bookstore.servlet.controller;

import bookstore.dto.BookRequestDTO;
import bookstore.dto.MessageResponse;
import bookstore.entity.BookEntity;
import bookstore.exception.ServletExceptionCustom;
import bookstore.service.interfaces.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<BookEntity>> getAllBooks() {
        List<BookEntity> books = bookService.findAllBooksInCatalog();
        return ResponseEntity.ok(books);
    }

    @PostMapping
    public ResponseEntity<MessageResponse> addBook(@RequestBody BookRequestDTO dto) {

        if (dto.getTitle() == null || dto.getTitle().isEmpty()) {
            throw new ServletExceptionCustom("Title is required!", 400);
        }

        bookService.addBookIntoCatalogAndUpdateStock(dto.getTitle(), dto.getPrice(), dto.getQuantity());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("Book added successfully!"));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteBook(@RequestParam("id") Long id) {
        bookService.deleteBookFromCatalogAndStock(id);
        return ResponseEntity.noContent().build();
    }
}
