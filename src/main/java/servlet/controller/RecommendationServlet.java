package servlet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import exception.ServletExceptionCustom;
import exception.exception_handling.ServletExceptionHandling;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.impl.BookRecommendationService;

import java.io.IOException;
import java.util.List;

@WebServlet("/recommendations")
public class RecommendationServlet extends HttpServlet {

    private BookRecommendationService recommendationService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        this.recommendationService = new BookRecommendationService();
        this.objectMapper = new ObjectMapper();
        System.out.println("RecommendationServlet (Neo4j) initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String userIdParam = request.getParameter("userId");

            if (userIdParam == null || userIdParam.isEmpty()) {
                throw new ServletExceptionCustom("User ID is required!", 400);
            }

            Long userId = Long.valueOf(userIdParam);

            List<String> recommendations = recommendationService.getRecommendations(userId);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(recommendations));

        } catch (NumberFormatException e) {
            ServletExceptionHandling.handle(response, new ServletExceptionCustom("User ID must be a number!", 400));
        } catch (Exception e) {
            ServletExceptionHandling.handle(response, e);
        }
    }
}
