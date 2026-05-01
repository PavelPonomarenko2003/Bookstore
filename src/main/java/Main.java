import serialization.config.ApplicationConfig;
import dto.BookstoreStorageForSerializingDTO;
import exception.serialization_exceptions.FileNotFoundExceptionCustom;
import repository.impl.BookRepositoryImpl;
import repository.impl.StockRepositoryImpl;
import repository.impl.OrderRepositoryImpl;

import repository.interfaces.BookRepository;
import repository.interfaces.StockRepository;
import repository.interfaces.OrderRepository;

import serialization.SerializationUtility;
import service.interfaces.StockService;
import service.interfaces.OrderService;
import service.interfaces.BookService;

import service.impl.OrderServiceImpl;
import service.impl.StockServiceImpl;
import service.impl.BookServiceImpl;

import ui.ConsoleUI;

public class Main {
    public static void main(String[] args) {

        ApplicationConfig config = new ApplicationConfig("application.properties");
        SerializationUtility serializationUtility = new SerializationUtility();
        String savePath = config.getSavePath();

        BookstoreStorageForSerializingDTO loadedState = null;
        try {
            loadedState = (BookstoreStorageForSerializingDTO) serializationUtility.load(savePath);
            System.out.println("Data got successfully:  " + savePath);
        } catch (FileNotFoundExceptionCustom e) {
            System.out.println("Will be created a new file.");
        } catch (Exception e) {
            System.err.println("Troubles with data loading: " + e.getMessage());
            e.printStackTrace();
        }

        // checking do we have any data
        BookRepository bookRepo = (loadedState != null)
                ? new BookRepositoryImpl(loadedState.getListOfBooks())
                : new BookRepositoryImpl();

        StockRepository stockRepo = (loadedState != null)
                ? new StockRepositoryImpl(loadedState.getListOfStock())
                : new StockRepositoryImpl();

        OrderRepository orderRepo = (loadedState != null)
                ? new OrderRepositoryImpl(loadedState.getListOfOrders())
                : new OrderRepositoryImpl();

        StockService stockService = new StockServiceImpl(stockRepo);
        BookService bookService = new BookServiceImpl(bookRepo, stockService);
        OrderService orderService = new OrderServiceImpl(orderRepo, bookService, stockService, config);

        // data for testing
        if (loadedState == null) {
            System.out.println("Testing data: ");
            bookService.addBookIntoCatalogAndUpdateStock("Java_Book", 100.0, 10);
            bookService.addBookIntoCatalogAndUpdateStock("AI_Book", 150.0, 5);
        }

        ConsoleUI ui = new ConsoleUI(bookService, orderService, stockService, savePath);
        ui.start();
    }
}
