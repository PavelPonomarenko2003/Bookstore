package service.impl;

import serialization.config.ApplicationConfig;
import entity.BookEntity;
import entity.OrderEntity;
import entity.OrderItemEntity;
import entity.OrderStatus;

import exception.BookNotFoundException;
import exception.BooksAreOutOfStockException;
import exception.OrderNotFoundException;
import exception.StatusChangingException;
import repository.interfaces.OrderRepository;
import service.interfaces.BookService;
import service.interfaces.OrderService;
import service.interfaces.StockService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final BookService bookService;
    private final StockService stockService;
    private final ApplicationConfig config;

    public OrderServiceImpl(OrderRepository orderRepository,
                            BookService bookService,
                            StockService stockService,
                            ApplicationConfig config) {
        this.orderRepository = orderRepository;
        this.bookService = bookService;
        this.stockService = stockService;
        this.config = config;
    }

    @Override
    public void createOrder(Long userId, String bookTitle, Integer quantity) {
        // flagpole on open/close bookstore
        if(config.isAllowChangeAvailability()) {
            BookEntity book = validateAndGetBookByTitle(bookTitle, quantity);

            OrderItemEntity item = createOrderItem(book, quantity);
            OrderEntity order = buildOrder(userId, item);

            saveOrderAndUpdateStock(order, book.getId(), quantity);
            System.out.println("Success: Order created. Total: " + order.getTotalPrice());
        } else {
            System.out.println("Sorry bookstore is closed!");
        }
    }

    @Override
    public void cancelOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getOrderStatus() != OrderStatus.OPENED) {
            throw new StatusChangingException(order.getOrderStatus());
        }

        for (OrderItemEntity item : order.getListBooksInOrder()) {
            stockService.updateQuantity(item.getBookId(), item.getNumberOfBooks());
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setFinishedTimestamp(LocalDateTime.now());
        orderRepository.save(order);

        System.out.println("Success: Order " + orderId + " is CANCELED.");
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

        System.out.println("Success! Order: " + orderId + " is FINISHED.");
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

        Comparator<OrderEntity> comparator = strategy.getOrDefault(sortBy.toLowerCase(),
                (o1, o2) -> 0);

        return allOrders.stream()
                .sorted(comparator)
                .skip((long) (page - 1) * pageSize)
                .limit(pageSize)
                .toList();
    }



    @Override
    public List<OrderEntity> getOrdersByUserId(Long userId) {
        return orderRepository.findAllByUserId(userId);
    }

    // Helper methods (When we'll use Spring and Hibernate we'll fix it up
    private BookEntity validateAndGetBookByTitle(String title, Integer quantity) {
        BookEntity book = null;
        for (BookEntity b : bookService.findAllBooksInCatalog()) {
            if (b.getTitle().equalsIgnoreCase(title)) {
                book = b;
                break;
            }
        }

        if (book == null) {
            throw new BookNotFoundException();
        }

        if (!stockService.doWeHaveThatBooksInStock(book.getId(), quantity)) {
            throw new BooksAreOutOfStockException();
        }
        return book;
    }

    private OrderItemEntity createOrderItem(BookEntity book, Integer quantity) {
        OrderItemEntity item = new OrderItemEntity();
        item.setBookId(book.getId());
        item.setNumberOfBooks(quantity);
        item.setPriceAtTheTimeOfPurchase(book.getPrice());
        return item;
    }

    private OrderEntity buildOrder(Long userId, OrderItemEntity item) {
        OrderEntity order = new OrderEntity();
        order.setUserId(userId);
        order.setCreatedTimestamp(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.OPENED);

        BigDecimal total = item.getPriceAtTheTimeOfPurchase().multiply(BigDecimal.valueOf(item.getNumberOfBooks()));
        order.setTotalPrice(total);
        order.getListBooksInOrder().add(item);
        return order;
    }

    private void saveOrderAndUpdateStock(OrderEntity order, Long bookId, Integer quantity) {
        orderRepository.save(order);
        stockService.updateQuantity(bookId, -quantity);
    }
}
