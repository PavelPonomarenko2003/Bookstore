package unit.service;

import entity.StockEntity;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.interfaces.StockRepository;
import service.impl.StockServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class StockServiceImplTest {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockServiceImpl stockService;

    private final Long bookId = 1L;

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
    void updateQuantityMustCallRepository() {
        Integer amount = 10;

        stockService.updateQuantity(bookId, amount);

        verify(stockRepository, times(1)).updateBooksQuantity(bookId, amount);
    }

    @Test
    void getBookQuantityMustReturnCorrectValue() {
        Integer expectedQuantity = 50;
        when(stockRepository.getQuantity(bookId)).thenReturn(expectedQuantity);

        Integer actualQuantity = stockService.getBookQuantity(bookId);

        assertEquals(expectedQuantity, actualQuantity);
        verify(stockRepository).getQuantity(bookId);
    }

    @Test
    void doWeHaveThatBooksInStockMustReturnTrueWhenWeHaveThatBooks() {
        Integer requestedAmount = 5;
        when(stockRepository.doWeHaveThatBooksInStock(bookId, requestedAmount)).thenReturn(true);

        boolean result = stockService.doWeHaveThatBooksInStock(bookId, requestedAmount);

        assertTrue(result);
        verify(stockRepository).doWeHaveThatBooksInStock(bookId, requestedAmount);
    }

    @Test
    void removeStockDataMustInvokeDeleteInRepository() {
        stockService.removeStockData(bookId);

        verify(stockRepository).deleteBook(bookId);
    }

    @Test
    void mustReturnListOfBooks() {
        List<StockEntity> mockList = Arrays.asList(new StockEntity(), new StockEntity());
        when(stockRepository.findAll()).thenReturn(mockList);

        List<StockEntity> result = stockService.findAllBooks();

        assertEquals(2, result.size());
        verify(stockRepository).findAll();
    }
}