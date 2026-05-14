package servlet.controller;

import dto.MessageResponse;
import dto.ResponseEntityDTO;
import entity.StockEntity;
import exception.ServletExceptionCustom;
import exception.exception_handling.ServletExceptionHandling;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.interfaces.StockService;
import servlet.utility.ResponseHandlerForHttp;

import java.io.IOException;
import java.util.List;

@WebServlet("/stock")
public class StockServlet extends HttpServlet {

    private StockService stockService;

    @Override
    public void init() {
        this.stockService = (StockService) getServletContext().getAttribute("stockService");

        System.out.println("StockServlet initialized successfully!");
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
        System.out.println("StockServlet context destroyed.");
    }
}
