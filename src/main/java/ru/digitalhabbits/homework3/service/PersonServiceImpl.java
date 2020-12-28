package ru.digitalhabbits.homework3.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.digitalhabbits.homework3.dao.PersonDao;
import ru.digitalhabbits.homework3.domain.Person;
import ru.digitalhabbits.homework3.model.PersonInfo;
import ru.digitalhabbits.homework3.model.PersonRequest;
import ru.digitalhabbits.homework3.model.PersonResponse;

import javax.annotation.Nonnull;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonDao personDao;
    @Autowired
    private  DepartmentService departmentService;

    @Nonnull
    @Override
    public List<PersonResponse> findAllPersons() {
        // TODO: NotImplemented: получение информации о всех людях во всех отделах
        return personDao.findAll().stream().map(this::buildPersonResponse).collect(Collectors.toList());

    }

    @Override
    public PersonInfo buildPersonInfo(Person person) {

        return new PersonInfo()
                .setFullName(getFullName(person))
                .setId(person.getId());
    }


    @Nonnull
    @Override
    public PersonResponse getPerson(@Nonnull Integer id) {
        // TODO: NotImplemented: получение информации о человеке. Если не найдено, отдавать 404:NotFound
        Person person = personDao.findById(id);
        if (person == null) throw new EntityNotFoundException("Не найден person с id: " + id);
        return buildPersonResponse(person);
    }

    @Nonnull
    @Override
    public Integer createPerson(@Nonnull PersonRequest request) {
        // TODO: NotImplemented: создание новой записи о человеке
        Person person = new Person()
                .setAge(request.getAge())
                .setFirstName(request.getFirstName())
                .setLastName(request.getLastName())
                .setMiddleName(request.getMiddleName());
        return personDao.create(person).getId();

    }

    @Nonnull
    @Override
    public PersonResponse updatePerson(@Nonnull Integer id, @Nonnull PersonRequest request) {
        // TODO: NotImplemented: обновление информации о человеке. Если не найдено, отдавать 404:NotFound
        Person person = personDao.findById(id);
        if (person == null) throw new EntityNotFoundException("Не найден person с id: " + id);
        person.setAge(request.getAge())
                .setFirstName(request.getFirstName())
                .setLastName(request.getLastName())
                .setMiddleName(request.getMiddleName());
        return buildPersonResponse(personDao.update(person));
    }

    @Override
    public void deletePerson(@Nonnull Integer id) {
        Person person = personDao.findById(id);
        if (person == null) return;
        List<Person> newPersonList = new ArrayList<>(person.getDepartment().getPersonList());
        newPersonList.remove(person);
        person.getDepartment().setPersonList(newPersonList);
        personDao.delete(person.getId());
        // TODO: NotImplemented: удаление информации о человеке и удаление его из отдела. Если не найдено, ничего не делать

    }

    private PersonResponse buildPersonResponse(Person person) {
        return new PersonResponse()
                .setAge(person.getAge())
                .setDepartment(departmentService.buildDepartmentInfo(person.getDepartment()))
                .setId(person.getId())
                .setFullName(getFullName(person));

    }

    private String getFullName(Person person) {
        String lastName = person.getLastName();
        String firstName = person.getFirstName();
        String middleName = person.getMiddleName();
        return lastName + " " + firstName + " " + middleName;
    }
}
