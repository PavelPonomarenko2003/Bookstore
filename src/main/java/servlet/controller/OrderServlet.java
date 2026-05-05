package servlet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.BookstoreStorageForSerializingDTO;
import dto.MessageResponse;
import dto.OrderRequestDTO;
import dto.ResponseEntityDTO;
import entity.OrderEntity;
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

@WebServlet("/orders")
public class OrderServlet extends HttpServlet {

    private OrderService orderService;
    private BookService bookService;
    private StockService stockService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {

        BookstoreStorageForSerializingDTO state = null;
        try {
            SerializationUtility serializationUtility = new SerializationUtility();
            state = (BookstoreStorageForSerializingDTO) serializationUtility.load("saving.bin");
        } catch (FileNotFoundExceptionCustom e) {
            System.out.println("Save file not found. We make conclusions that we don't have any orders!");
        } catch (LoadingDataFromFileException e) {
            System.err.println("Error loading order data: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }

        BookRepository bookRepository;
        StockRepository stockRepository;
        OrderRepository orderRepository;

        if (state != null) {
            bookRepository = new BookRepositoryImpl(state.getListOfBooks());
            stockRepository = new StockRepositoryImpl(state.getListOfStock());
            orderRepository = new OrderRepositoryImpl(state.getListOfOrders());
        } else {
            bookRepository = new BookRepositoryImpl();
            stockRepository = new StockRepositoryImpl();
            orderRepository = new OrderRepositoryImpl();
        }

        this.stockService = new StockServiceImpl(stockRepository);
        this.bookService = new BookServiceImpl(bookRepository, this.stockService);

        String configPath = getClass().getClassLoader().getResource("application.properties").getPath();
        ApplicationConfig config = new ApplicationConfig(configPath);

        this.orderService = new OrderServiceImpl(orderRepository, this.bookService, this.stockService, config);
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String sortBy = request.getParameter("sortBy");
            int page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
            int size = request.getParameter("size") != null ? Integer.parseInt(request.getParameter("size")) : 3;

            List<OrderEntity> orders = orderService.getSortedOrders(sortBy != null ? sortBy : "id", page, size);
            ResponseHandlerForHttp.send(response, ResponseEntityDTO.status(HttpServletResponse.SC_OK, orders));
        } catch (Exception exception) {
            ServletExceptionHandling.handle(response, exception);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            OrderRequestDTO dto = objectMapper.readValue(request.getInputStream(), OrderRequestDTO.class);
            orderService.createOrder(dto.getUserId(), dto.getBookTitle(), dto.getQuantity());

            ResponseHandlerForHttp.send(response, ResponseEntityDTO.status(
                    HttpServletResponse.SC_CREATED,
                    new MessageResponse("Order created successfully")
            ));
        } catch (Exception e) {
            ServletExceptionHandling.handle(response, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Long id = Long.valueOf(request.getParameter("id"));
            String action = request.getParameter("action");

            if ("complete".equalsIgnoreCase(action)) {
                orderService.completeOrder(id);
            } else if ("cancel".equalsIgnoreCase(action)) {
                orderService.cancelOrder(id);
            }

            ResponseHandlerForHttp.send(response, ResponseEntityDTO.status(HttpServletResponse.SC_OK,
                    new MessageResponse("Status updated!")));
        } catch (Exception e) {
            ServletExceptionHandling.handle(response, e);
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
            System.out.println("All order data saved successfully!");

        } catch (FileNotFoundExceptionCustom | DataWritingToFileException exception) {
            System.err.println("Save failed during shutdown: " + exception.getMessage());
        } catch (Exception exception) {
            System.err.println("Unexpected save error: " + exception.getMessage());
        }
    }
}