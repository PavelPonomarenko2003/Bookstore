import entity.BookEntity;
import entity.OrderEntity;
import repository.impl.*;
import repository.interfaces.*;
import service.impl.*;
import service.interfaces.*;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        // We'll fix it up when we'll use spring, now we need to create it by ourselves
        BookRepository bookRepo = new BookRepositoryImpl();
        StockRepository stockRepo = new StockRepositoryImpl();
        OrderRepository orderRepo = new OrderRepositoryImpl();

        StockService stockService = new StockServiceImpl(stockRepo);
        BookService bookService = new BookServiceImpl(bookRepo, stockService);
        OrderService orderService = new OrderServiceImpl(orderRepo, bookService, stockService);

        bookService.addBookIntoCatalogAndUpdateStock("Java_Book", 100.0, 10);
        bookService.addBookIntoCatalogAndUpdateStock("AI_Book", 150.0, 5);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Order Management !!!!!!!!!!!!!!!");

        while (true) {
            System.out.println("\n MENU: ");
            System.out.println("1. Catalog / Stock");
            System.out.println("2. Create Order (Buy)");
            System.out.println("3. Cancel Order (Return items)");
            System.out.println("4. Finish Order");
            System.out.println("5. Show All My Orders");
            System.out.println("6. Show Sorted Orders (Pagination)");
            System.out.println("0. Exit");
            System.out.print("Action: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            try {
                if (choice == 1) {
                    for (BookEntity b : bookService.findAllBooksInCatalog()) {
                        System.out.println("[" + b.getTitle() + "] Price: " + b.getPrice()
                                + " Stock: " + stockService.getBookQuantity(b.getId()));
                    }
                }
                else if (choice == 2) {
                    System.out.print("Book title: ");
                    String title = scanner.nextLine();
                    System.out.print("Quantity: ");
                    int qty = scanner.nextInt();
                    orderService.createOrder(1L, title, qty);
                }
                else if (choice == 3) {
                    System.out.print("Enter Order ID to CANCEL: ");
                    Long id = scanner.nextLong();
                    orderService.cancelOrder(id);
                }
                else if (choice == 4) {
                    System.out.print("Enter Order ID to FINISH: ");
                    Long id = scanner.nextLong();
                    orderService.completeOrder(id);
                }
                else if (choice == 5) {
                    for (OrderEntity o : orderService.getOrdersByUserId(1L)) {
                        System.out.println("Order " + o.getId() + " Status: " + o.getOrderStatus() + " Total: " + o.getTotalPrice());
                    }
                }
                else if (choice == 6) {
                    System.out.println("\n--- Sorting Options: id, price, status: (opened, finished) ");
                    System.out.print("Sort by: ");
                    String sortBy = scanner.nextLine();

                    System.out.print("Enter page number: ");
                    int page = scanner.nextInt();

                    int pageSize = 1; // Size of out pages!!!

                    List<OrderEntity> sortedOrders = orderService.getSortedOrders(sortBy, page, pageSize);

                    if (sortedOrders.isEmpty()) {
                        System.out.println("No orders found on this page.");
                    } else {
                        System.out.println("\n--- Orders Page " + page + " (Sorted by " + sortBy + ") ---");
                        for (OrderEntity o : sortedOrders) {
                            System.out.println(
                                    "ID: " + o.getId() +
                                            " Status: " + o.getOrderStatus() +
                                            " Total: " + o.getTotalPrice() +
                                            " Created: " + o.getCreatedTimestamp()
                            );
                        }
                    }
                }
                else if (choice == 0) break;

            } catch (RuntimeException e) {
                System.out.println("System exception : " + e.getMessage());
            }
        }
    }
}
