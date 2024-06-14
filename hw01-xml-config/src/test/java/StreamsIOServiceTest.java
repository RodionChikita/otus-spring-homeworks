import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.hw.service.StreamsIOService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StreamsIOServiceTest {

    private ByteArrayOutputStream outputStream;
    private StreamsIOService ioService;
    private final static String EXPECTED_OUTPUT = "Hello, World!\n";

    @BeforeEach
    public void setUp() {
        outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        ioService = new StreamsIOService(printStream);
    }

    @Test
    public void testPrintLine() {
        ioService.printLine("Hello, World!");
        assertEquals(EXPECTED_OUTPUT, outputStream.toString());
    }

    @Test
    public void testPrintFormattedLine() {
        String format = "Hello, %s!";
        String name = "World";
        ioService.printFormattedLine(format, name);
        assertEquals(EXPECTED_OUTPUT, outputStream.toString());
    }
}