package com.test.hibernate;

import java.time.LocalDate;

import javax.persistence.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.test.hibernate.model.DataTypes;

/**
 * @author Antonio Damato <anto.damato@gmail.com>
 */
public class DataTypesTest {
    private static EntityManagerFactory emf;

    @BeforeAll
    public static void beforeAll() {
        emf = Persistence.createEntityManagerFactory("data_types", PersistenceUnitProperties.getProperties());
    }

    @AfterAll
    public static void afterAll() {
        emf.close();
    }

    @Test
    public void dataTypes() throws Exception {
//      org.apache.log4j.Logger.getLogger("org.hibernate.SQL").setLevel(org.apache.log4j.Level.OFF);
        final EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        DataTypes dataTypes = new DataTypes();
        dataTypes.setTimeValue(java.sql.Date.valueOf(LocalDate.now()));
        em.persist(dataTypes);
        Assertions.assertThrows(PersistenceException.class, () -> {
            em.flush();
        });

        Assertions.assertNotNull(dataTypes.getId());

        DataTypes d = em.find(DataTypes.class, dataTypes.getId());
        Assertions.assertNotNull(d);
        Assertions.assertEquals(dataTypes.getTimeValue(), d.getTimeValue());

        em.remove(d);
        tx.commit();
        em.close();
    }

}
