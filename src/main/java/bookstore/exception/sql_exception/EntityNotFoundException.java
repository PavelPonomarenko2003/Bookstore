package bookstore.exception.sql_exception;

public class EntityNotFoundException extends DataStorageException {

    public EntityNotFoundException(String message) {
        super(message);
    }
}
