package bookstore.service.impl;

import bookstore.entity.StockEntity;
import bookstore.repository.interfaces.StockRepository;
import bookstore.service.interfaces.StockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    public StockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    // transactionManager - to prevent Mysql/neo conflicts
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ, timeout = 5, value = "transactionManager")
    public void updateQuantity(Long bookId, Integer amount) {
        if (bookId == null) {
            throw new IllegalArgumentException("Book ID cannot be null for stock update");
        }

        stockRepository.updateBooksQuantity(bookId, amount);
        System.out.println("Stock updated for book ID: " + bookId + ". Absolute quantity set to: " + amount);
    }

    @Override
    public Integer getBookQuantity(Long bookId) {
        if (bookId == null) return 0;
        return stockRepository.getQuantity(bookId);
    }

    @Override
    public boolean doWeHaveThatBooksInStock(Long bookId, Integer requestedAmount) {
        if (requestedAmount == null || requestedAmount < 0) {
            return false;
        }
        return stockRepository.doWeHaveThatBooksInStock(bookId, requestedAmount);
    }

    // remove principle - fire and forget (don't need isolation)
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeStockData(Long bookId) {
        if (bookId != null) {
            stockRepository.deleteBook(bookId);
            System.out.println("Stock data cleared for book ID: " + bookId);
        }
    }

    @Override
    public List<StockEntity> findAllBooks() {
        return stockRepository.findAll();
    }
}
