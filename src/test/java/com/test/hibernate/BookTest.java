package com.test.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.test.hibernate.model.BookSample;

/**
 * @author adamato
 */
public class BookTest {

    private static EntityManagerFactory emf;

    @BeforeAll
    public static void beforeAll() {
        emf = Persistence.createEntityManagerFactory("books", PersistenceUnitProperties.getProperties());
    }

    @AfterAll
    public static void afterAll() {
        emf.close();
    }

    @Test
    public void persist() throws Exception {
        final EntityManager em = emf.createEntityManager();
        final EntityTransaction tx = em.getTransaction();
        tx.begin();

        BookSample book = new BookSample();
        book.setTitle("Marc");
        em.persist(book);

        Assertions.assertNotNull(book.getId());
        tx.commit();

        tx.begin();
        book.setTitle("The Guardian");
        tx.commit();

        BookSample b = em.find(BookSample.class, book.getId());
        Assertions.assertNotNull(b);

        em.close();
        emf.close();
    }

}
