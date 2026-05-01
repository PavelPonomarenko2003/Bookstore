package exception;

public class BooksAreOutOfStockException extends RuntimeException {

    public BooksAreOutOfStockException() {
        super("Books are out of stock, Sorry!");
    }

    public BooksAreOutOfStockException(Throwable cause) {
        super("Books are out of stock, Sorry!", cause);
    }
}
