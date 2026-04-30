package service.impl;

import entity.BookEntity;
import exception.BookNotFoundException;
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
        Optional<BookEntity> existingBook = bookRepository.findByTitle(title);

        if (existingBook.isPresent()) {
            Long bookId = existingBook.get().getId();

            stockService.updateQuantity(bookId, initialQuantity);
            System.out.println(
                    "The book: " + title +
                            " already exists. Quantity increased by: " + initialQuantity);
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
    public void deleteBookFromCatalog(Long id) {
        Optional<BookEntity> bookOptional = bookRepository.findById(id);

        if (bookOptional.isEmpty()) {
            throw new BookNotFoundException();
        }

        bookRepository.delete(id);

        stockService.removeStockData(id);

        System.out.println(
                "Success: Book with ID " + id + " has been removed from catalog and stock.");
    }

    @Override
    public List<BookEntity> findAllBooksInCatalog() {
        return bookRepository.findAll();
    }
}
