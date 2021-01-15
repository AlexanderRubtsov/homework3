package ru.digitalhabbits.homework3.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.digitalhabbits.homework3.dao.DepartmentDao;
import ru.digitalhabbits.homework3.dao.PersonDao;
import ru.digitalhabbits.homework3.domain.Department;
import ru.digitalhabbits.homework3.domain.Person;
import ru.digitalhabbits.homework3.exceptions.DepartmentClosedException;
import ru.digitalhabbits.homework3.model.*;

import javax.annotation.Nonnull;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl
        implements DepartmentService {
    @Autowired
    private PersonService personService;
    private final DepartmentDao departmentDao;
    private final PersonDao personDao;

    @Nonnull
    @Override
    public List<DepartmentShortResponse> findAllDepartments() {
        return departmentDao.findAll().stream().map(this::buildDepartmentShortResponse).collect(Collectors.toList());
    }

    private DepartmentShortResponse buildDepartmentShortResponse(Department department) {
        return new DepartmentShortResponse()
                .setId(department.getId())
                .setName(department.getName());
    }

    @Override
    public DepartmentInfo buildDepartmentInfo(Department department) {
        return new DepartmentInfo()
                .setId(department.getId())
                .setName(department.getName());
    }

    @Nonnull
    @Override
    public DepartmentResponse getDepartment(@Nonnull Integer id) {
        Department department = departmentDao.findById(id);
        if (department == null) throw new EntityNotFoundException("Не найден департамент с id: " + id);
        return buildDepartmentResponse(department);

    }

    private DepartmentResponse buildDepartmentResponse(Department department) {
        List<PersonInfo> personInfoList = department.getPersonList().stream()
                .map(personService::buildPersonInfo).collect(Collectors.toList());

        return new DepartmentResponse()
                .setId(department.getId())
                .setName(department.getName())
                .setClosed(department.isClosed())
                .setPersons(personInfoList);
    }


    @Nonnull
    @Override
    public Integer createDepartment(@Nonnull DepartmentRequest request) {
        Department department = new Department()
                .setName(request.getName())
                .setClosed(false);
        department = departmentDao.create(department);
        return department.getId();

    }

    @Nonnull
    @Override
    public DepartmentResponse updateDepartment(@Nonnull Integer id, @Nonnull DepartmentRequest request) {

        Department department = departmentDao.findById(id);
        if (department == null) throw new EntityNotFoundException("Не найден департамент с id: " + id);
        department.setName(request.getName());
        return buildDepartmentResponse(departmentDao.update(department));

    }

    @Override
    public void deleteDepartment(@Nonnull Integer id) {
        Department department = departmentDao.findById(id);
        if (department == null) return;
        department.setPersonList(null);
        departmentDao.delete(department.getId());
    }

    @Override
    public void addPersonToDepartment(@Nonnull Integer departmentId, @Nonnull Integer personId) {
        Department department = departmentDao.findById(departmentId);
        Person person = personDao.findById(personId);
        if (department == null) throw new EntityNotFoundException("Не найден департамент с id: " + departmentId);
        if (person == null) throw new EntityNotFoundException("Не найден person с id: " + personId);
        if (department.isClosed()) throw new DepartmentClosedException("Департамент с id: " + departmentId+ " закрыт");
        List<Person> immutablePersonList = department.getPersonList();
        List<Person> newPersonList = new ArrayList<>(immutablePersonList);
        newPersonList.add(person);
        department.setPersonList(newPersonList);
        person.setDepartment(department);
        personDao.update(person);
        departmentDao.update(department);
    }

    @Override
    public void removePersonToDepartment(@Nonnull Integer departmentId, @Nonnull Integer personId) {
        Department department = departmentDao.findById(departmentId);
        Person person = personDao.findById(personId);
        if (department == null) throw new EntityNotFoundException("Не найден департамент с id: " + departmentId);
        if (person == null) return;
        List<Person> immutablePersonList = department.getPersonList();
        List<Person> newPersonList = new ArrayList<>(immutablePersonList);
        newPersonList.remove(person);
        department.setPersonList(newPersonList);
        departmentDao.update(department);
        person.setDepartment(null);
        personDao.update(person);
    }

    @Override
    public void closeDepartment(@Nonnull Integer id) {
        Department department = departmentDao.findById(id);
        if (department == null) throw new EntityNotFoundException("Не найден департамент с id: " + id);
        List<Person> personList = department.getPersonList();
        for (Person person: personList) {
            person.setDepartment(null);
            personDao.update(person);
        }
        department.setPersonList(null);
        department.setClosed(true);
        departmentDao.update(department);
    }
}
