import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.hw.domain.Student;
import ru.otus.hw.service.IOService;
import ru.otus.hw.service.StudentService;
import ru.otus.hw.service.StudentServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StudentServiceImplTest {
    private IOService ioService;

    private StudentService studentService;
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final Student TEST_STUDENT = new Student(FIRST_NAME, LAST_NAME);

    @BeforeEach
    public void setUp() {
        ioService = mock();
        studentService = new StudentServiceImpl(ioService);
    }

    @Test
    public void testDetermineCurrentStudent() {
        when(ioService.readStringWithPrompt("Please input your first name")).thenReturn(FIRST_NAME);
        when(ioService.readStringWithPrompt("Please input your last name")).thenReturn(LAST_NAME);
        Student result = studentService.determineCurrentStudent();
        assertEquals(TEST_STUDENT, result);
    }
}
