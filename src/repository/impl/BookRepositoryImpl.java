package repository.impl;

import entity.BookEntity;
import repository.interfaces.BookRepository;

import java.util.List;
import java.util.Optional;

/**
 * That class is implementation of our interface Book repository,
 * that provide our db logic and simulate our db action (cause we don't use db on that stage)
 * Right now we have just basic functionality, but in future we can realize new methods
 * Other logic about method's acting and validation we'll make in service
  */
public class BookRepositoryImpl extends CrudRepositoryImpl<BookEntity>
        implements BookRepository {

    // Constructor when we get data from the file during serialization
    public BookRepositoryImpl(List<BookEntity> listOfBooks) {
        super(listOfBooks);
    }

    public BookRepositoryImpl() {
        super();
    }

    @Override
    public Optional<BookEntity> findByTitle(String title) {
        return storageDB.values().stream()
                .filter(book -> book.getTitle().equalsIgnoreCase(title))
                .findFirst();
    }
}
