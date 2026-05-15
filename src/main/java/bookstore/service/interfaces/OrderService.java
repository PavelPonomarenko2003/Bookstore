package bookstore.service.interfaces;

import bookstore.dto.OrderResponseDTO;
import bookstore.entity.OrderEntity;

import java.util.List;

public interface OrderService {

    void createOrder(Long userId, String bookTitle, Integer quantity);

    void cancelOrder(Long orderId);

    void completeOrder(Long orderId);

    List<OrderEntity> getOrdersByUserId(Long userId);

    List<OrderResponseDTO> getSortedOrders(String sortBy, int page, int pageSize);
}