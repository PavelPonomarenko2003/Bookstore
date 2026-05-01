package unit.serialization;

import exception.serialization_exceptions.FileNotFoundExceptionCustom;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import serialization.SerializationUtility;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Class for testing my saving data into file and getting data out of the file
 * serialization/deserialization
 */

@DisplayName("Testing class for serialization/deserialiaztion.")
public class SerializationUtilityTest {

    private final SerializationUtility serializationUtility = new SerializationUtility();

    @TempDir
    Path tempDirectory;

    @BeforeAll
    public static void notificationAboutTestStarting(TestInfo info) {
        System.out.println(info.getDisplayName() + " Get started!");
    }

    @AfterAll
    public static void notificationAboutTestFinishing(TestInfo info) {
        System.out.println(info.getDisplayName() + " Get finished!");
    }

    @BeforeEach
    public void notificationAboutMethodStarted(TestInfo info) {
        System.out.println(info.getDisplayName() + " Get started!");
    }

    @AfterEach
    public void notificationAboutMethodFinished(TestInfo info) {
        System.out.println(info.getDisplayName() + " Get finished!");
    }

    @Test
    @DisplayName("Save data into file (serialization).")
    void mustSaveObjectToFile() throws Exception {
        // Arrange
        String title = "Testing Book";

        Path filePath = tempDirectory.resolve("test_books.txt");
        String path = filePath.toString();

        // Act
        serializationUtility.save(title, path);

        // Assert
        assertTrue(Files.exists(filePath), "File has to exist!");
        assertTrue(Files.size(filePath) > 0, "File shouldn't be empty!");

        // check what data was saved in testing file
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            Object dataFromFile = ois.readObject();

            assertEquals(title, dataFromFile, "Loaded data must be equals original!");
            System.out.println("Original: " + title);
            System.out.println("Data in file: " + dataFromFile);
        }
    }

    @Test
    @DisplayName("Load data from file (deserialization).")
    void mustLoadObjectFromFile() throws Exception {
        // Arrange
        String originalData = "Testing book";
        Path filePath = tempDirectory.resolve("test_load.txt");
        String path = filePath.toString();

        serializationUtility.save(originalData, path);

        // Act
        Object loadedData = serializationUtility.load(path);

        // Assert
        assertNotNull(loadedData, "Loaded data should not be null");
        assertEquals(originalData, loadedData, "Loaded data must be equals original");

        System.out.println("Original: " + originalData);
        System.out.println("Loaded: " + loadedData);
    }

    // using functional interface Executable
    @Test
    @DisplayName("Exception 'FileNotFoundExceptionCustom' throwing if file doesn't exist executing method save.")
    void mustThrowExceptionWhenPathIsNullExecutingMethodSave() {
        assertThrows(FileNotFoundExceptionCustom.class, () -> {
            serializationUtility.save("Testing Data", null);
        });
    }

    @Test
    @DisplayName("Exception 'FileNotFoundExceptionCustom' throwing if file doesn't exist executing method load.")
    void mustThrowExceptionWhenPathIsNullExecutingMethodLoad() {
        String wrongPathOfFile = "Wrong Testing Path";
        assertThrows(FileNotFoundExceptionCustom.class, () -> {
            serializationUtility.load( wrongPathOfFile);
        });
    }
}
