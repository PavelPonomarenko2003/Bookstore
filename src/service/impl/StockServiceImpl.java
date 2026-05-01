package service.impl;

import repository.interfaces.StockRepository;
import service.interfaces.StockService;

public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    public StockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public void updateQuantity(Long bookId, Integer amount) {
        stockRepository.updateBooksQuantity(bookId, amount);
        System.out.println("Stock updated for book with ID: " + bookId +
                ". Amount changed by: " + amount);
    }

    @Override
    public Integer getBookQuantity(Long bookId) {
        return stockRepository.getQuantity(bookId);
    }

    @Override
    public boolean doWeHaveThatBooksInStock(Long bookId, Integer requestedAmount) {
        return stockRepository.doWeHaveThatBooksInStock(bookId, requestedAmount);
    }

    @Override
    public void removeStockData(Long bookId) {
        stockRepository.deleteBook(bookId);
        System.out.println("Stock data cleared for book ID: " + bookId);
    }
}
