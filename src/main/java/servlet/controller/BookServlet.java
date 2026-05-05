package servlet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.BookstoreStorageForSerializingDTO;
import dto.MessageResponse;
import dto.ResponseEntityDTO;
import entity.BookEntity;
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
import dto.BookRequestDTO;
import servlet.utility.ResponseHandlerForHttp;

import java.io.IOException;
import java.util.List;

/**
 * Serializations from my file saving.bin using in methods init/destroy
 * replace it from my console ui to this methods
 */
@WebServlet("/books")
public class BookServlet extends HttpServlet {

    private BookServiceImpl bookService;
    private StockServiceImpl stockService;
    private OrderServiceImpl orderService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {

        BookstoreStorageForSerializingDTO state = null;
        try {
            SerializationUtility serializationUtility = new SerializationUtility();
            state = (BookstoreStorageForSerializingDTO) serializationUtility.load("saving.bin");
        } catch (FileNotFoundExceptionCustom exception) {
            System.out.println("Save file not found. We make conclusions that we don't have any book in catalog!");
        } catch (LoadingDataFromFileException exception) {
            System.err.println("Error while loading data from file: " + exception.getMessage());
        } catch (Exception exception) {
            System.err.println("Unexpected error during data loading: " + exception.getMessage());
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

        try {
            String configPath = getClass().getClassLoader().getResource("application.properties").getPath();
            ApplicationConfig config = new ApplicationConfig(configPath);
            this.orderService = new OrderServiceImpl(orderRepository, this.bookService, this.stockService, config);
        } catch (Exception e) {
            System.err.println("Could not load application.properties!");
        }

        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<BookEntity> books = bookService.findAllBooksInCatalog();
            ResponseHandlerForHttp.send(response, ResponseEntityDTO.status(HttpServletResponse.SC_OK, books));
        } catch (Exception exception) {
            ServletExceptionHandling.handle(response, exception);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            BookRequestDTO dto = objectMapper.readValue(request.getInputStream(), BookRequestDTO.class);

            if (dto.getTitle() == null || dto.getTitle().isEmpty()) {
                throw new ServletExceptionCustom("Title is required!", 400);
            }

            bookService.addBookIntoCatalogAndUpdateStock(dto.getTitle(), dto.getPrice(), dto.getQuantity());

            ResponseHandlerForHttp.send(response, ResponseEntityDTO.status(
                    HttpServletResponse.SC_CREATED,
                    new MessageResponse("Book added successfully!")
            ));
        } catch (Exception e) {
            ServletExceptionHandling.handle(response, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                throw new ServletExceptionCustom("Parameter 'id' is required!", 400);
            }

            Long id = Long.valueOf(idParam);
            bookService.deleteBookFromCatalogAndStock(id);

            ResponseHandlerForHttp.send(response, ResponseEntityDTO.status(HttpServletResponse.SC_NO_CONTENT, null));
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
            System.out.println("Data saved successfully before server shutdown.");

        } catch (FileNotFoundExceptionCustom exception) {
            System.err.println("Save failed! Path is invalid or null.");
        } catch (DataWritingToFileException exception) {
            System.err.println("Error writing data to file: " + exception.getMessage());
        } catch (Exception exception) {
            System.err.println("Unexpected error during data saving: " + exception.getMessage());
        }
    }
}
