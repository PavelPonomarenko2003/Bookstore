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
import service.interfaces.BookService; // Переходим на интерфейс вместо реализации
import dto.BookRequestDTO;
import servlet.utility.ResponseHandlerForHttp;

import java.io.IOException;
import java.util.List;

@WebServlet("/books")
public class BookServlet extends HttpServlet {

    private BookService bookService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        this.bookService = (BookService) getServletContext().getAttribute("bookService");

        this.objectMapper = new ObjectMapper();

        System.out.println("BookServlet initialized successfully!");
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
