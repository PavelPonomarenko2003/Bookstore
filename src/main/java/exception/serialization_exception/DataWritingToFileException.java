package exception.serialization_exception;

public class DataWritingToFileException extends Exception {

    public DataWritingToFileException(String path, Throwable cause) {
        super("Error writing to file: " + path);
    }

}