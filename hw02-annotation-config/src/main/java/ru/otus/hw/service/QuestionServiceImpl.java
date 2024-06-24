package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Question;

import java.util.List;

@Service
@RequiredArgsConstructor

public class QuestionServiceImpl implements QuestionService {
    private final IOService ioService;

    @Override
    public void showQuestion(List<Question> questions, Question question) {
        ioService.printFormattedLine(questions.indexOf(question) + 1 + ". %s", question.text());
        var answers = question.answers();
        for (var answer : answers) {
            ioService.printFormattedLine("  " + (answers.indexOf(answer) + 1) + ") %s", answer.text());
        }
    }
}
