package ru.digitalhabbits.homework3.dao;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.digitalhabbits.homework3.domain.Department;
import ru.digitalhabbits.homework3.domain.Person;
import ru.digitalhabbits.homework3.service.DepartmentServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest()
@ContextConfiguration(classes = {ConfigDaoBeans.class})
@AutoConfigureTestEntityManager
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class DepartmentDaoImplTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private ApplicationContext applicationContext;

    Department department1 = new Department().setName("First Department").setClosed(true).setPersonList(new ArrayList<>());
    Department department2 = new Department().setName("Second Department").setClosed(false).setPersonList(new ArrayList<>());


    @Test
    void create(){
        departmentDao.create(department1);
        entityManager.clear();
        Department actual = departmentDao.findById(department1.getId());
        assertEquals(department1, actual);
    }

    @Test
    void findById() {
        Integer id = entityManager.persistAndGetId(department1, Integer.class);
        System.out.println(applicationContext.getBeanDefinitionCount());
        entityManager.clear();
        Department department = departmentDao.findById(id);
        assertEquals(department1, department);

    }

    @Test
    void findAll() {
        List<Department> expected = List.of(this.department1, department2);
        entityManager.persistAndFlush(department1);
        entityManager.persistAndFlush(department2);
        entityManager.clear();
        List<Department> actual = departmentDao.findAll();
        assertEquals(expected, actual);

    }

    @Test
    void update() {
        entityManager.persistAndFlush(department1);
        department1.setName("New Name");
        entityManager.clear();
        departmentDao.update(department1);
        Object actual = entityManager.find(Department.class, department1.getId());
        assertEquals(department1, actual);

    }

    @Test
    void delete() {
        entityManager.persistAndFlush(department1);
        entityManager.clear();
        Department department = departmentDao.findById(department1.getId());
        departmentDao.delete(department1.getId());
        entityManager.clear();
        Department actual = departmentDao.findById(department.getId());
        assertNotNull(department);
        assertNull(actual);
    }
}