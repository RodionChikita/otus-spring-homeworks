package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Question;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {
    private final LocalizedIOServiceImpl ioService;

    public QuestionServiceImpl(LocalizedIOServiceImpl ioService) {
        this.ioService = ioService;
    }

    @Override
    public void showQuestion(List<Question> questions, Question question) {
        ioService.printFormattedLine(questions.indexOf(question) + 1 + ". %s", question.text());
        var answers = question.answers();
        for (var answer : answers) {
            ioService.printFormattedLine("  " + (answers.indexOf(answer) + 1) + ") %s", answer.text());
        }
    }
}
