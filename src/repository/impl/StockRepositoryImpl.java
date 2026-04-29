package repository.impl;

import entity.StockEntity;
import repository.interfaces.StockRepository;
import java.util.List;

public class StockRepositoryImpl
        extends CrudRepositoryImpl<StockEntity> implements StockRepository {

    public StockRepositoryImpl(List<StockEntity> listOfStock) {
        super(listOfStock); // Данные уходят в родительскую карту storageDB
    }

    public StockRepositoryImpl() {
        super();
    }

    @Override
    public void updateBooksQuantity(Long bookId, Integer quantity) {
        StockEntity existingStock = null;

        // Ищем вручную через цикл
        for (StockEntity s : storageDB.values()) {
            if (s.getId().equals(bookId)) {
                existingStock = s;
                break;
            }
        }

        if (existingStock != null) {
            existingStock.setNumberOfBooksInStock(existingStock.getNumberOfBooksInStock() + quantity);
        } else {
            save(new StockEntity(bookId, quantity));
        }
    }

    @Override
    public Integer getQuantity(Long bookId) {
        for (StockEntity s : storageDB.values()) {
            if (s.getId().equals(bookId)) {
                return s.getNumberOfBooksInStock();
            }
        }
        return 0;
    }

    @Override
    public boolean doWeHaveThatBooksInStock(Long bookId, Integer changedQuantity) {
        return getQuantity(bookId) >= changedQuantity;
    }

    @Override
    public void deleteBook(Long bookId) {
        Long idToRemove = null;
        for (StockEntity s : storageDB.values()) {
            if (s.getId().equals(bookId)) {
                idToRemove = s.getId();
                break;
            }
        }
        if (idToRemove != null) {
            delete(idToRemove);
        }
    }
}
