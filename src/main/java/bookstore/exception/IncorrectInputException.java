package bookstore.exception;

public class IncorrectInputException extends RuntimeException {

    public IncorrectInputException() {
        super("Incorrect data input. Try again!");
    }

    public IncorrectInputException(Throwable cause) {
        super("Incorrect data input. Try again!", cause);
    }
}
