package repository.impl;

import repository.interfaces.StockRepository;

import java.util.HashMap;
import java.util.Map;

/**
 *  That class is implementation of StockRepository need to realize all
 *  methods that will be happening in DB connected with stock
 *  (other logic in service layers)
 */
public class StockRepositoryImpl implements StockRepository {

    private final Map<Long, Integer> stockDB = new HashMap<>();

    @Override
    public void updateBooksQuantity(Long bookId, Integer quantity) {
        if (stockDB.containsKey(bookId)) {
            stockDB.put(bookId, stockDB.get(bookId) + quantity);
        } else {
            stockDB.put(bookId, quantity);
        }
    }

    @Override
    public Integer getQuantity(Long bookId) {
        return stockDB.getOrDefault(bookId, 0);
    }

    @Override
    public boolean doWeHaveThatBooksInStock(Long bookId, Integer changedQuantity) {
        return getQuantity(bookId) >= changedQuantity;
    }

    @Override
    public void deleteBook(Long bookId) {
        stockDB.remove(bookId);
    }
}
