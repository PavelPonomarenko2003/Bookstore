package servlet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.MessageResponse;
import dto.ResponseEntityDTO;
import entity.BookEntity;
import exception.ServletExceptionCustom;
import exception.exception_handling.ServletExceptionHandling;
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
import serialization.config.ApplicationConfig;
import service.impl.BookServiceImpl;
import service.impl.OrderServiceImpl;
import service.impl.StockServiceImpl;
import dto.BookRequestDTO;
import servlet.utility.ResponseHandlerForHttp;

import java.io.IOException;
import java.util.List;

@WebServlet("/books")
public class BookServlet extends HttpServlet {

    private BookServiceImpl bookService;
    private StockServiceImpl stockService;
    private OrderServiceImpl orderService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {

        BookRepository bookRepository = new BookRepositoryImpl();
        StockRepository stockRepository = new StockRepositoryImpl();
        OrderRepository orderRepository = new OrderRepositoryImpl();

        this.stockService = new StockServiceImpl(stockRepository);
        this.bookService = new BookServiceImpl(bookRepository, this.stockService);

        try {
            String configPath = getClass().getClassLoader().getResource("application.properties").getPath();
            ApplicationConfig config = new ApplicationConfig(configPath);
            this.orderService = new OrderServiceImpl(orderRepository, this.bookService, this.stockService, config);
        } catch (Exception e) {
            System.err.println("Configuration load failed in BookServlet!");
        }

        this.objectMapper = new ObjectMapper();
        System.out.println("BookServlet initialized with MySQL!!!");
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
        System.out.println("BookServlet context destroyed.");
    }
}
