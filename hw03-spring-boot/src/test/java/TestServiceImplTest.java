import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.*;
import ru.otus.hw.service.LocalizedIOServiceImpl;
import ru.otus.hw.service.TestServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TestServiceImplTest {

    @Mock
    private LocalizedIOServiceImpl ioService;

    @Mock
    private QuestionDao questionDao;

    @InjectMocks
    private TestServiceImpl testService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testExecuteTestFor() {
        // Given
        Student student = new Student("John", "Doe");

        Answer answer1 = new Answer("Science doesn't know this yet", true);
        Answer answer2 = new Answer("Certainly. The red UFO is from Mars. And green is from Venus", false);
        Answer answer3 = new Answer("Absolutely not", false);
        Question question = new Question("Is there life on Mars?", List.of(answer1, answer2, answer3));

        when(questionDao.findAll()).thenReturn(List.of(question));
        when(ioService.readIntForRangeWithPromptLocalized(anyInt(), anyInt(), anyString(), anyString())).thenReturn(1);

        TestResult testResult = testService.executeTestFor(student);

        assertEquals(1, testResult.getAnsweredQuestions().size());
        assertEquals(1, testResult.getRightAnswersCount());

        verify(ioService, times(1)).printLine("");
        verify(ioService, times(1)).printFormattedLineLocalized("TestService.answer.the.questions");
        verify(ioService, times(1)).printFormattedLine("1. %s", "Is there life on Mars?");
        verify(ioService, times(1)).printFormattedLine("  1) %s", "Science doesn't know this yet");
        verify(ioService, times(1)).printFormattedLine("  2) %s", "Certainly. The red UFO is from Mars. And green is from Venus");
        verify(ioService, times(1)).printFormattedLine("  3) %s", "Absolutely not");
    }
}