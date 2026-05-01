package repository.interfaces;

import entity.OrderEntity;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends CrudRepository<OrderEntity, Long>{

    List<OrderEntity> findAllByUserId(Long userId);
}
