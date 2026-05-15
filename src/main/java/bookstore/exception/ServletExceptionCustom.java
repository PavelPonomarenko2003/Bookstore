package bookstore.exception;

public class ServletExceptionCustom extends RuntimeException {
    private final int statusCode;

    public ServletExceptionCustom(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public ServletExceptionCustom(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}