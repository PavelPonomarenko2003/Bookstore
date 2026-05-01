package exception.serialization_exceptions;

public class LoadingDataFromFileException extends Exception {

    public LoadingDataFromFileException(String path, Throwable cause) {
        super("Error loading data from file: " + path);
    }
}
