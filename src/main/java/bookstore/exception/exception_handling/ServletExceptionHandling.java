package bookstore.exception.exception_handling;

import bookstore.dto.MessageResponse;
import bookstore.dto.ResponseEntityDTO;
import bookstore.exception.ServletExceptionCustom;
import jakarta.servlet.http.HttpServletResponse;
import bookstore.servlet.utility.ResponseHandlerForHttp;

import java.io.IOException;

public class ServletExceptionHandling {

    public static void handle(HttpServletResponse response, Exception exception) throws IOException {
        if (exception instanceof ServletExceptionCustom) {
            ServletExceptionCustom servletException = (ServletExceptionCustom) exception;
            ResponseHandlerForHttp.send(response, ResponseEntityDTO.status(
                    servletException.getStatusCode(),
                    new MessageResponse(servletException.getMessage())
            ));
        } else {
            exception.printStackTrace();

            ResponseHandlerForHttp.send(response, ResponseEntityDTO.status(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    new MessageResponse("Internal server error: " + exception.getMessage())
            ));
        }
    }
}
