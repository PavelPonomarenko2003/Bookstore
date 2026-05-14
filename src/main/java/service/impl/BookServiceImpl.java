package service.impl;

import entity.BookEntity;
import exception.BookNotFoundException;
import exception.IncorrectInputException;
import repository.interfaces.BookRepository;
import service.interfaces.BookService;
import service.interfaces.StockService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final StockService stockService;

    public BookServiceImpl(BookRepository bookRepository, StockService stockService) {
        this.bookRepository = bookRepository;
        this.stockService = stockService;
    }

    @Override
    public void addBookIntoCatalogAndUpdateStock(String title, double price, int initialQuantity) {

        if (title == null || title.isBlank() || price < 0 || initialQuantity < 0) {
            throw new IncorrectInputException();
        }

        Optional<BookEntity> existingBook = bookRepository.findByTitle(title);

        if (existingBook.isPresent()) {
            Long bookId = existingBook.get().getId();
            stockService.updateQuantity(bookId, initialQuantity);
            System.out.println("The book: " + title + " already exists. Quantity updated.");
        } else {
            BookEntity newBook = new BookEntity();
            newBook.setTitle(title);
            newBook.setPrice(BigDecimal.valueOf(price));

            bookRepository.save(newBook);

            stockService.updateQuantity(newBook.getId(), initialQuantity);
            System.out.println("The book '" + title + "' has been added to catalog and stock!");
        }
    }

    @Override
    public void deleteBookFromCatalogAndStock(Long id) {

        bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException());

        bookRepository.delete(id);
        stockService.removeStockData(id);

        System.out.println("Success: Book with ID " + id + " removed.");
    }

    @Override
    public List<BookEntity> findAllBooksInCatalog() {
        return bookRepository.findAll();
    }
}

