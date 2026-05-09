package repository.interfaces;

import entity.StockEntity;

public interface StockRepository extends CrudRepository<StockEntity, Long> {

    void updateBooksQuantity(Long bookId, Integer amount);

    Integer getQuantity(Long bookId);

    boolean doWeHaveThatBooksInStock(Long bookId, Integer requestedAmount);

    void deleteBook(Long bookId);
}
