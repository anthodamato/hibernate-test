package com.test.hibernate;

import com.test.hibernate.model.Artist;
import com.test.hibernate.model.Movie;
import com.test.hibernate.model.Song;
import org.junit.jupiter.api.*;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Set;

public class MultipleJoinTest {

    private static EntityManagerFactory emf;

    @BeforeAll
    public static void beforeAll() throws Exception {
        emf = Persistence.createEntityManagerFactory("multiple_joins", PersistenceUnitProperties.getProperties());
    }

    @AfterAll
    public static void afterAll() {
        emf.close();
    }

    /**
     * Not working on Hibernate
     *
     * @throws Exception
     */
    @Disabled
    @Test
    public void multipleJoins() throws Exception {
        final EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        Artist artist = buildArtist();
        persistArtist(em, artist);
        tx.commit();

        tx.begin();
        em.detach(artist);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Artist> criteriaQuery = cb.createQuery(Artist.class);
        Root<Artist> root = criteriaQuery.from(Artist.class);
        root.fetch("movies");
        root.fetch("songs");
        criteriaQuery.distinct(true);
        TypedQuery<Artist> q = em.createQuery(criteriaQuery);
        List<Artist> artists = q.getResultList();

        Assertions.assertNotNull(artists);
        Assertions.assertEquals(1, artists.size());
        Assertions.assertEquals(3, artists.get(0).getMovies().size());
        Assertions.assertEquals(3, artists.get(0).getSongs().size());

        removeArtist(em, artists.get(0));
        tx.commit();

        em.close();
    }

    private Artist buildArtist() {
        Song song1 = new Song();
        song1.setName("That's All Right");

        Song song2 = new Song();
        song2.setName("Loving You");

        Song song3 = new Song();
        song3.setName("Blue Hawaii");

        Movie movie1 = new Movie();
        movie1.setName("Love Me Tender");

        Movie movie2 = new Movie();
        movie2.setName("Flaming Star");

        Movie movie3 = new Movie();
        movie3.setName("It Happened at the World's Fair");

        Artist artist = new Artist();
        artist.setName("Elvis Presley");
        artist.setMovies(Set.of(movie1, movie2, movie3));
        artist.setSongs(List.of(song1, song2, song3));
        return artist;
    }

    private void removeArtist(EntityManager em, Artist artist) {
        em.remove(artist);
        artist.getMovies().forEach(em::remove);
        artist.getSongs().forEach(em::remove);
    }

    private void persistArtist(EntityManager em, Artist artist) {
        artist.getMovies().forEach(em::persist);
        artist.getSongs().forEach(em::persist);
        em.persist(artist);
    }
}
