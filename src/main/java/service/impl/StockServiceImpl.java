package service.impl;

import entity.StockEntity;
import repository.interfaces.StockRepository;
import service.interfaces.StockService;

import java.util.List;

public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    public StockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
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

    @Override
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
