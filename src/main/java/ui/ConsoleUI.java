package ui;

import dto.BookstoreStorageForSerializingDTO;
import entity.BookEntity;
import entity.OrderEntity;
import entity.StockEntity;
import serialization.SerializationUtility;
import service.interfaces.BookService;
import service.interfaces.OrderService;
import service.interfaces.StockService;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final BookService bookService;
    private final OrderService orderService;
    private final StockService stockService;
    private final Scanner scanner;

    public ConsoleUI(BookService bookService, OrderService orderService, StockService stockService, String savePath) {
        this.bookService = bookService;
        this.orderService = orderService;
        this.stockService = stockService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Order Management System ");
        while (true) {
            printMenu();
            try {
                int choice = Integer.parseInt(scanner.nextLine());

                if (choice == 0) {
                    saveDataBeforeExit(); // data saving before leave program
                    System.out.println("Finished!");
                    break;
                }
                makeYourChoice(choice);
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            } catch (RuntimeException e) {
                System.out.println("System error: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("\n MENU. Choose your option: ");
        System.out.println("1. Catalog / Stock");
        System.out.println("2. Create Order (Buy)");
        System.out.println("3. Cancel Order (Return items)");
        System.out.println("4. Finish Order");
        System.out.println("5. Show All My Orders");
        System.out.println("6. Show Sorted Orders (Pagination)");
        System.out.println("0. Exit");
        System.out.print("Action: ");
    }

    private void makeYourChoice(int choice) {
        switch (choice) {
            case 1 -> showCatalog();
            case 2 -> createOrder();
            case 3 -> cancelOrder();
            case 4 -> completeOrder();
            case 5 -> showUserOrders();
            case 6 -> showSortedOrders();
            default -> System.out.println("Incorrect action!");
        }
    }

    private void showCatalog() {
        System.out.println("\n Catalog: ");
        for (BookEntity b : bookService.findAllBooksInCatalog()) {
            System.out.println("[" + b.getTitle() + "] Price: " + b.getPrice()
                    + "In Stock: " + stockService.getBookQuantity(b.getId()));
        }
    }

    private void createOrder() {
        System.out.print("Enter Book Title: ");
        String title = scanner.nextLine();
        System.out.print("Quantity: ");
        int qty = Integer.parseInt(scanner.nextLine());

        orderService.createOrder(1L, title, qty);
    }

    private void cancelOrder() {
        System.out.print("Enter Order ID to CANCEL: ");
        Long id = Long.parseLong(scanner.nextLine());
        orderService.cancelOrder(id);
        System.out.println("Order: " + id + " canceled.");
    }

    private void completeOrder() {
        System.out.print("Enter Order ID to FINISH: ");
        Long id = Long.parseLong(scanner.nextLine());
        orderService.completeOrder(id);
        System.out.println("Order: " + id + " completed.");
    }

    private void showUserOrders() {
        System.out.println("\n My Orders: ");
        List<OrderEntity> orders = orderService.getOrdersByUserId(1L);
        if (orders.isEmpty()) {
            System.out.println("No orders found!");
        } else {
            orders.forEach(this::printOrderDetails);
        }
    }

    private void showSortedOrders() {
        System.out.println("\n Sorting (id, price, status)");
        System.out.print("Sort by: ");
        String sortBy = scanner.nextLine();

        System.out.print("Page number: ");
        int page = Integer.parseInt(scanner.nextLine());

        int pageSize = 2; // Size of pages!!!!
        List<OrderEntity> sortedOrders = orderService.getSortedOrders(sortBy, page, pageSize);

        if (sortedOrders.isEmpty()) {
            System.out.println("No orders on this page.");
        } else {
            System.out.println("\n Page: " + page + " (Sorted by " + sortBy + ")");
            sortedOrders.forEach(this::printOrderDetails);
        }
    }

    private void printOrderDetails(OrderEntity o) {
        System.out.println("ID: " + o.getId() +
                " Статус: " + o.getOrderStatus() +
                " Сумма: " + o.getTotalPrice() +
                " Дата: " + o.getCreatedTimestamp());
    }

    private void saveDataBeforeExit() {
        try {
            // Data preparing for saving
            List<BookEntity> books = bookService.findAllBooksInCatalog();
            List<OrderEntity> orders = orderService.getOrdersByUserId(1L);
            List<StockEntity> stocks = stockService.findAllBooks();

            BookstoreStorageForSerializingDTO state =
                    new BookstoreStorageForSerializingDTO(books, orders, stocks);

            SerializationUtility serializationUtility = new SerializationUtility();

            serializationUtility.save(state, "saving.bin");

            System.out.println("All data saved to disk successfully.");
        } catch (Exception e) {
            System.out.println("Warning: Couldn't save data! " + e.getMessage());
        }
    }
}