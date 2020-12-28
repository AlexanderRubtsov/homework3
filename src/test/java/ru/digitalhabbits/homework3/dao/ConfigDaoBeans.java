package ru.digitalhabbits.homework3.dao;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ConfigDaoBeans {

    @Bean
    DepartmentDao departmentDao(){
        return new DepartmentDaoImpl();
    }

    @Bean
    PersonDao personDao(){
        return new PersonDaoImpl();
    }
}
