package bookstore.serialization.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ApplicationConfig {

    // keys from application.propeties for db connection
    private static final String KEY_DB_URL = "db.url";
    private static final String KEY_DB_USER = "db.username";
    private static final String KEY_DB_PASS = "db.password";
    private static final String KEY_AVAILABILITY = "bookstore.allow.changing.availability";

    private String dbUrl;
    private String dbUser;
    private String dbPassword;
    private boolean allowChangeAvailability;

    public ApplicationConfig() {
    }

    public ApplicationConfig(String fileName) {
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream(fileName)) {
            properties.load(fis);

            this.dbUrl = properties.getProperty(KEY_DB_URL, "jdbc:mysql://localhost:3306/bookstore_db");
            this.dbUser = properties.getProperty(KEY_DB_USER, "root");
            this.dbPassword = properties.getProperty(KEY_DB_PASS, "root");

            this.allowChangeAvailability = Boolean.parseBoolean(
                    properties.getProperty(KEY_AVAILABILITY, "true")
            );

        } catch (IOException e) {
            System.err.println("Config file not found!");
        }
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public boolean isAllowChangeAvailability() {
        return allowChangeAvailability;
    }
}
