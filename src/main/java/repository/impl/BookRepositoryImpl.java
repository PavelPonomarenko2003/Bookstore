package repository.impl;

import entity.BookEntity;
import exception.sql_exception.DataStorageException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import repository.interfaces.BookRepository;

import java.util.Optional;

/**
 * That class is implementation of our interface Book repository,
 * that provide our db logic and simulate our db action (cause we don't use db on that stage)
 * Right now we have just basic functionality, but in future we can realize new methods
 * Other logic about method's acting and validation we'll make in service
  */
public class BookRepositoryImpl extends JpaBaseRepository<BookEntity, Long>
        implements BookRepository {

    public BookRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory, BookEntity.class);
    }


    @Override
    public Optional<BookEntity> findByTitle(String title) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            String query = "SELECT book FROM BookEntity book WHERE LOWER(book.title) = LOWER(:title)";
            return entityManager.createQuery(query, BookEntity.class)
                    .setParameter("title", title)
                    .getResultList()
                    .stream()
                    .findFirst();
        } catch (Exception exception) {
            throw new DataStorageException("Error during getting book by title!", exception);
        }
    }
}