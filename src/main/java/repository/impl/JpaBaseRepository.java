package repository.impl;

import entity.BaseEntity;
import exception.EntityAlreadyExistException;
import exception.sql_exception.DataStorageException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import repository.interfaces.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * General class with basic functionality
 * @param <T> - entity
 * @param <ID> - Id of our object
 * EntityManagerFactory - singleton
 * EntityManager - one per one session (one query)
 */
public abstract class JpaBaseRepository<T extends BaseEntity, ID>
        implements CrudRepository<T, ID> {

    protected final EntityManagerFactory entityManagerFactory;
    private final Class<T> entityClass; // to prevent type erasure, we will save our type in here

    protected JpaBaseRepository(EntityManagerFactory entityManagerFactory, Class<T> entityClass) {
        this.entityManagerFactory = entityManagerFactory;
        this.entityClass = entityClass;
    }

    @Override
    public T save(T entity) {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            if (entity.getId() != null) {
                T existing = entityManager.find(entityClass, entity.getId());

                if (existing != null) {
                    throw new EntityAlreadyExistException("Object with ID " + entity.getId() + " already exist!");
                }
            }

            entityManager.persist(entity);
            entityManager.getTransaction().commit();
            return entity;
        } catch (Exception exception) {
            if (entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new DataStorageException("Error during save data!", exception);
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            T entity = entityManager.find(entityClass, id);
            return Optional.ofNullable(entity);
        } catch (Exception exception) {
            throw new DataStorageException("Error during get data by ID!", exception);
        }
    }

    @Override
    public List<T> findAll() {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            String query = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            return entityManager.createQuery(query, entityClass).getResultList();
        } catch (Exception exception) {
            throw new DataStorageException("Error during getting all data!", exception);
        }
    }

    @Override
    public void delete(ID id) {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            T entity = entityManager.find(entityClass, id);

            if (entity != null) {
                entityManager.remove(entity);
            }

            entityManager.getTransaction().commit();
        } catch (Exception exception) {
            if (entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new DataStorageException("Error during delete data!", exception);
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}