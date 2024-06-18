package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        for (int i = 0; questionDao.findAll().size() > i; i++) {
            ioService.printFormattedLine(i + 1 + ". %s", questionDao.findAll().get(i).text());
            for (int j = 0; questionDao.findAll().get(i).answers().size() > j; j++) {
                ioService.printFormattedLine("  " + (j + 1) + ") %s", questionDao.findAll().get(i).answers().get(j).text());
            }
        }
        // Получить вопросы из дао и вывести их с вариантами ответов
    }
}
