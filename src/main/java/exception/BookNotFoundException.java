package exception;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException() {
        super("Such book hasn't found!");
    }

    public BookNotFoundException(Throwable cause) {
        super("Such book hasn't found!", cause);
    }

}
