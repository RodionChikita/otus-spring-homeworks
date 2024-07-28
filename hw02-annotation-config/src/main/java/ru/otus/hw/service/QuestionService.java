package ru.otus.hw.service;

import ru.otus.hw.domain.Question;

import java.util.List;

public interface QuestionService {
    void showQuestion(List<Question> questions, Question question);
}
