package exception.serialization_exceptions;

public class FileNotFoundExceptionCustom extends Exception {

    public FileNotFoundExceptionCustom(String path) {
        super("File hasn't been created on path: " + path);
    }

    public FileNotFoundExceptionCustom(Throwable cause) {
        super("File hasn't been created! Check your file!", cause);
    }
}
