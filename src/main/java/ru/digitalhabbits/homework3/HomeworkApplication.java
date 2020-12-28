package ru.digitalhabbits.homework3;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HomeworkApplication {
    public static void main(String[] args) {
        SpringApplication.run(HomeworkApplication.class, args);
    }


    @Bean
    public CommandLineRunner run(ApplicationContext applicationContext){
       return args -> System.out.println(applicationContext.getBeanDefinitionCount());
    }

}
