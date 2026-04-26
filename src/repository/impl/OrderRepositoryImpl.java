package repository.impl;

import entity.OrderEntity;
import repository.interfaces.OrderRepository;

import java.util.List;

/**
 * That class provide all db logic about orders
 */
public class OrderRepositoryImpl extends CrudRepositoryImpl<OrderEntity>
        implements OrderRepository {

    @Override
    public List<OrderEntity> findAllByUserId(Long userId) {
        return storageDB.values().stream()
                .filter(order -> order.getUserId().equals(userId))
                .toList();
    }
}