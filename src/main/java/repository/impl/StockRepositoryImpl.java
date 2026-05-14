package repository.impl;

import entity.StockEntity;
import exception.sql_exception.DataStorageException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import repository.interfaces.StockRepository;

public class StockRepositoryImpl extends JpaBaseRepository<StockEntity, Long> implements StockRepository {

    public StockRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory, StockEntity.class);
    }

    @Override
    public void updateBooksQuantity(Long bookId, Integer quantity) {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            String sqlQuery = "INSERT INTO stock (book_id, quantity) VALUES (?, ?) " +
                    "ON DUPLICATE KEY UPDATE quantity = VALUES(quantity)";
            entityManager.createNativeQuery(sqlQuery)
                    .setParameter(1, bookId)
                    .setParameter(2, quantity)
                    .executeUpdate();

            entityManager.getTransaction().commit();
        } catch (Exception exception) {
            if(entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new DataStorageException("Error during updating stock quantity!", exception);
        }
    }

    @Override
    public Integer getQuantity(Long bookId) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            String jpqlQuery = "SELECT s.numberOfBooksInStock FROM StockEntity s WHERE book.id = :bookId";
            return entityManager.createQuery(jpqlQuery, Integer.class)
                    .setParameter("bookId", bookId)
                    .getSingleResult();
        } catch (NoResultException exception) {
            return 0;
        } catch (Exception exception) {
            throw new DataStorageException("Error during getting stock quantity!", exception);
        }
    }

    @Override
    public boolean doWeHaveThatBooksInStock(Long bookId, Integer requestedQuantity) {
        return getQuantity(bookId) >= requestedQuantity;
    }


    @Override
    public void deleteBook(Long bookId) {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            String jpql = "DELETE FROM StockEntity s WHERE s.book.id = :bookId";

            entityManager.createQuery(jpql)
                    .setParameter("bookId", bookId)
                    .executeUpdate();

            entityManager.getTransaction().commit();
        } catch (Exception exception) {
            if (entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new DataStorageException("Error during deleting book from stock!", exception);
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}
