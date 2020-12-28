package ru.digitalhabbits.homework3.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.digitalhabbits.homework3.dao.PersonDao;
import ru.digitalhabbits.homework3.domain.Department;
import ru.digitalhabbits.homework3.domain.Person;
import ru.digitalhabbits.homework3.model.DepartmentInfo;
import ru.digitalhabbits.homework3.model.PersonInfo;
import ru.digitalhabbits.homework3.model.PersonRequest;
import ru.digitalhabbits.homework3.model.PersonResponse;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PersonServiceImpl.class)
class PersonServiceTest {

    @MockBean
    private PersonDao personDao;
    @MockBean
    private DepartmentService departmentService;

    @Autowired
    private PersonService personService;

    @Test
    void findAllPersons() {
        Department department = new Department().setName("First dep").setId(1).setClosed(false);
        List<Person> personList = List.of(new Person().setAge(30).setFirstName("Ivan").setLastName("Ivanov").setId(20).setMiddleName("Ivanovich").setDepartment(department),
                new Person().setAge(20).setFirstName("Petr").setLastName("Petrov").setId(10).setMiddleName("Petrovich").setDepartment(department));
        when(personDao.findAll()).thenReturn(personList);
        DepartmentInfo departmentInfo = new DepartmentInfo().setName("First dep").setId(1);
        when(departmentService.buildDepartmentInfo(department)).thenReturn(departmentInfo);
        List<PersonResponse> allPersons = personService.findAllPersons();
        PersonResponse firstPersonResponse = allPersons.get(0);
        PersonResponse secondPersonResponse = allPersons.get(1);
        assertAll(() -> {
            assertEquals(departmentInfo, firstPersonResponse.getDepartment());
            assertEquals(30, firstPersonResponse.getAge());
            assertEquals(20, firstPersonResponse.getId());
            assertEquals("Ivanov Ivan Ivanovich", firstPersonResponse.getFullName());

            assertEquals(departmentInfo, secondPersonResponse.getDepartment());
            assertEquals(20, secondPersonResponse.getAge());
            assertEquals(10, secondPersonResponse.getId());
            assertEquals("Petrov Petr Petrovich", secondPersonResponse.getFullName());
        });
    }

    @Test
    void getPerson() {
        Department department = new Department().setName("First dep").setId(1).setClosed(false);
        DepartmentInfo departmentInfo = new DepartmentInfo().setName("First dep").setId(1);
        Person person = new Person().setAge(30).setFirstName("Ivan").setLastName("Ivanov").
                setId(20).setMiddleName("Ivanovich").setDepartment(department);
        when(departmentService.buildDepartmentInfo(department)).thenReturn(departmentInfo);
        when(personDao.findById(anyInt())).thenReturn(person);
        PersonResponse personResponse = personService.getPerson(1);
        assertAll(() -> {
            assertEquals(departmentInfo, personResponse.getDepartment());
            assertEquals(30, personResponse.getAge());
            assertEquals(20, personResponse.getId());
            assertEquals("Ivanov Ivan Ivanovich", personResponse.getFullName());
        });
    }


    @Test
    void getPersonNotFound() {
        when(personDao.findById(anyInt())).thenReturn(null);
        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> personService.getPerson(1),
                "Expected getPerson(1) to throw, but it didn't");
        assertTrue(entityNotFoundException.getMessage().contains("Не найден person с id: " + 1));
    }

    @Test
    void createPerson() {

        PersonRequest personRequest = new PersonRequest().setAge(30).setFirstName("Ivan").setLastName("Ivanov")
                .setMiddleName("Ivanovich");
        Person person = new Person().setAge(30).setFirstName("Ivan").setLastName("Ivanov").setMiddleName("Ivanovich").setId(10);
        when(personDao.create(any())).thenReturn(person);
        Integer id = personService.createPerson(personRequest);
        assertEquals(person.getId(), id);
    }

    @Test
    void updatePerson() {
        Department department = new Department().setName("First dep").setId(1).setClosed(false);
        DepartmentInfo departmentInfo = new DepartmentInfo().setName("First dep").setId(1);
        Person person = new Person().setAge(30).setFirstName("Ivan").setLastName("Ivanov").setMiddleName("Ivanovich").setId(10).setDepartment(department);
        when(departmentService.buildDepartmentInfo(department)).thenReturn(departmentInfo);
        when(personDao.findById(10)).thenReturn(person);
        when(personDao.update(person)).thenReturn(person);
        PersonRequest personRequest = new PersonRequest().setAge(20).setFirstName("Petr").setLastName("Petrov").setMiddleName("Petrovich");
        PersonResponse personResponse = personService.updatePerson(10, personRequest);
        verify(personDao,times(1)).update(person);
        assertAll(()->{
            assertEquals(departmentInfo, personResponse.getDepartment());
            assertEquals(20, personResponse.getAge());
            assertEquals(10, personResponse.getId());
            assertEquals("Petrov Petr Petrovich", personResponse.getFullName());
        });

    }

    @Test
    void updatePersonNotFound() {
        when(personDao.findById(anyInt())).thenReturn(null);
        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> personService.updatePerson(1, new PersonRequest()),
                "Expected updatePerson(1, new PersonRequest()) to throw, but it didn't");
        assertTrue(entityNotFoundException.getMessage().contains("Не найден person с id: " + 1));
    }

    @Test
    void deletePerson() {
        Department department = new Department().setName("First dep").setId(1).setClosed(false);
        Person person = new Person().setAge(30).setFirstName("Ivan").setLastName("Ivanov").setMiddleName("Ivanovich").setId(10).setDepartment(department);
        department.setPersonList(List.of(person));
        when(personDao.findById(10)).thenReturn(person);
        personService.deletePerson(10);
        verify(personDao,times(1)).delete(person.getId());
        assertAll(()->{
            assertFalse(department.getPersonList().contains(person));
        });
    }

    @Test
    void buildPersonInfo(){
        Person person = new Person().setAge(30).setFirstName("Ivan").setMiddleName("Ivanovich").setLastName("Ivanov").setId(1);
        PersonInfo personInfo = personService.buildPersonInfo(person);
        assertAll(()->{
            assertEquals(1,personInfo.getId());
            assertEquals("Ivanov Ivan Ivanovich",personInfo.getFullName());
        });

    }
}