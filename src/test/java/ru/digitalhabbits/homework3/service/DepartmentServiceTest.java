package ru.digitalhabbits.homework3.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.digitalhabbits.homework3.dao.DepartmentDao;
import ru.digitalhabbits.homework3.dao.PersonDao;
import ru.digitalhabbits.homework3.domain.Department;
import ru.digitalhabbits.homework3.domain.Person;
import ru.digitalhabbits.homework3.exceptions.DepartmentClosedException;
import ru.digitalhabbits.homework3.model.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = DepartmentServiceImpl.class)
class DepartmentServiceTest {

    @MockBean
    private DepartmentDao departmentDao;
    @MockBean
    private PersonService personService;
    @MockBean
    private PersonDao personDao;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void findAllDepartments() {
        List<Department> departments = List.of(new Department().setId(1).setName("First Department"), new Department().setId(2).setName("Second Department"));
        when(departmentDao.findAll()).thenReturn(departments);
        List<DepartmentShortResponse> allDepartments = departmentService.findAllDepartments();
        DepartmentShortResponse first = allDepartments.get(0);
        DepartmentShortResponse second = allDepartments.get(1);
        assertAll(() -> {
            assertEquals(1, first.getId());
            assertEquals(2, second.getId());
            assertEquals("First Department", first.getName());
            assertEquals("Second Department", second.getName());
        });
        System.out.println(applicationContext.getBeanDefinitionCount());
    }

    @Test
    void getDepartment() {
        List<Person> people = List.of(new Person().setAge(10).setFirstName("Petr").setLastName("Petrov").setId(10));
        Department department = new Department().setId(1).setName("First Department").setClosed(false).setPersonList(people);
        when(departmentDao.findById(1)).thenReturn(department);
        PersonInfo petrov_petr = new PersonInfo().setId(10).setFullName("Petrov Petr");
        when(personService.buildPersonInfo(people.get(0))).thenReturn(petrov_petr);
        DepartmentResponse departmentResponse = departmentService.getDepartment(1);
        assertAll(() -> {
            assertEquals(1, departmentResponse.getId());
            assertEquals("First Department", departmentResponse.getName());
            assertFalse(departmentResponse.isClosed());
            assertEquals(petrov_petr, departmentResponse.getPersons().get(0));
        });
        System.out.println(applicationContext.getBeanDefinitionCount());
    }

