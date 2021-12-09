package com.test.hibernate;

import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.test.hibernate.model.onetomany.Item;
import com.test.hibernate.model.onetomany.Store;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

/**
 * java -jar $DERBY_HOME/lib/derbyrun.jar server start
 *
 * connect 'jdbc:derby://localhost:1527/test';
 *
 * @author adamato
 *
 */
public class OneToManyUniTest {

    private static EntityManagerFactory emf;

    @BeforeAll
    public static void beforeAll() {
	emf = Persistence.createEntityManagerFactory("onetomany_uni", PersistenceUnitProperties.getProperties());
    }

    @AfterAll
    public static void afterAll() {
	emf.close();
    }

    @Test
    public void persist() throws Exception {
	final EntityManager em = emf.createEntityManager();
	Store store = new Store();
	store.setName("Upton Store");

	Item item1 = new Item();
	item1.setName("Notepad");
	item1.setModel("Free Inch");

	Item item2 = new Item();
	item2.setName("Pencil");
	item2.setModel("Staedtler");

	store.setItems(Arrays.asList(item1, item2));

	final EntityTransaction tx = em.getTransaction();
	tx.begin();

	em.persist(item1);
	em.persist(store);
	em.persist(item2);

	tx.commit();

	Assertions.assertFalse(store.getItems().isEmpty());

	em.detach(store);

	Store s = em.find(Store.class, store.getId());
	Assertions.assertTrue(!s.getItems().isEmpty());
	Assertions.assertEquals(2, s.getItems().size());
	Assertions.assertFalse(s == store);

	em.close();
    }

    @Test
    public void persistCollection() throws Exception {
	final EntityManager em = emf.createEntityManager();
	Store store = new Store();
	store.setName("Upton Store");

	Item item1 = new Item();
	item1.setName("Notepad");
	item1.setModel("Free Inch");

	Item item2 = new Item();
	item2.setName("Pencil");
	item2.setModel("Staedtler");

	List list = new ArrayList();
	list.add(item1);
	list.add(item2);
	store.setItems(list);

	final EntityTransaction tx = em.getTransaction();
	tx.begin();

	em.persist(item1);
	em.persist(store);
	em.persist(item2);

	tx.commit();

	tx.begin();
	Item item3 = new Item();
	item3.setName("Pen");
	item3.setModel("Bic");
	em.persist(item3);

	store.getItems().add(item3);
	em.persist(store);

	tx.commit();

	Assertions.assertFalse(store.getItems().isEmpty());

	em.detach(store);

	Store s = em.find(Store.class, store.getId());
	Assertions.assertTrue(!s.getItems().isEmpty());
	Assertions.assertEquals(3, s.getItems().size());
	Assertions.assertFalse(s == store);

	em.close();
    }
}
