package com.test.hibernate;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.Bindable.BindableType;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.Type.PersistenceType;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.test.hibernate.model.Book;
import com.test.hibernate.model.BookFormat;

/**
 * @author Antonio Damato <anto.damato@gmail.com>
 */
public class EmbBookTest {

    private static EntityManagerFactory emf;

    @BeforeAll
    public static void beforeAll() {
        emf = Persistence.createEntityManagerFactory("emb_books", PersistenceUnitProperties.getProperties());
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

        Book book = BookUtils.create1stFreudBook();
        em.persist(book);

        Assertions.assertNotNull(book.getId());
        tx.commit();

        Book b = em.find(Book.class, book.getId());
        Assertions.assertTrue(b == book);
        Assertions.assertNotNull(b);
        BookFormat format = b.getBookFormat();
        Assertions.assertNotNull(format);
        Assertions.assertEquals(688, format.getPages());

        em.detach(book);
        b = em.find(Book.class, book.getId());
        Assertions.assertFalse(b == book);
        Assertions.assertNotNull(b);

        Book b2 = em.find(Book.class, b.getId());
        Assertions.assertTrue(b2 == b);

        tx.begin();
        em.remove(b2);
        tx.commit();

        em.close();
    }

    @Test
    public void metamodel() {
        final EntityManager em = emf.createEntityManager();
        Metamodel metamodel = em.getMetamodel();
        Assertions.assertNotNull(metamodel);

        Set<EntityType<?>> entityTypes = metamodel.getEntities();
        Assertions.assertEquals(1, entityTypes.size());

        List<String> names = entityTypes.stream().map(e -> e.getName()).collect(Collectors.toList());
        Assertions.assertTrue(CollectionUtils.containsAll(Arrays.asList("Book"), names));

        for (EntityType<?> entityType : entityTypes) {
            if (entityType.getName().equals("Book")) {
                checkBook(entityType);
            }
        }

        Set<ManagedType<?>> managedTypes = metamodel.getManagedTypes();
        Assertions.assertEquals(2, managedTypes.size());

        Set<EmbeddableType<?>> embeddableTypes = metamodel.getEmbeddables();
        Assertions.assertEquals(1, embeddableTypes.size());
        checkBookFormat(embeddableTypes.iterator().next());

        em.close();
    }

    private void checkBook(EntityType<?> entityType) {
        Assertions.assertEquals("Book", entityType.getName());
        MetamodelUtils.checkType(entityType, Book.class, PersistenceType.ENTITY);
        MetamodelUtils.checkType(entityType.getIdType(), Long.class, PersistenceType.BASIC);

        Assertions.assertEquals(BindableType.ENTITY_TYPE, entityType.getBindableType());
        Assertions.assertEquals(Book.class, entityType.getBindableJavaType());

        List<String> names = MetamodelUtils.getAttributeNames(entityType);
        Assertions.assertTrue(CollectionUtils.containsAll(Arrays.asList("id", "title", "author", "bookFormat"), names));

        MetamodelUtils.checkAttribute(entityType.getAttribute("title"), "title", String.class,
                PersistentAttributeType.BASIC, false, false);
        MetamodelUtils.checkAttribute(entityType.getAttribute("author"), "author", String.class,
                PersistentAttributeType.BASIC, false, false);
    }

    private void checkBookFormat(EmbeddableType<?> embeddableType) {
        MetamodelUtils.checkType(embeddableType, BookFormat.class, PersistenceType.EMBEDDABLE);

        List<String> names = MetamodelUtils.getAttributeNames(embeddableType);
        Assertions.assertTrue(CollectionUtils.containsAll(Arrays.asList("format", "pages"), names));

        MetamodelUtils.checkAttribute(embeddableType.getAttribute("format"), "format", String.class,
                PersistentAttributeType.BASIC, false, false);
        MetamodelUtils.checkAttribute(embeddableType.getAttribute("pages"), "pages", Integer.class,
                PersistentAttributeType.BASIC, false, false);
    }

