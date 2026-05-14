package servlet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dto.MessageResponse;
import dto.OrderRequestDTO;
import dto.ResponseEntityDTO;
import entity.OrderEntity;
import exception.exception_handling.ServletExceptionHandling;
import exception.ServletExceptionCustom;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.impl.BookRecommendationService;
import service.interfaces.OrderService;
import servlet.utility.ResponseHandlerForHttp;

import java.io.IOException;
import java.util.List;

@WebServlet("/orders")
public class OrderServlet extends HttpServlet {

    private OrderService orderService;
    private BookRecommendationService recService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        var servletContext = getServletContext();
        this.orderService = (OrderService) servletContext.getAttribute("orderService");
        this.recService = (BookRecommendationService) servletContext.getAttribute("recommendationService");

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());

        System.out.println("OrderServlet fully refactored and initialized!");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String sortBy = request.getParameter("sortBy");
            int page = (request.getParameter("page") != null) ? Integer.parseInt(request.getParameter("page")) : 1;
            int size = (request.getParameter("size") != null) ? Integer.parseInt(request.getParameter("size")) : 3;

            List<OrderEntity> orders = orderService.getSortedOrders(sortBy != null ? sortBy : "id", page, size);

            ResponseHandlerForHttp.send(response, ResponseEntityDTO.status(HttpServletResponse.SC_OK, orders));
        } catch (NumberFormatException e) {
            ServletExceptionHandling.handle(response, new ServletExceptionCustom("Page and Size must be numbers!", 400));
        } catch (Exception exception) {
            ServletExceptionHandling.handle(response, exception);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            OrderRequestDTO dto = objectMapper.readValue(request.getInputStream(), OrderRequestDTO.class);

            orderService.createOrder(dto.getUserId(), dto.getBookTitle(), dto.getQuantity());

            recService.addPurchase(dto.getUserId(), dto.getBookTitle());
            List<String> recommendations = recService.getRecommendations(dto.getUserId());

            String message = "Order created! Your recommendations: " + recommendations;
            ResponseHandlerForHttp.send(response, ResponseEntityDTO.status(
                    HttpServletResponse.SC_CREATED,
                    new MessageResponse(message)
            ));

        } catch (Exception e) {
            ServletExceptionHandling.handle(response, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idParam = request.getParameter("id");
            String action = request.getParameter("action");

            if (idParam == null || action == null) {
                throw new ServletExceptionCustom("Parameters 'id' and 'action' are required!", 400);
            }

            Long id = Long.valueOf(idParam);

            if ("complete".equalsIgnoreCase(action)) {
                orderService.completeOrder(id);
            } else if ("cancel".equalsIgnoreCase(action)) {
                orderService.cancelOrder(id);
            } else {
                throw new ServletExceptionCustom("Unknown action: " + action, 400);
            }

            ResponseHandlerForHttp.send(response, ResponseEntityDTO.status(
                    HttpServletResponse.SC_OK,
                    new MessageResponse("Order status updated successfully!")
            ));
        } catch (Exception e) {
            ServletExceptionHandling.handle(response, e);
        }
    }

    @Override
    public void destroy() {
        System.out.println("OrderServlet context destroyed.");
    }
}
