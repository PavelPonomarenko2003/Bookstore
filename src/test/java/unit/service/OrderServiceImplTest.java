package unit.service;

import entity.BookEntity;
import entity.OrderEntity;
import entity.OrderStatus;
import exception.OrderNotFoundException;
import exception.StatusChangingException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.interfaces.OrderRepository;
import serialization.config.ApplicationConfig;
import service.impl.OrderServiceImpl;
import service.interfaces.BookService;
import service.interfaces.StockService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private BookService bookService;

    @Mock
    private StockService stockService;

    @Mock
    private ApplicationConfig config;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    @DisplayName("Have to create order when bookstore is open.")
    void createOrderWhenBookstoreIsOpen() {
        // Arrange
        Long userId = 1L;
        String title = "Testing book";
        Integer qty = 2;

        BookEntity book = new BookEntity();
        book.setId(10L);
        book.setTitle(title); // Важно, чтобы title совпал
        book.setPrice(BigDecimal.valueOf(100));

        when(config.isAllowChangeAvailability()).thenReturn(true);

        when(bookService.findAllBooksInCatalog()).thenReturn(List.of(book));

        when(stockService.doWeHaveThatBooksInStock(10L, qty)).thenReturn(true);

        // Act
        orderService.createOrder(userId, title, qty);

        // Assert
        verify(orderRepository).save(any(OrderEntity.class));
        verify(stockService).updateQuantity(10L, -qty);
    }

    @Test
    @DisplayName("Have not to create order when bookstore is closed.")
    void createOrderWhenBookstoreIsClose() {
        // Arrange
        when(config.isAllowChangeAvailability()).thenReturn(false);

        // Act
        orderService.createOrder(1L, "Testing book", 1);

        // Assert
        verifyNoInteractions(bookService);
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(stockService);
    }

    @Test
    @DisplayName("Have to complete order successfully.")
    void finishOrderSuccess() {
        // Arrange
        Long orderId = 1L;
        OrderEntity order = new OrderEntity();
        order.setOrderStatus(OrderStatus.OPENED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        orderService.completeOrder(orderId);

        // Assert
        assertEquals(OrderStatus.FINISHED, order.getOrderStatus());
        assertNotNull(order.getFinishedTimestamp());
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("Have to throw exception when trying to finis non-opened order.")
    void mustThrowExceptionWhenWeHaveNotSuitableStatus() {
        // Arrange
        Long orderId = 1L;
        OrderEntity order = new OrderEntity();
        order.setOrderStatus(OrderStatus.CANCELLED); // Заказ уже отменен

        // Act
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Assert
        assertThrows(StatusChangingException.class, () -> orderService.completeOrder(orderId));
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Have to throw exception when order not found.")
    void mustThrowExceptionWhenOrderNotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.cancelOrder(1L));
    }
}
