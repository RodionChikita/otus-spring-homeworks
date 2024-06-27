package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

@Service
public class TestServiceImpl implements TestService {

    private final LocalizedIOServiceImpl ioService;

    private final QuestionDao questionDao;

    private final QuestionService questionService;
    @Autowired

    public TestServiceImpl(LocalizedIOServiceImpl ioService, QuestionDao questionDao, QuestionService questionService) {
        this.ioService = ioService;
        this.questionService = questionService;
        this.questionDao = questionDao;
    }


    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLineLocalized("TestService.answer.the.questions");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question: questions) {
            questionService.showQuestion(questions, question);
            var numberOfAnswer = ioService.readIntForRangeWithPromptLocalized(1, question.answers().size(), "TestService.answer.the.questions", "Your answer is invalid, try again");
            testResult.applyAnswer(question, question.answers().get(numberOfAnswer - 1).isCorrect());
        }

        return testResult;
    }
}