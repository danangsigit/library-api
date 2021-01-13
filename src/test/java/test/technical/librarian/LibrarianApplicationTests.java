package test.technical.librarian;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class LibrarianApplicationTests {

    @Test
    void contextLoads() throws Exception{
        assertEquals("00", "00");
    }

}
