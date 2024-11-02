package com.test.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.test.hibernate.model.Address;
import com.test.hibernate.model.Citizen;

/**
 * @author Antonio Damato <anto.damato@gmail.com>
 */
public class RemoveTest {

    private static EntityManagerFactory emf;

    @BeforeAll
    public static void beforeAll() {
        emf = Persistence.createEntityManagerFactory("citizens", PersistenceUnitProperties.getProperties());
    }

    @AfterAll
    public static void afterAll() {
        emf.close();
    }

    @Test
    public void remove() throws Exception {
        final EntityManager em = emf.createEntityManager();
        final EntityTransaction tx = em.getTransaction();
        tx.begin();

        Citizen citizen = new Citizen();
        citizen.setName("Marc");
        em.persist(citizen);

        Address address = new Address();
        address.setName("Regent St");
        em.persist(address);

        tx.commit();

        tx.begin();
        em.remove(citizen);
        em.remove(address);
        tx.commit();

        Citizen c = em.find(Citizen.class, citizen.getId());
        Assertions.assertNull(c);

        c = em.find(Citizen.class, address.getId());
        Assertions.assertNull(c);

        em.close();
        emf.close();
    }

}
