package com.test.hibernate;

import com.test.hibernate.model.Continent;
import com.test.hibernate.model.Country;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Antonio Damato <anto.damato@gmail.com>
 */
public class OneToManyLombokBidTest {

    private static EntityManagerFactory emf;

    @BeforeAll
    public static void beforeAll() {
        emf = Persistence.createEntityManagerFactory("bid_lombok_onetomany",
                PersistenceUnitProperties.getProperties());
    }

    @AfterAll
    public static void afterAll() {
        emf.close();
    }

    @Test
    public void persist() throws Exception {
        final EntityManager em = emf.createEntityManager();

        Continent continent = new Continent();
        continent.setName("Africa");

        Country ghana = new Country();
        ghana.setName("Ghana");
        ghana.setContinent(continent);

        Country gambia = new Country();
        gambia.setName("Gambia");
        gambia.setContinent(continent);

        continent.setCountries(Set.of(ghana, gambia));

        em.getTransaction().begin();
        em.persist(continent);
        em.persist(ghana);
        em.persist(gambia);

        Continent ghanaContinent = ghana.getContinent();
        Assertions.assertNotNull(ghanaContinent);
        Assertions.assertEquals(ghanaContinent.getId(), continent.getId());

        Set<Country> countries = continent.getCountries();
        Assertions.assertNotNull(countries);
        Assertions.assertEquals(2, countries.size());

        em.getTransaction().commit();

        em.detach(continent);
        em.detach(gambia);
        em.detach(ghana);

        em.getTransaction().begin();
        Continent continentNew = em.find(Continent.class, continent.getId());
        Assertions.assertNotNull(continentNew);
        Set<Country> continentCountries = continentNew.getCountries();
        Assertions.assertNotNull(continentCountries);
        Assertions.assertEquals(2, continentCountries.size());

        Iterator<Country> it = continentCountries.iterator();
        Country c1 = it.next();
        Assertions.assertEquals(continentNew, c1.getContinent());
        Country c2 = it.next();
        Assertions.assertEquals(continentNew, c2.getContinent());

        em.remove(c1);
        em.remove(c2);
        em.remove(continentNew);
        em.getTransaction().commit();

        em.close();
    }
}
