package bookstore.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long orderId) {
        super("Such order hasn't found with id: " + orderId);
    }

    public OrderNotFoundException(Throwable cause) {
        super("Such book order found!", cause);
    }
}
