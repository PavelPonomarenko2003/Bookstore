package servlet.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dto.ResponseEntityDTO;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Class for converting to Json and getting status
 */
public class ResponseHandlerForHttp {

    // adding for correct date converting, cause Jackson have some troubles with taht
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public static void send(HttpServletResponse response, ResponseEntityDTO<?> entity) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(entity.getStatus());

        if (entity.getBody() != null) {
            response.getWriter().write(mapper.writeValueAsString(entity.getBody()));
        }
    }
}
