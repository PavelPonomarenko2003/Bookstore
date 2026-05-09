package exception.sql_exception;

public class UniqueViolationException extends DataStorageException {

    public UniqueViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}

