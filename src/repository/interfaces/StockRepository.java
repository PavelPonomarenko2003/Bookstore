package repository.interfaces;

public interface StockRepository {

    void updateBooksQuantity(Long bookId, Integer quantity);

    Integer getQuantity(Long bookId);

    boolean doWeHaveThatBooksInStock(Long bookId, Integer changedQuantity);

    void deleteBook(Long bookId);
}
