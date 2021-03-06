package ru.digitalhabbits.homework3.service;

import ru.digitalhabbits.homework3.domain.Person;
import ru.digitalhabbits.homework3.model.PersonInfo;
import ru.digitalhabbits.homework3.model.PersonRequest;
import ru.digitalhabbits.homework3.model.PersonResponse;

import javax.annotation.Nonnull;
import java.util.List;

public interface PersonService {

    @Nonnull
    List<PersonResponse> findAllPersons();

    PersonInfo buildPersonInfo(Person person);

    @Nonnull
    PersonResponse getPerson(@Nonnull Integer id);

    @Nonnull
    Integer createPerson(@Nonnull PersonRequest request);

    @Nonnull
    PersonResponse updatePerson(@Nonnull Integer id, @Nonnull PersonRequest request);

    void deletePerson(@Nonnull Integer id);
}
