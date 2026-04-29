package repository.interfaces;

import entity.StockEntity;

public interface StockRepository extends CrudRepository<StockEntity, Long>{

    void updateBooksQuantity(Long bookId, Integer quantity);

    Integer getQuantity(Long bookId);

    boolean doWeHaveThatBooksInStock(Long bookId, Integer changedQuantity);

    void deleteBook(Long bookId);
}
