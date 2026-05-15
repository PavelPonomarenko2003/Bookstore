package bookstore.service.interfaces;

import bookstore.entity.BookEntity;

import java.util.List;

public interface BookService {

    void addBookIntoCatalogAndUpdateStock(String title, double price, int initialQuantity);

    List<BookEntity> findAllBooksInCatalog();

    void deleteBookFromCatalogAndStock(Long id);
}
