package com.test.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.test.hibernate.model.BookSample;

/**
 * java -jar $DERBY_HOME/lib/derbyrun.jar server start
 *
 * connect 'jdbc:derby://localhost:1527/test';
 *
 * @author adamato
 *
 */
public class BookTest {

    @Test
    public void persist() throws Exception {
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("books", PersistenceUnitProperties.getProperties());
	final EntityManager em = emf.createEntityManager();
	final EntityTransaction tx = em.getTransaction();
	tx.begin();

	BookSample book = new BookSample();
	book.setTitle("Marc");
	em.persist(book);

	System.out.println("BookTest.persist: book.getId()=" + book.getId());

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
