package com.test.hibernate;

import com.test.hibernate.model.StoreItem;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.Assertions;

/**
 *
 * @author adamato
 *
 */
public class PessimisticLockTest {

    private Logger LOG = Logger.getLogger(PessimisticLockTest.class);
    private static EntityManagerFactory emf;

    @BeforeAll
    public static void beforeAll() {
	emf = Persistence.createEntityManagerFactory("items", PersistenceUnitProperties.getProperties());
    }

    @AfterAll
    public static void afterAll() {
	emf.close();
    }

    @Test
    public void pessimisticLock() throws Exception {
	final EntityManager em = emf.createEntityManager();
	final EntityTransaction tx = em.getTransaction();
	tx.begin();
	final StoreItem storeItem = new StoreItem();
	storeItem.setAmount(10L);
	em.persist(storeItem);
	tx.commit();

	int nth = 10;
	ExecutorService executorService = Executors.newFixedThreadPool(nth);
	for (int i = 0; i < nth; ++i) {
	    executorService.submit(() -> {
		modifyStoreItem(storeItem.getId(), 10);
	    });
	}

	Thread.sleep(10000);
	tx.begin();
	em.refresh(storeItem);
	Assertions.assertEquals(110, storeItem.getAmount());
	tx.commit();

//	CompletableFuture.runAsync(() -> {
//	    for (int i = 0; i < 5; ++i) {
//		try {
//		    System.out.println("pessimisticLock " + i);
//		    Thread.sleep(1000);
//		} catch (InterruptedException ex) {
//		    System.out.println("pessimisticLock Interrupted");
//		}
//	    }
//	}).thenApply(v -> {
//	    for (int i = 0; i < 5; ++i) {
//		try {
//		    System.out.println("pessimisticLock 2 " + i);
//		    Thread.sleep(1000);
//		} catch (InterruptedException ex) {
//		    System.out.println("pessimisticLock Interrupted");
//		}
//	    }
//	    return null;
//	});
//
////	Thread.sleep(10000);
//	System.out.println("pessimisticLock Completed");
    }

    private void modifyStoreItem(Long id, int amountToAdd) {
	final EntityManager em = emf.createEntityManager();
	final EntityTransaction tx = em.getTransaction();
	tx.begin();
	StoreItem storeItem = em.find(StoreItem.class, id, LockModeType.PESSIMISTIC_WRITE);
	long value = storeItem.getAmount() + amountToAdd;
	LOG.info("modifyStoreItem: value=" + value);
	storeItem.setAmount(value);
	tx.commit();
	em.close();
    }
}
