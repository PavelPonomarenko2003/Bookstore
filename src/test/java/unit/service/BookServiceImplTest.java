package unit.service;

import entity.BookEntity;
import exception.BookNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.interfaces.BookRepository;
import service.impl.BookServiceImpl;
import service.interfaces.StockService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * All testing will be divided on three steps Arrange/Act/Assert
 * Arrange - подготовка
 * Act - действие
 * Assert - проверка результата
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testing class for Book-Service")
public class BookServiceImplTest {

    // Interface mocking because testing logic, not realization
    @Mock
    private BookRepository bookRepository;

    @Mock
    private StockService stockService;

    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeAll
    public static void notificationAboutTestStarting(TestInfo info) {
        System.out.println(info.getDisplayName() + " Get started!");
    }

    @AfterAll
    public static void notificationAboutTestFinishing(TestInfo info) {
        System.out.println(info.getDisplayName() + " Get finished!");
    }

    @BeforeEach
    public void notificationAboutMethodStarted(TestInfo info) {
        System.out.println(info.getDisplayName() + " Get started!");
    }

    @AfterEach
    public void notificationAboutMethodFinished(TestInfo info) {
        System.out.println(info.getDisplayName() + " Get finished!");
    }

    @Test
    @DisplayName("Adding book into catalog, if that is new book.")
    void mustSaveBookToCatalogWhenBookIsNew(){
        // Arrange
        Long bookId = 1L;
        String title = "Testing Book";
        double price = 100.0;
        int quantity = 5;

        BookEntity expectedBook = new BookEntity();
        expectedBook.setTitle(title);
        expectedBook.setPrice(BigDecimal.valueOf(price));

        // if we haven't found such book in our catalog, then we need to add that
        when(bookRepository.findByTitle(title)).thenReturn(Optional.empty());

        // Act
        bookService.addBookIntoCatalogAndUpdateStock(title, price,quantity);

        // Assert
        // checking what data was saved
        // equlas/hashcode redefined
        verify(bookRepository).save(expectedBook);
    }

    @Test
    @DisplayName("Updating quantity of certain book in the stock.")
    void mustUpdateTheStockChangeQuantityOfCertainBook(){
        // Arrange
        Long bookId = 1L;
        int quantity = 5;
        String title = "Testing Book";
        double price = 100.0;

        BookEntity existingBook = new BookEntity();
        existingBook.setId(bookId);

        // we have found the book in catalog
        when(bookRepository.findByTitle(title)).thenReturn(Optional.of(existingBook));

        // Act
        bookService.addBookIntoCatalogAndUpdateStock(title, price, quantity);

        // Assert
        verify(stockService).updateQuantity(bookId, quantity);
    }

    @Test
    @DisplayName("Deleting book from catalog and stock respectively.")
    void deleteMustWorkCorrectly() {
        // Arrange
        Long bookId = 1L;
        BookEntity book = new BookEntity();
        book.setId(bookId);

        // book was found
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // Act
        bookService.deleteBookFromCatalogAndStock(bookId);

        // Assert
        // check deleting from catalog and stock
        verify(bookRepository).delete(bookId);
        verify(stockService).removeStockData(bookId);
    }

    @Test
    @DisplayName("Search all books in the catalog")
    void mustReturnAllBooksFromRepository() {
        // Arrange
        BookEntity book1 = new BookEntity();
        book1.setTitle("Testing book1");

        BookEntity book2 = new BookEntity();
        book2.setTitle("Testing book2");

        List<BookEntity> mockListOfBooks = List.of(book1, book2);

        // when someone calls findAll -> return mockListOfBooks
        when(bookRepository.findAll()).thenReturn(mockListOfBooks);

        // Act
        List<BookEntity> result = bookService.findAllBooksInCatalog();

        // Assert
        assertNotNull(result, "shouldn't be null");
        assertEquals(2, result.size(), "size of list has to be 2");
        assertEquals("Testing book1", result.get(0).getTitle(),
                "Names has to be similar");

        verify(bookRepository, times(1)).findAll();
    }

    // using functional interface Executable
    @Test
    @DisplayName("Exception 'BookNotFoundException' throwing if book wasn't found.")
    void deleteMustThrowExceptionWhenNotFound() {
        // Arrange
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // Act
        assertThrows(BookNotFoundException.class, () -> {
            bookService.deleteBookFromCatalogAndStock(bookId);
        });

        // Assert
        // methods wasn't call
        verify(bookRepository, never()).delete(anyLong());
        verify(stockService, never()).removeStockData(anyLong());
    }
}