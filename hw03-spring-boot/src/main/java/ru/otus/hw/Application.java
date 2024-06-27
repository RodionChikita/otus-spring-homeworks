package ru.otus.hw;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.service.TestRunnerService;
@SpringBootApplication
@ConfigurationPropertiesScan("ru.otus.hw")
public class Application {
    public static void main(String[] args) {

        //Создать контекст Spring Boot приложения
        SpringApplication.run(Application.class, args);
    }
}