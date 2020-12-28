package ru.digitalhabbits.homework3.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import ru.digitalhabbits.homework3.domain.Department;
import ru.digitalhabbits.homework3.domain.Person;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest()
@ContextConfiguration(classes = {ConfigDaoBeans.class})
@AutoConfigureTestEntityManager
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class PersonDaoTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PersonDao personDao;


    Department department = new Department().setName("First dep").setClosed(false);
    Person person1 = new Person().setAge(30).setFirstName("Ivan").setLastName("Ivanov").setMiddleName("Ivanovich").setDepartment(department);
    Person person2 = new Person().setAge(20).setFirstName("Petr").setLastName("Petrov").setMiddleName("Petrovich").setDepartment(department);

    @Test
    void create(){
        entityManager.persistAndFlush(department);
        personDao.create(person1);
        entityManager.clear();
        Person actual = personDao.findById(person1.getId());
        assertEquals(person1, actual);
    }

    @Test
    void findById() {
        entityManager.persistAndFlush(department);
        entityManager.persistAndFlush(person1);
        entityManager.clear();
        Person actual = personDao.findById(person1.getId());
        assertEquals(actual, person1);
        // TODO: NotImplemented
    }

    @Test
    void findAll() {
        entityManager.persistAndFlush(department);
        entityManager.persistAndFlush(person1);
        entityManager.persistAndFlush(person2);
        List<Person> expected = List.of(this.person1, person2);
        entityManager.clear();
        List<Person> actual = personDao.findAll();
        assertEquals(expected,actual);
        // TODO: NotImplemented
    }

    @Test
    void update() {
        entityManager.persistAndFlush(department);
        entityManager.persistAndFlush(person1);
        person1.setDepartment(null);
        person1.setAge(35);
        personDao.update(person1);
        entityManager.clear();
        Person actual = personDao.findById(person1.getId());
        assertEquals(person1, actual);
        // TODO: NotImplemented
    }

    @Test
    void delete() {
        entityManager.persistAndFlush(department);
        entityManager.persistAndFlush(person1);
        entityManager.clear();
        Person oldPerson = personDao.findById(person1.getId());
        personDao.delete(oldPerson.getId());
        entityManager.clear();
        Person actual = personDao.findById(person1.getId());
        assertNotNull(oldPerson);
        assertNull(actual);
        // TODO: NotImplemented
    }
}