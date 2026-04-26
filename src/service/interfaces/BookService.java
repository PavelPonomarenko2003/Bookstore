package service.interfaces;

import entity.BookEntity;

import java.util.List;

public interface BookService {

    void addBookIntoCatalogAndUpdateStock(String title, double price, int initialQuantity);

    List<BookEntity> findAllBooksInCatalog();

    void deleteBookFromCatalog(Long id);
}
