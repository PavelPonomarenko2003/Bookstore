package repository.impl;

import entity.OrderEntity;
import exception.sql_exception.DataStorageException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import repository.interfaces.OrderRepository;

import java.sql.*;
import java.util.List;

public class OrderRepositoryImpl extends JpaBaseRepository<OrderEntity, Long> implements OrderRepository {

    public OrderRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory, OrderEntity.class);
    }

    @Override
    public List<OrderEntity> findAllByUserId(Long userId) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            String jpqlQuery = "SELECT o FROM OrderEntity o WHERE o.user.id = :userId";

            return entityManager.createQuery(jpqlQuery, OrderEntity.class)
                    .setParameter("userId", userId)
                    .getResultList();

        } catch (Exception exception) {
            throw new DataStorageException("Error during fetching orders by user ID!", exception);
        }
    }
}