package serialization;

import exception.serialization_exceptions.DataWritingToFileException;
import exception.serialization_exceptions.FileNotFoundExceptionCustom;
import exception.serialization_exceptions.LoadingDataFromFileException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.FileInputStream;

/**
 * Main functionality about class SerializationUtil:
 * data transfer from os to disk and back - serialization/deserialization
 * data control during transfer using autocloseable and checked custom exceptions
 * Here we are using stream of bytes ObjectOutputStream/ObjectInputStream,
 * but we can use stream of char FileWriter/FileReader for readable data
 * also we can use Buffering data transmission as well
 * but when we are using stream of bytes that's much faster and more easy transmission
 * less load on the system
 */
public class SerializationUtility {

    // byte stream out of the program (serialization)
    public static void save(Serializable data, String path)
            throws FileNotFoundExceptionCustom, DataWritingToFileException {
        if (path == null) {
            throw new FileNotFoundExceptionCustom(path);
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new DataWritingToFileException(path, e);
        }
    }

    // byte stream into the program (deserialization)
    public static Object load(String path)
            throws FileNotFoundExceptionCustom, LoadingDataFromFileException {
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundExceptionCustom(path);
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new LoadingDataFromFileException(path, e);
        }
    }
}