    @Test
    void getDepartmentNotFound() {
        when(departmentDao.findById(1)).thenReturn(null);
        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> departmentService.getDepartment(1),
                "Expected getDepartment(1) to throw, but it didn't");
        assertTrue(entityNotFoundException.getMessage().contains("Не найден департамент с id: 1"));
    }

    @Test
    void createDepartment() {
        when(departmentDao.create(any())).thenReturn(new Department().setId(1));
        Integer id = departmentService.createDepartment(new DepartmentRequest().setName("New Department"));
        assertEquals(1, id);
    }

    @Test
    void updateDepartment() {
        List<Person> people = List.of(new Person().setAge(10).setFirstName("Petr").setLastName("Petrov").setId(10));
        Department department = new Department().setId(1).setName("First Department").setClosed(false).setPersonList(people);
        when(departmentDao.findById(1)).thenReturn(department);
        PersonInfo petrov_petr = new PersonInfo().setId(10).setFullName("Petrov Petr");
        when(personService.buildPersonInfo(people.get(0))).thenReturn(petrov_petr);
        when(departmentDao.update(department)).thenReturn(department);
        DepartmentResponse departmentResponse = departmentService.updateDepartment(1, new DepartmentRequest().setName("Changed Department"));
        assertAll(() -> {
            assertEquals(1, departmentResponse.getId());
            assertEquals("Changed Department", departmentResponse.getName());
            assertFalse(departmentResponse.isClosed());
            assertEquals(petrov_petr, departmentResponse.getPersons().get(0));
        });
    }

    @Test
    void updateDepartmentNotFound() {
        when(departmentDao.findById(1)).thenReturn(null);
        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> departmentService.getDepartment(1),
                "Expected getDepartment(1) to throw, but it didn't");
        assertTrue(entityNotFoundException.getMessage().contains("Не найден департамент с id: 1"));
    }

    @Test
    void deleteDepartment() {
        List<Person> people = List.of(new Person().setAge(10).setFirstName("Petr").setLastName("Petrov").setId(10));
        Department department = new Department().setId(1).setName("First Department").setClosed(false).setPersonList(people);
        when(departmentDao.findById(1)).thenReturn(department, department);
        when(departmentDao.delete(1)).thenReturn(department);
        departmentService.deleteDepartment(1);
        verify(departmentDao, times(1)).findById(1);
        verify(departmentDao, times(1)).delete(1);
        assertNull(department.getPersonList());
    }

    @Test
    void addPersonToDepartment() {
        Person person = new Person().setAge(20).setFirstName("Petr").setLastName("Petrov").setId(10);
        List<Person> personList = List.of(new Person().setAge(30).setFirstName("Ivan").setLastName("Ivanov").setId(20));
        Department department = new Department().setId(1).setName("First Department").setClosed(false).setPersonList(personList);
        when(departmentDao.findById(1)).thenReturn(department);
        when(personDao.findById(1)).thenReturn(person);
        departmentService.addPersonToDepartment(1,1);
        verify(departmentDao, times(1)).update(department);
        assertAll(()->{
            assertEquals(2, department.getPersonList().size());
            assertEquals(person, department.getPersonList().get(1));
        });
    }

    @Test
    void addPersonToDepartmentNotFoundException() {
        when(departmentDao.findById(1)).thenReturn(null, new Department(), new Department().setClosed(true));
        EntityNotFoundException departmentNotFound = assertThrows(EntityNotFoundException.class,
                () -> departmentService.addPersonToDepartment(1, 1),
                "Expected addPersonToDepartment(1,1) to throw, but it didn't");
        when(personDao.findById(1)).thenReturn(null, new Person());
        EntityNotFoundException personNotFound = assertThrows(EntityNotFoundException.class,
                () -> departmentService.addPersonToDepartment(1, 1),
                "Expected addPersonToDepartment(1,1) to throw, but it didn't");
        DepartmentClosedException departmentClosedException = assertThrows(DepartmentClosedException.class,
                () -> departmentService.addPersonToDepartment(1, 1),
                "Expected addPersonToDepartment(1,1) to throw, but it didn't");
        assertTrue(departmentNotFound.getMessage().contains("Не найден департамент с id: 1"));
        assertTrue(personNotFound.getMessage().contains("Не найден person с id: 1"));
        assertTrue(departmentClosedException.getMessage().contains("Департамент с id: " + 1 + " закрыт"));

    }

    @Test
    void removePersonToDepartment() {
        Person person = new Person().setAge(20).setFirstName("Petr").setLastName("Petrov").setId(10);
        List<Person> personList = List.of(new Person().setAge(30).setFirstName("Ivan").setLastName("Ivanov").setId(20), person);
        Department department = new Department().setId(1).setName("First Department").setClosed(false).setPersonList(personList);
        when(departmentDao.findById(1)).thenReturn(department);
        when(personDao.findById(1)).thenReturn(person);
        departmentService.removePersonToDepartment(1,1);
        verify(departmentDao, times(1)).update(department);
        assertAll(()->{
            assertEquals(1, department.getPersonList().size());
        });
    }

    @Test
    void failRemovePersonToDepartment() {
        when(departmentDao.findById(1)).thenReturn(null,new Department());
        when(personDao.findById(1)).thenReturn(null);

        EntityNotFoundException departmentNotFound = assertThrows(EntityNotFoundException.class,
                () -> departmentService.removePersonToDepartment(1, 1),
                "Expected removePersonToDepartment(1,1) to throw, but it didn't");
        assertTrue(departmentNotFound.getMessage().contains("Не найден департамент с id: " + 1));
        departmentService.removePersonToDepartment(1,1);
        verify(departmentDao, times(0)).update(any());
    }

    @Test
    void closeDepartment() {
        Person person = new Person().setAge(20).setFirstName("Petr").setLastName("Petrov").setId(10);
        List<Person> personList = List.of(new Person().setAge(30).setFirstName("Ivan").setLastName("Ivanov").setId(20), person);
        Department department = new Department().setId(1).setName("First Department").setClosed(false).setPersonList(personList);
        when(departmentDao.findById(1)).thenReturn(department);
        departmentService.closeDepartment(1);
        verify(departmentDao,times(1)).update(department);
        assertAll(()->{
            assertEquals("First Department", department.getName());
            assertEquals(1, department.getId());
            assertTrue(department.isClosed());
            assertNull(department.getPersonList());
        });
    }

    @Test
    void closeDepartmentNotFound() {
        when(departmentDao.findById(1)).thenReturn(null);
        EntityNotFoundException departmentNotFound = assertThrows(EntityNotFoundException.class,
                () -> departmentService.closeDepartment(1),
                "Expected closeDepartment(1) to throw, but it didn't");
        assertTrue(departmentNotFound.getMessage().contains("Не найден департамент с id: " + 1));
    }

    @Test
    void buildDepartmentInfo(){
        Department department = new Department().setId(1).setName("My dep:-)");
        DepartmentInfo departmentInfo = departmentService.buildDepartmentInfo(department);
        assertAll(()->{
           assertEquals(1,departmentInfo.getId());
           assertEquals("My dep:-)",departmentInfo.getName());
        });

    }
}