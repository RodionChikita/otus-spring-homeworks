import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.service.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuestionServiceImplTest {
    private ByteArrayOutputStream outputStream;
    private IOService ioService;
    private QuestionService questionService;
    private static final Answer TEST_ANSWER_1 = new Answer("Science doesn't know this yet", true);
    private static final Answer TEST_ANSWER_2 = new Answer("Certainly. The red UFO is from Mars. And green is from Venus", true);
    private static final Answer TEST_ANSWER_3 = new Answer("Absolutely not", true);
    private static final Question TEST_QUESTION = new Question("Is there life on Mars?", List.of(TEST_ANSWER_1, TEST_ANSWER_2, TEST_ANSWER_3));
    private static final List<Question> TEST_QUESTIONS = new ArrayList<>(List.of(TEST_QUESTION));
    private static final String EXPECTED_RESULT =
            "1. Is there life on Mars?\n" +
            "  1) Science doesn't know this yet\n" +
            "  2) Certainly. The red UFO is from Mars. And green is from Venus\n" +
            "  3) Absolutely not\n";
    @BeforeEach
    public void setUp() {
        outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        ioService = new StreamsIOService(printStream, System.in);
        questionService = new QuestionServiceImpl(ioService);
    }

    @Test
    public void testShowQuestion() {
        questionService.showQuestion(TEST_QUESTIONS, TEST_QUESTION);
       assertEquals(EXPECTED_RESULT, outputStream.toString());
    }
}
