package serialization.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ApplicationConfig {

    // Keys from app properties
    private static final String KEY_AVAILABILITY = "bookstore.allow.changing.availability";
    private static final String KEY_PATH = "bookstore.file.saving.path";

    // fields to save our data
    private boolean allowChangeAvailability;
    private String savePath;

    public ApplicationConfig(String fileName) {
        Properties properties = new Properties();

        // on case if smth wrong with our file using try with res
        // also FileInputStream implements Autocloseable
        // using auto-closable to prevent our os taking over it
        try (FileInputStream fis = new FileInputStream(fileName)) {
            properties.load(fis);

            // preventing system falling (use default = true)
            this.allowChangeAvailability = Boolean.parseBoolean(
                    properties.getProperty(KEY_AVAILABILITY, "true")
            );

            this.savePath = properties.getProperty(KEY_PATH, "default.bin");

        } catch (IOException e) {
            System.err.println("Config file hasn't found!");
            this.allowChangeAvailability = true;
            this.savePath = "default.bin";
        }
    }

    public boolean isAllowChangeAvailability() {
        return allowChangeAvailability;
    }

    public String getSavePath() {
        return savePath;
    }
}
