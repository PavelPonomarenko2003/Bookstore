package bookstore.servlet.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import bookstore.dto.ResponseEntityDTO;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Class for converting to Json and getting status
 * to follow DRY better make such helper classes
 * //////////////////////////////////////////////
 * WebSocket - all classes marked this annotations handling all requests with certain url
 * methods that HttpsServlet has: init, destroy, doGet, doPost, doPut (don't have doPatch)
 * Http request - everything that client has sent
 * Https response - body for making answer to client
 * ObjectMapper - class from Jackson for data converting, client send JSON, Mapper convert to java
 * object, cause java doesn't work with JSON format!
 */
public class ResponseHandlerForHttp {

    // adding for correct date converting, cause Jackson have some troubles with that
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public static void send(HttpServletResponse response, ResponseEntityDTO<?> entity) throws IOException {
        // warn browser that we will send json format and he will parsing it
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(entity.getStatus());

        if (entity.getBody() != null) {
            response.getWriter().write(mapper.writeValueAsString(entity.getBody()));
        }
    }
}