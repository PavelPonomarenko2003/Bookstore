import bookstore.Application;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// rise testing db H2
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
class BookstoreApplicationTests {

    @Test
    void contextLoads() {
    }
}
