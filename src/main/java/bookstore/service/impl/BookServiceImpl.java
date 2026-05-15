package bookstore.service.impl;

import bookstore.entity.BookEntity;
import bookstore.exception.BookNotFoundException;
import bookstore.exception.IncorrectInputException;
import bookstore.repository.interfaces.BookRepository;
import bookstore.service.interfaces.BookService;
import bookstore.service.interfaces.StockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final StockService stockService;

    public BookServiceImpl(BookRepository bookRepository, StockService stockService) {
        this.bookRepository = bookRepository;
        this.stockService = stockService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ, timeout = 5)
    public void addBookIntoCatalogAndUpdateStock(String title, double price, int initialQuantity) {

        if (title == null || title.isBlank() || price < 0 || initialQuantity < 0) {
            throw new IncorrectInputException();
        }

        Optional<BookEntity> existingBook = bookRepository.findByTitleIgnoreCase(title);

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
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ, timeout = 5)
    public void deleteBookFromCatalogAndStock(Long id) {

        bookRepository.findById(id)
                .orElseThrow(BookNotFoundException::new);

        bookRepository.deleteById(id);
        stockService.removeStockData(id);

        System.out.println("Success: Book with ID " + id + " removed.");
    }

    @Override
    public List<BookEntity> findAllBooksInCatalog() {
        return bookRepository.findAll();
    }
}