    @Test
    public void distinct() throws Exception {
        final EntityManager em = emf.createEntityManager();
        final EntityTransaction tx = em.getTransaction();
        tx.begin();

        Book book1 = BookUtils.create1stFreudBook();
        em.persist(book1);
        Book book2 = BookUtils.create2ndFreudBook();
        em.persist(book2);
        Book book3 = BookUtils.create1stJoyceBook();
        em.persist(book3);
        Book book4 = BookUtils.create1stLondonBook();
        em.persist(book4);
        Book book5 = BookUtils.create2ndLondonBook();
        em.persist(book5);

        CriteriaQuery<String> query = em.getCriteriaBuilder().createQuery(String.class);
        Root<Book> root = query.from(Book.class);
        query.select(root.get("author")).distinct(true);
        TypedQuery<String> tq = em.createQuery(query);
        List<String> resultList = tq.getResultList();
        Assertions.assertEquals(3, resultList.size());
        Assertions.assertTrue(
                CollectionUtils.containsAll(Arrays.asList("Sigmund Freud", "James Joyce", "Jack London"), resultList));

        tx.commit();

        tx.begin();
        em.remove(book1);
        em.remove(book2);
        em.remove(book3);
        em.remove(book4);
        em.remove(book5);
        tx.commit();

        em.close();
    }

    @Test
    public void count() throws Exception {
        final EntityManager em = emf.createEntityManager();
        final EntityTransaction tx = em.getTransaction();
        tx.begin();

        Book book1 = BookUtils.create1stFreudBook();
        em.persist(book1);
        Book book2 = BookUtils.create2ndFreudBook();
        em.persist(book2);
        Book book3 = BookUtils.create1stJoyceBook();
        em.persist(book3);
        Book book4 = BookUtils.create1stLondonBook();
        em.persist(book4);
        Book book5 = BookUtils.create2ndLondonBook();
        em.persist(book5);
        tx.commit();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery query = cb.createQuery();
        Root<Book> root = query.from(Book.class);
        query.select(cb.count(root));
        TypedQuery<?> typedQuery = em.createQuery(query);
        Object result = typedQuery.getSingleResult();
        Assertions.assertEquals(5L, result);

        query.select(cb.countDistinct(root.get("author")));
        typedQuery = em.createQuery(query);
        result = typedQuery.getSingleResult();
        Assertions.assertEquals(3L, result);

        tx.begin();
        em.remove(book1);
        em.remove(book2);
        em.remove(book3);
        em.remove(book4);
        em.remove(book5);
        tx.commit();

        em.close();
    }

    @Test
    public void equalInEmbedded() throws Exception {
        final EntityManager em = emf.createEntityManager();
        final EntityTransaction tx = em.getTransaction();
        tx.begin();

        Book book1 = BookUtils.create1stFreudBook();
        em.persist(book1);
        Book book2 = BookUtils.create2ndFreudBook();
        em.persist(book2);
        Book book3 = BookUtils.create1stJoyceBook();
        em.persist(book3);
        Book book4 = BookUtils.create1stLondonBook();
        em.persist(book4);
        Book book5 = BookUtils.create2ndLondonBook();
        em.persist(book5);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery query = cb.createQuery();
        Root<Book> root = query.from(Book.class);
        query.select(root);
        query.where(cb.equal(root.get("bookFormat").get("format"), "hardcover"));
        TypedQuery<?> typedQuery = em.createQuery(query);
        List<?> result = typedQuery.getResultList();
        Assertions.assertEquals(1, result.size());

        query.where(cb.in(root.get("bookFormat").get("format")).value("hardcover").value("electronic"));
        typedQuery = em.createQuery(query);
        result = typedQuery.getResultList();
        Assertions.assertEquals(3, result.size());

        em.remove(book1);
        em.remove(book2);
        em.remove(book3);
        em.remove(book4);
        em.remove(book5);
        tx.commit();

        em.close();
    }

}
