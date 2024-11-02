package com.test.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.test.hibernate.model.onetoone.lazy.Capital;
import com.test.hibernate.model.onetoone.lazy.State;

/**
 * @author Antonio Damato <anto.damato@gmail.com>
 */
public class OneToOneBidLazyTest {

    private static EntityManagerFactory emf;

    @BeforeAll
    public static void beforeAll() {
        emf = Persistence.createEntityManagerFactory("onetoone_bid_lazy",
                PersistenceUnitProperties.getProperties());
    }

    @AfterAll
    public static void afterAll() {
        emf.close();
    }

    @Test
    public void persist() throws Exception {
        final EntityManager em = emf.createEntityManager();
        try {
            final EntityTransaction tx = em.getTransaction();
            tx.begin();

            State state = new State();
            state.setName("England");

            Capital capital = new Capital();
            capital.setName("London");

            state.setCapital(capital);

            em.persist(capital);
            em.persist(state);

            tx.commit();

            em.detach(capital);
            em.detach(state);

            tx.begin();
            State s = em.find(State.class, state.getId());

            Assertions.assertFalse(s == state);
            Assertions.assertEquals("England", state.getName());
//			LOG.info("Loading s.getCapital()");
            Capital c = s.getCapital();
            Assertions.assertNotNull(c);
            Assertions.assertNotNull(c.getState());
            Assertions.assertEquals("London", c.getName());
            em.remove(c);
            em.remove(s);
            tx.commit();
        } finally {
            em.close();
            emf.close();
        }
    }

}
