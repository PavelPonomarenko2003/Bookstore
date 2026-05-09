package service.interfaces;

import entity.StockEntity;

import java.sql.SQLException;
import java.util.List;

public interface StockService {

    void updateQuantity(Long bookId, Integer amount);

    Integer getBookQuantity(Long bookId);

    boolean doWeHaveThatBooksInStock(Long bookId, Integer requestedAmount);

    void removeStockData(Long bookId);

    List<StockEntity> findAllBooks();
}
