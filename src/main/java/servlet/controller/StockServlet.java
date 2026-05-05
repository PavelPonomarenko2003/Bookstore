package servlet.controller;

import dto.BookstoreStorageForSerializingDTO;
import dto.MessageResponse;
import dto.ResponseEntityDTO;
import entity.StockEntity;
import exception.ServletExceptionCustom;
import exception.exception_handling.ServletExceptionHandling;
import exception.serialization_exceptions.DataWritingToFileException;
import exception.serialization_exceptions.FileNotFoundExceptionCustom;
import exception.serialization_exceptions.LoadingDataFromFileException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.impl.BookRepositoryImpl;
import repository.impl.OrderRepositoryImpl;
import repository.impl.StockRepositoryImpl;
import repository.interfaces.BookRepository;
import repository.interfaces.OrderRepository;
import repository.interfaces.StockRepository;
import serialization.SerializationUtility;
import serialization.config.ApplicationConfig;
import service.impl.BookServiceImpl;
import service.impl.OrderServiceImpl;
import service.impl.StockServiceImpl;
import service.interfaces.BookService;
import service.interfaces.OrderService;
import service.interfaces.StockService;
import servlet.utility.ResponseHandlerForHttp;

import java.io.IOException;
import java.util.List;

@WebServlet("/stock")
public class StockServlet extends HttpServlet {

    private StockService stockService;
    private BookService bookService;
    private OrderService orderService;

    @Override
    public void init() {

        BookstoreStorageForSerializingDTO state = null;
        try {
            SerializationUtility serializationUtility = new SerializationUtility();
            state = (BookstoreStorageForSerializingDTO) serializationUtility.load("saving.bin");
        } catch (FileNotFoundExceptionCustom e) {
            System.out.println("Save file not found. We make conclusions that our stock is empty now!");
        } catch (LoadingDataFromFileException e) {
            System.err.println("Error loading stock data: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }

        StockRepository stockRepository;
        BookRepository bookRepository;
        OrderRepository orderRepository;

        if (state != null) {
            stockRepository = new StockRepositoryImpl(state.getListOfStock());
            bookRepository = new BookRepositoryImpl(state.getListOfBooks());
            orderRepository = new OrderRepositoryImpl(state.getListOfOrders());
        } else {
            stockRepository = new StockRepositoryImpl();
            bookRepository = new BookRepositoryImpl();
            orderRepository = new OrderRepositoryImpl();
        }

        // 3. Initialize Services
        this.stockService = new StockServiceImpl(stockRepository);
        this.bookService = new BookServiceImpl(bookRepository, this.stockService);

        try {
            String configPath = getClass().getClassLoader().getResource("application.properties").getPath();
            ApplicationConfig config = new ApplicationConfig(configPath);
            this.orderService = new OrderServiceImpl(orderRepository, this.bookService, this.stockService, config);
        } catch (Exception e) {
            System.err.println("Configuration load failed.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<StockEntity> stocks = stockService.findAllBooks();
            ResponseHandlerForHttp.send(response, ResponseEntityDTO.status(HttpServletResponse.SC_OK, stocks));
        } catch (Exception exception) {
            ServletExceptionHandling.handle(response, exception);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String bookIdParam = request.getParameter("bookId");
            String amountParam = request.getParameter("amount");

            if (bookIdParam == null || amountParam == null) {
                throw new ServletExceptionCustom("Parameters 'bookId' and 'amount' are required!", 400);
            }

            Long bookId = Long.valueOf(bookIdParam);
            Integer amount = Integer.valueOf(amountParam);

            stockService.updateQuantity(bookId, amount);

            ResponseHandlerForHttp.send(response, ResponseEntityDTO.status(
                    HttpServletResponse.SC_OK,
                    new MessageResponse("Stock updated successfully for book ID: " + bookId)
            ));
        } catch (NumberFormatException exception) {
            ServletExceptionHandling.handle(response, new ServletExceptionCustom("ID and Amount must be numbers!", 400));
        } catch (Exception exception) {
            ServletExceptionHandling.handle(response, exception);
        }
    }

    @Override
    public void destroy() {
        try {
            BookstoreStorageForSerializingDTO state = new BookstoreStorageForSerializingDTO(
                    bookService.findAllBooksInCatalog(),
                    orderService.getOrdersByUserId(1L),
                    stockService.findAllBooks()
            );

            SerializationUtility serializationUtility = new SerializationUtility();
            serializationUtility.save(state, "saving.bin");
            System.out.println("Stock data saved successfully.");
        } catch (FileNotFoundExceptionCustom | DataWritingToFileException e) {
            System.err.println("Save failed: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected save error: " + e.getMessage());
        }
    }
}
