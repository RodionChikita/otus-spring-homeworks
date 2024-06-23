package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
@Setter
public class CsvQuestionDao implements QuestionDao {
    private static final Function<QuestionDto, Question> MAP_FROM_DTO_FUNCTION =
            q -> new Question(q.getText(), q.getAnswers());

    private final TestFileNameProvider fileNameProvider;
    private BufferedReader reader;

    @Override
    public List<Question> findAll() {
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(fileNameProvider.getTestFileName());
             InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             ) {
            setReader(new BufferedReader(streamReader));
            var csvReader = new CsvToBeanBuilder<QuestionDto>(reader)
                    .withType(QuestionDto.class)
                    .withSeparator(';')
                    .withSkipLines(1)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .build();
            return csvReader.stream().toList().stream().map(MAP_FROM_DTO_FUNCTION).toList();
        } catch (Exception ex) {
            throw new QuestionReadException(ex.getMessage(), ex);
        }
    }
}
