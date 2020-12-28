package ru.digitalhabbits.homework3.dao;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Repository;
import ru.digitalhabbits.homework3.domain.Person;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class PersonDaoImpl implements PersonDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Person findById(@Nonnull Integer id) {

        return entityManager.find(Person.class, id);
    }

    @Override
    public List<Person> findAll() {
        return entityManager.createQuery("SELECT p FROM Person p", Person.class).getResultList();
    }

    @Override
    @Transactional
    public Person create(Person entity) {
        entityManager.persist(entity);
        return entity;
    }


    @Override
    @Transactional
    public Person update(Person entity) {
        Person merge = entityManager.merge(entity);
        entityManager.flush();
        return merge;
    }

    @Override
    @Transactional
    public Person delete(Integer integer) {
        Person entity = findById(integer);
        if (entity != null) entityManager.remove(entity);
        entityManager.flush();
        return entity;
    }
}
