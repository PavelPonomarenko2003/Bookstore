package repository.interfaces;

import entity.OrderEntity;

import java.util.List;

public interface OrderRepository extends CrudRepository<OrderEntity, Long>{

    List<OrderEntity> findAllByUserId(Long userId);
}
