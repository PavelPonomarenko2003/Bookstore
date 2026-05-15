package bookstore.service.impl;

import bookstore.dto.OrderResponseDTO;
import bookstore.entity.*;
import bookstore.exception.BookNotFoundException;
import bookstore.exception.BooksAreOutOfStockException;
import bookstore.exception.OrderNotFoundException;
import bookstore.exception.StatusChangingException;
import bookstore.repository.interfaces.BookRepository;
import bookstore.repository.interfaces.OrderRepository;
import bookstore.service.interfaces.OrderService;
import bookstore.service.interfaces.StockService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final StockService stockService;

    // about open-closed bookstore
    @Value("${bookstore.allow.changing.availability}")
    private boolean isAllowChangeAvailability;

    public OrderServiceImpl(OrderRepository orderRepository,
                            BookRepository bookRepository,
                            StockService stockService) {
        this.orderRepository = orderRepository;
        this.bookRepository = bookRepository;
        this.stockService = stockService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ, timeout = 5, value = "transactionManager")
    public void createOrder(Long userId, String bookTitle, Integer quantity) {
        if (!isAllowChangeAvailability) {
            System.out.println("Sorry, the bookstore is closed!");
            return;
        }

        BookEntity book = bookRepository.findByTitleIgnoreCase(bookTitle)
                .orElseThrow(BookNotFoundException::new);

        if (!stockService.doWeHaveThatBooksInStock(book.getId(), quantity)) {
            throw new BooksAreOutOfStockException();
        }

        OrderEntity order = new OrderEntity();
        order.setCreatedTimestamp(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.OPENED);

        UserEntity user = new UserEntity();
        user.setId(userId);
        order.setUser(user);

        BigDecimal total = book.getPrice().multiply(BigDecimal.valueOf(quantity));
        order.setTotalPrice(total);

        OrderItemEntity item = new OrderItemEntity();
        item.setBook(book);
        item.setNumberOfBooks(quantity);
        item.setPriceAtTheTimeOfPurchase(book.getPrice());

        order.addOrderItem(item);

        orderRepository.save(order);

        Integer currentStock = stockService.getBookQuantity(book.getId());
        stockService.updateQuantity(book.getId(), currentStock - quantity);

        System.out.println("Success: Order created. ID: " + order.getId() + ", Total: " + order.getTotalPrice());
    }


    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ, timeout = 5, value = "transactionManager")
    public void cancelOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getOrderStatus() != OrderStatus.OPENED) {
            throw new StatusChangingException(order.getOrderStatus());
        }

        for (OrderItemEntity item : order.getListBooksInOrder()) {
            Long bookId = item.getBook().getId();
            Integer currentStock = stockService.getBookQuantity(bookId);
            stockService.updateQuantity(bookId, currentStock + item.getNumberOfBooks());
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setFinishedTimestamp(LocalDateTime.now());

        orderRepository.save(order);

        System.out.println("Success: Order " + orderId + " is CANCELLED.");
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ, timeout = 5, value = "transactionManager")
    public void completeOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getOrderStatus() != OrderStatus.OPENED) {
            throw new StatusChangingException(order.getOrderStatus());
        }

        order.setOrderStatus(OrderStatus.FINISHED);
        order.setFinishedTimestamp(LocalDateTime.now());
        orderRepository.save(order);

        System.out.println("Success: Order " + orderId + " is FINISHED.");
    }

    @Override
    public List<OrderEntity> getOrdersByUserId(Long userId) {
        return orderRepository.findAllByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true, value = "transactionManager")
    public List<OrderResponseDTO> getSortedOrders(String sortBy, int page, int pageSize) {

        String sortField = switch (sortBy.toLowerCase()) {
            case "price" -> "totalPrice";
            case "opened" -> "createdTimestamp";
            case "finished" -> "finishedTimestamp";
            default -> "id";
        };

        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending());


        List<OrderEntity> orders = orderRepository.findAll(pageable).getContent();

        return orders.stream().map(order -> {
            OrderResponseDTO dto = new OrderResponseDTO();
            dto.setId(order.getId());
            dto.setTotalPrice(order.getTotalPrice());
            dto.setCreatedTimestamp(order.getCreatedTimestamp().toString());

            List<String> titles = order.getListBooksInOrder().stream()
                    .map(orderItem -> orderItem.getBook().getTitle())
                    .toList();

            dto.setBookTitles(titles);
            return dto;
        }).toList();
    }
}