import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.exceptions.QuestionReadException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CsvQuestionDaoTest {

    @Mock
    private TestFileNameProvider fileNameProvider;

    private CsvQuestionDao csvQuestionDao;
    private static final String FILE_EXIST = "questions.csv";
    private static final String FILE_NON_EXIST = "non-existent-file.csv";

    @BeforeEach
    public void setUp() {
        csvQuestionDao = new CsvQuestionDao(fileNameProvider);
    }

    @Test
    public void testFindAllThrowsException() {
        when(fileNameProvider.getTestFileName()).thenReturn(FILE_NON_EXIST);
        assertThrows(QuestionReadException.class, () -> csvQuestionDao.findAll());
    }

    @Test
    public void testFindAll() {
        String testFileName = "non-existent-file.csv";
        when(fileNameProvider.getTestFileName()).thenReturn(testFileName);
        assertThrows(QuestionReadException.class, () -> csvQuestionDao.findAll());
    }

    @Test
    public void testFindAllWithExistFile(){
        when(fileNameProvider.getTestFileName()).thenReturn(FILE_EXIST);
        assertDoesNotThrow(() -> csvQuestionDao.findAll());

    }
}
