package ru.digitalhabbits.homework3.dao;

import nonapi.io.github.classgraph.json.JSONUtils;
import org.springframework.data.repository.NoRepositoryBean;
import ru.digitalhabbits.homework3.domain.Person;

@NoRepositoryBean
public interface PersonDao
        extends CrudOperations<Person, Integer> {

    static void next(){
        System.out.println("sdsa");
    }

}