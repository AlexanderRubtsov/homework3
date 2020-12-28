package ru.digitalhabbits.homework3.dao;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Repository;
import ru.digitalhabbits.homework3.domain.Department;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class DepartmentDaoImpl
        implements DepartmentDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Department findById(@Nonnull Integer integer) {
        return entityManager.find(Department.class, integer);

    }

    @Override
    @Transactional
    public Department create(Department entity) {
        entityManager.persist(entity);
        entityManager.flush();
        return entity;
    }

    @Override
    public List<Department> findAll() {
        return entityManager.createQuery("SELECT d FROM Department d", Department.class).getResultList();
    }

    @Override
    @Transactional
    public Department update(Department entity) {
        Department merge = entityManager.merge(entity);
        entityManager.flush();
        return merge;
    }

    @Override
    @Transactional
    public Department delete(Integer integer) {
        Department entity = findById(integer);
        if (entity != null) entityManager.remove(entity);
        entityManager.flush();
        return entity;
    }
}
