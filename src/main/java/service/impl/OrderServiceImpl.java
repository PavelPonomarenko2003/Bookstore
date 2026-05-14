package service.impl;

import entity.*;
import exception.*;
import repository.interfaces.BookRepository;
import repository.interfaces.OrderRepository;
import service.interfaces.BookService;
import service.interfaces.OrderService;
import service.interfaces.StockService;
import serialization.config.ApplicationConfig;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;
    private final StockService stockService;
    private final ApplicationConfig config;

    public OrderServiceImpl(OrderRepository orderRepository,
                            BookRepository bookRepository,
                            BookService bookService,
                            StockService stockService,
                            ApplicationConfig config) {
        this.orderRepository = orderRepository;
        this.bookRepository = bookRepository;
        this.bookService = bookService;
        this.stockService = stockService;
        this.config = config;
    }

    @Override
    public void createOrder(Long userId, String bookTitle, Integer quantity) {
        if (!config.isAllowChangeAvailability()) {
            System.out.println("Sorry, the bookstore is closed!");
            return;
        }

        BookEntity book = bookRepository.findByTitle(bookTitle)
                .orElseThrow(BookNotFoundException::new);

        if (!stockService.doWeHaveThatBooksInStock(book.getId(), quantity)) {
            throw new BooksAreOutOfStockException();
        }

        OrderEntity order = new OrderEntity();

        UserEntity userProxy = new UserEntity();
        userProxy.setId(userId);

        order.setUser(userProxy);
        order.setCreatedTimestamp(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.OPENED);

        OrderItemEntity item = new OrderItemEntity();
        item.setBook(book);
        item.setNumberOfBooks(quantity);
        item.setPriceAtTheTimeOfPurchase(book.getPrice());

        item.setOrder(order);
        order.getListBooksInOrder().add(item);

        BigDecimal total = book.getPrice().multiply(BigDecimal.valueOf(quantity));
        order.setTotalPrice(total);

        orderRepository.save(order);

        Integer currentStock = stockService.getBookQuantity(book.getId());
        stockService.updateQuantity(book.getId(), currentStock - quantity);

        System.out.println("Success: Order created. ID: " + order.getId() + ", Total: " + order.getTotalPrice());
    }

    @Override
    public void cancelOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getOrderStatus() != OrderStatus.OPENED) {
            throw new StatusChangingException(order.getOrderStatus());
        }

        for (OrderItemEntity item : order.getListBooksInOrder()) {
            Long bookId = item.getBook().getId();
            stockService.updateQuantity(bookId, -item.getNumberOfBooks());
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setFinishedTimestamp(LocalDateTime.now());

        orderRepository.save(order);

        System.out.println("Success: Order " + orderId + " is CANCELLED.");
    }

    @Override
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
    public List<OrderEntity> getSortedOrders(String sortBy, int page, int pageSize) {
        List<OrderEntity> allOrders = orderRepository.findAll();

        Map<String, Comparator<OrderEntity>> strategy = Map.of(
                "id",       Comparator.comparing(OrderEntity::getId),
                "price",    Comparator.comparing(OrderEntity::getTotalPrice),
                "opened",   Comparator.comparing(OrderEntity::getCreatedTimestamp),
                "finished", Comparator.comparing(OrderEntity::getFinishedTimestamp,
                        Comparator.nullsLast(Comparator.naturalOrder()))
        );

        Comparator<OrderEntity> comparator = strategy.getOrDefault(sortBy.toLowerCase(), (o1, o2) -> 0);

        return allOrders.stream()
                .sorted(comparator)
                .skip((long) (page - 1) * pageSize)
                .limit(pageSize)
                .toList();
    }
}
