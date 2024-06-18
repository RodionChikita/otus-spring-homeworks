package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private static final Function<QuestionDto, Question> MAP_FROM_DTO_FUNCTION =
            q -> new Question(q.getText(), q.getAnswers());

    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        try (FileReader reader = new FileReader(fileNameProvider.getTestFileName())) {
            var csvReader = new CsvToBeanBuilder<QuestionDto>(reader)
                    .withType(QuestionDto.class)
                    .withSeparator(';')
                    .withSkipLines(1)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .build();
            return csvReader.stream().toList().stream().map(MAP_FROM_DTO_FUNCTION).toList();
        } catch (IOException ex) {
            throw new QuestionReadException(ex.getMessage(), ex);
        }
    }
}
