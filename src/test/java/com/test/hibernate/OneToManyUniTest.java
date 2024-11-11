package com.test.hibernate;

import com.test.hibernate.model.onetomany.Item;
import com.test.hibernate.model.onetomany.Item_;
import com.test.hibernate.model.onetomany.Store;
import com.test.hibernate.model.onetomany.Store_;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Antonio Damato <anto.damato@gmail.com>
 */
public class OneToManyUniTest {

    private static final Logger LOG = LogManager.getLogger(OneToManyUniTest.class);

    private static EntityManagerFactory emf;

    @BeforeAll
    public static void beforeAll() {
        emf = Persistence.createEntityManagerFactory("onetomany_uni",
                PersistenceUnitProperties.getProperties());
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

        tx.begin();
        Store s = em.find(Store.class, store.getId());
        Assertions.assertTrue(!s.getItems().isEmpty());
        Assertions.assertEquals(2, s.getItems().size());
        Assertions.assertFalse(s == store);
        removeStore(s.getId(), em);
        tx.commit();

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

        tx.begin();
        Store s = em.find(Store.class, store.getId());
        Assertions.assertTrue(!s.getItems().isEmpty());
        Assertions.assertEquals(3, s.getItems().size());
        Assertions.assertFalse(s == store);
        removeStore(store.getId(), em);
        tx.commit();

        em.close();
    }

    @Test
    public void likeCriteria() throws Exception {
        final EntityManager em = emf.createEntityManager();
        Store store = new Store();
        store.setName("Upton Store");

        Item item1 = new Item();
        item1.setName("Notepad");
        item1.setModel("Free_Inch");

        Item item2 = new Item();
        item2.setName("Pencil");
        item2.setModel("Staedtler%Walls");

        Item item3 = new Item();
        item3.setName("Pen");
        item3.setModel("Pen");

        store.setItems(Arrays.asList(item1, item2, item3));

        final EntityTransaction tx = em.getTransaction();
        tx.begin();

        em.persist(item1);
        em.persist(store);
        em.persist(item2);
        em.persist(item3);

        tx.commit();

        Assertions.assertFalse(store.getItems().isEmpty());

        em.detach(store);

        tx.begin();
        testLike1(em);
        tx.commit();

        tx.begin();
        testLike2(em);
        tx.commit();

        tx.begin();
        testLike3(em);
        tx.commit();

        tx.begin();
        testLike4(em);
        tx.commit();

        tx.begin();
        testLike5(em);
        tx.commit();

        tx.begin();
        removeStore(store.getId(), em);
        tx.commit();

        em.close();
    }

    private void testLike1(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Item> criteriaQuery = cb.createQuery(Item.class);
        Root<Item> root = criteriaQuery.from(Item.class);
        Predicate predicate = cb.like(root.get("model"), "Free\\_I%", '\\');
        criteriaQuery.where(predicate);
        criteriaQuery.select(root);
        TypedQuery<Item> typedQuery = em.createQuery(criteriaQuery);
        List<Item> items = typedQuery.getResultList();
        Assertions.assertEquals(1, items.size());
        Item item = items.get(0);
        Assertions.assertEquals("Notepad", item.getName());
        Assertions.assertEquals("Free_Inch", item.getModel());
    }

    private void testLike2(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Item> criteriaQuery = cb.createQuery(Item.class);
        Root<Item> root = criteriaQuery.from(Item.class);
        Predicate predicate = cb.like(root.get("model"), root.get("name"), '\\');
        criteriaQuery.where(predicate);
        criteriaQuery.select(root);
        TypedQuery<Item> typedQuery = em.createQuery(criteriaQuery);
        List<Item> items = typedQuery.getResultList();
        Assertions.assertEquals(1, items.size());
        Assertions.assertEquals("Pen", items.get(0).getName());
        Assertions.assertEquals("Pen", items.get(0).getModel());
    }

    private void testLike3(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Item> criteriaQuery = cb.createQuery(Item.class);
        Root<Item> root = criteriaQuery.from(Item.class);
        Predicate predicate = cb.like(root.get("model"), cb.parameter(String.class, "pattern"), '\\');
        criteriaQuery.where(predicate);
        criteriaQuery.select(root);
        TypedQuery<Item> typedQuery = em.createQuery(criteriaQuery);
        typedQuery.setParameter("pattern", "Free\\_I%");
        List<Item> items = typedQuery.getResultList();
        Assertions.assertEquals(1, items.size());
        Item item = items.get(0);
        Assertions.assertEquals("Notepad", item.getName());
        Assertions.assertEquals("Free_Inch", item.getModel());
    }

    private void testLike4(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Item> criteriaQuery = cb.createQuery(Item.class);
        Root<Item> root = criteriaQuery.from(Item.class);
        Predicate predicate = cb.like(root.get("model"), root.get("name"),
                cb.parameter(Character.class, "escape"));
        criteriaQuery.where(predicate);
        criteriaQuery.select(root);
        TypedQuery<Item> typedQuery = em.createQuery(criteriaQuery);
        typedQuery.setParameter("escape", '\\');
        List<Item> items = typedQuery.getResultList();
        Assertions.assertEquals(1, items.size());
        Assertions.assertEquals("Pen", items.get(0).getName());
        Assertions.assertEquals("Pen", items.get(0).getModel());
    }

    private void testLike5(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Item> criteriaQuery = cb.createQuery(Item.class);
        Root<Item> root = criteriaQuery.from(Item.class);
        Predicate predicate = cb.like(root.get("model"), "Free\\_I%",
                cb.parameter(Character.class, "escape"));
        criteriaQuery.where(predicate);
        criteriaQuery.select(root);
        TypedQuery<Item> typedQuery = em.createQuery(criteriaQuery);
        typedQuery.setParameter("escape", '\\');
        List<Item> items = typedQuery.getResultList();
        Assertions.assertEquals(1, items.size());
        Item item = items.get(0);
        Assertions.assertEquals("Notepad", item.getName());
        Assertions.assertEquals("Free_Inch", item.getModel());
    }

    @Test
    public void notLikeCriteria() throws Exception {
        final EntityManager em = emf.createEntityManager();
        Store store = new Store();
        store.setName("Upton Store");

        Item item1 = new Item();
        item1.setName("Notepad");
        item1.setModel("Free_Inch");

        Item item2 = new Item();
        item2.setName("Pencil");
        item2.setModel("Staedtler%Walls");

        Item item3 = new Item();
        item3.setName("Pen");
        item3.setModel("Pen");

        store.setItems(Arrays.asList(item1, item2, item3));

        final EntityTransaction tx = em.getTransaction();
        tx.begin();

        em.persist(item1);
        em.persist(store);
        em.persist(item2);
        em.persist(item3);

        tx.commit();

        Assertions.assertFalse(store.getItems().isEmpty());

        em.detach(store);

        tx.begin();
        testNotLike1(em);
        tx.commit();

        tx.begin();
        testNotLike2(em);
        tx.commit();

        tx.begin();
        testNotLike3(em);
        tx.commit();

        tx.begin();
        testNotLike4(em);
        tx.commit();

        tx.begin();
        testNotLike5(em);
        tx.commit();

        tx.begin();
        removeStore(store.getId(), em);
        tx.commit();

        em.close();
    }

    private void testNotLike1(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Item> criteriaQuery = cb.createQuery(Item.class);
        Root<Item> root = criteriaQuery.from(Item.class);
        Predicate predicate = cb.notLike(root.get("model"), "Free\\_I%", '\\');
        criteriaQuery.where(predicate).orderBy(cb.asc(root.get("name")));
        criteriaQuery.select(root);
        TypedQuery<Item> typedQuery = em.createQuery(criteriaQuery);
        List<Item> items = typedQuery.getResultList();
        Assertions.assertEquals(2, items.size());
        Assertions.assertEquals("Pen", items.get(0).getName());
        Assertions.assertEquals("Pen", items.get(0).getModel());
        Assertions.assertEquals("Pencil", items.get(1).getName());
        Assertions.assertEquals("Staedtler%Walls", items.get(1).getModel());
    }

    private void testNotLike2(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Item> criteriaQuery = cb.createQuery(Item.class);
        Root<Item> root = criteriaQuery.from(Item.class);
        Predicate predicate = cb.notLike(root.get("model"), root.get("name"), '\\');
        criteriaQuery.where(predicate);
        criteriaQuery.select(root);
        TypedQuery<Item> typedQuery = em.createQuery(criteriaQuery);
        List<Item> items = typedQuery.getResultList();
        Assertions.assertEquals(2, items.size());
        Assertions.assertEquals("Notepad", items.get(0).getName());
        Assertions.assertEquals("Free_Inch", items.get(0).getModel());
        Assertions.assertEquals("Pencil", items.get(1).getName());
        Assertions.assertEquals("Staedtler%Walls", items.get(1).getModel());
    }

    private void testNotLike3(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Item> criteriaQuery = cb.createQuery(Item.class);
        Root<Item> root = criteriaQuery.from(Item.class);
        Predicate predicate = cb.notLike(root.get("model"), cb.parameter(String.class, "pattern"),
                '\\');
        criteriaQuery.where(predicate).orderBy(cb.asc(root.get("name")));
        criteriaQuery.select(root);
        TypedQuery<Item> typedQuery = em.createQuery(criteriaQuery);
        typedQuery.setParameter("pattern", "Free\\_I%");
        List<Item> items = typedQuery.getResultList();
        Assertions.assertEquals(2, items.size());
        Assertions.assertEquals("Pen", items.get(0).getName());
        Assertions.assertEquals("Pen", items.get(0).getModel());
        Assertions.assertEquals("Pencil", items.get(1).getName());
        Assertions.assertEquals("Staedtler%Walls", items.get(1).getModel());
    }

    private void testNotLike4(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Item> criteriaQuery = cb.createQuery(Item.class);
        Root<Item> root = criteriaQuery.from(Item.class);
        Predicate predicate = cb.notLike(root.get("model"), root.get("name"),
                cb.parameter(Character.class, "escape"));
        criteriaQuery.where(predicate);
        criteriaQuery.select(root);
        TypedQuery<Item> typedQuery = em.createQuery(criteriaQuery);
        typedQuery.setParameter("escape", '\\');
        List<Item> items = typedQuery.getResultList();
        Assertions.assertEquals(2, items.size());
        Assertions.assertEquals("Notepad", items.get(0).getName());
        Assertions.assertEquals("Free_Inch", items.get(0).getModel());
        Assertions.assertEquals("Pencil", items.get(1).getName());
        Assertions.assertEquals("Staedtler%Walls", items.get(1).getModel());
    }

    private void testNotLike5(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Item> criteriaQuery = cb.createQuery(Item.class);
        Root<Item> root = criteriaQuery.from(Item.class);
        Predicate predicate = cb.notLike(root.get("model"), "Free\\_I%",
                cb.parameter(Character.class, "escape"));
        criteriaQuery.where(predicate).orderBy(cb.asc(root.get("name")));
        criteriaQuery.select(root);
        TypedQuery<Item> typedQuery = em.createQuery(criteriaQuery);
        typedQuery.setParameter("escape", '\\');
        List<Item> items = typedQuery.getResultList();
        Assertions.assertEquals(2, items.size());
        Assertions.assertEquals("Pen", items.get(0).getName());
        Assertions.assertEquals("Pen", items.get(0).getModel());
        Assertions.assertEquals("Pencil", items.get(1).getName());
        Assertions.assertEquals("Staedtler%Walls", items.get(1).getModel());
    }

    @Test
    public void join() throws Exception {
        final EntityManager em = emf.createEntityManager();
        Store store = new Store();
        store.setName("Upton Store");

        Item item1 = new Item();
        item1.setName("Notepad");
        item1.setModel("Free Inch");

        Item item2 = new Item();
        item2.setName("Pencil");
        item2.setModel("Staedtler");

        Item item3 = new Item();
        item3.setName("Pen");
        item3.setModel("Castles");

        store.setItems(Arrays.asList(item1, item2, item3));

        final EntityTransaction tx = em.getTransaction();
        tx.begin();

        em.persist(item1);
        em.persist(store);
        em.persist(item2);
        em.persist(item3);

        tx.commit();

        Assertions.assertFalse(store.getItems().isEmpty());

        em.detach(store);

        tx.begin();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Store> cq = cb.createQuery(Store.class);
        Root<Store> root = cq.from(Store.class);
        Join<Object, Object> item = root.join(Store_.ITEMS);

        ParameterExpression<String> pTitle = cb.parameter(String.class);
        cq.where(cb.like(item.get(Item_.MODEL), pTitle));
//    cq.where(cb.like(root.get("name"), pTitle));

        TypedQuery<Store> q = em.createQuery(cq);
        q.setParameter(pTitle, "%Castles%");
        List<Store> stores = q.getResultList();
        tx.commit();
        Assertions.assertFalse(stores.isEmpty());
        Assertions.assertEquals(1, stores.size());
        Store s = stores.get(0);
        Assertions.assertEquals("Upton Store", s.getName());
        Assertions.assertFalse(s == store);

        Collection<Item> items = s.getItems();
        Assertions.assertEquals(3, items.size());

        em.detach(store);
        tx.begin();
        removeStore(store.getId(), em);
        tx.commit();

        em.close();
    }

    @Test
    public void joinDoubleStore() throws Exception {
        final EntityManager em = emf.createEntityManager();
        Store store = new Store();
        store.setName("Upton Store");

        Item item1 = new Item();
        item1.setName("Notepad");
        item1.setModel("Free Inch");

        Item item2 = new Item();
        item2.setName("Pencil");
        item2.setModel("Staedtler");

        Item item3 = new Item();
        item3.setName("Pen");
        item3.setModel("Castles");

        Item item4 = new Item();
        item4.setName("Pencil");
        item4.setModel("Castles");

        store.setItems(Arrays.asList(item1, item2, item3, item4));

        final EntityTransaction tx = em.getTransaction();
        tx.begin();

        em.persist(item1);
        em.persist(store);
        em.persist(item2);
        em.persist(item3);
        em.persist(item4);

        tx.commit();

        Assertions.assertFalse(store.getItems().isEmpty());

        em.detach(store);

        tx.begin();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Store> cq = cb.createQuery(Store.class);
        Root<Store> root = cq.from(Store.class);
        Join<Object, Object> item = root.join(Store_.ITEMS);

        ParameterExpression<String> pTitle = cb.parameter(String.class);
        cq.where(cb.like(item.get(Item_.MODEL), pTitle));

        TypedQuery<Store> q = em.createQuery(cq);
        q.setParameter(pTitle, "%Castles%");
        List<Store> stores = q.getResultList();
        tx.commit();
        Assertions.assertFalse(stores.isEmpty());
        Assertions.assertEquals(2, stores.size());
        Store s1 = stores.get(0);
        Assertions.assertEquals("Upton Store", s1.getName());
        Assertions.assertFalse(s1 == store);

        Store s2 = stores.get(1);
        Assertions.assertEquals("Upton Store", s2.getName());
        Assertions.assertFalse(s2 == store);

        Assertions.assertTrue(s1 == s2);

        em.detach(store);
        tx.begin();
        removeStore(store.getId(), em);
        tx.commit();

        em.close();
    }

    @Test
    public void leftJoin() throws Exception {
        final EntityManager em = emf.createEntityManager();
        Store store = new Store();
        store.setName("Upton Store");

        Item item1 = new Item();
        item1.setName("Notepad");
        item1.setModel("Free Inch");

        Item item2 = new Item();
        item2.setName("Pencil");
        item2.setModel("Staedtler");

        Item item3 = new Item();
        item3.setName("Pen");
        item3.setModel("Castles");

        Item item4 = new Item();
        item4.setName("Notepad");
        item4.setModel("Free Inch");

        Item item5 = new Item();
        item5.setName("Pencil");
        item5.setModel("Staedtler");

        store.setItems(Arrays.asList(item1, item2, item3));

        Store store2 = new Store();
        store2.setName("Upton Store 2nd");
        store2.setItems(Arrays.asList(item4, item5));

        Store store3 = new Store();
        store3.setName("Upton Store 3rd");

        final EntityTransaction tx = em.getTransaction();
        tx.begin();

        em.persist(item1);
        em.persist(store);
        em.persist(store2);
        em.persist(store3);
        em.persist(item2);
        em.persist(item3);
        em.persist(item4);
        em.persist(item5);

        tx.commit();

        Assertions.assertFalse(store.getItems().isEmpty());

        em.detach(store);
        em.detach(store2);
        em.detach(store3);

        tx.begin();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Store> cq = cb.createQuery(Store.class);
        Root<Store> root = cq.from(Store.class);
        Join<Object, Object> item = root.join("items", JoinType.LEFT);

        cq.distinct(true);
        TypedQuery<Store> q = em.createQuery(cq);
        List<Store> stores = q.getResultList();
        tx.commit();

        Assertions.assertFalse(stores.isEmpty());
        Assertions.assertEquals(3, stores.size());
        Store s = stores.get(0);
        Assertions.assertEquals("Upton Store", s.getName());
        Assertions.assertFalse(s == store);
        Assertions.assertEquals("Upton Store 2nd", stores.get(1).getName());
        Assertions.assertEquals("Upton Store 3rd", stores.get(2).getName());

        Collection<Item> items = s.getItems();
        Assertions.assertEquals(3, items.size());

        em.detach(store);
        em.detach(store2);
        em.detach(store3);

        tx.begin();
        removeStore(store.getId(), em);
        removeStore(store2.getId(), em);
        removeStore(store3.getId(), em);
        tx.commit();

        em.close();
    }


    @Test
    public void fetchJoin() throws Exception {
        final EntityManager em = emf.createEntityManager();
        Store store = new Store();
        store.setName("Upton Store");

        Item item1 = new Item();
        item1.setName("Notepad");
        item1.setModel("Free Inch");

        Item item2 = new Item();
        item2.setName("Pencil");
        item2.setModel("Castles");

        Item item3 = new Item();
        item3.setName("Pen");
        item3.setModel("Castles");

        store.setItems(Arrays.asList(item1, item2, item3));

        final EntityTransaction tx = em.getTransaction();
        tx.begin();

        em.persist(item1);
        em.persist(store);
        em.persist(item2);
        em.persist(item3);

        tx.commit();

        Assertions.assertFalse(store.getItems().isEmpty());

        em.detach(store);

        tx.begin();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Store> cq = cb.createQuery(Store.class);
        Root<Store> root = cq.from(Store.class);
        Join<Object, Object> item = (Join<Object, Object>) root.fetch(Store_.ITEMS);

        ParameterExpression<String> pTitle = cb.parameter(String.class);
        cq.where(cb.like(item.get(Item_.MODEL), pTitle));

        cq.select(root).distinct(true);
//    cq.distinct(true);
        TypedQuery<Store> q = em.createQuery(cq);
        q.setParameter(pTitle, "%Castles%");
        List<Store> stores = q.getResultList();
        tx.commit();
        Assertions.assertFalse(stores.isEmpty());

//    // TEMP
//    Assertions.assertEquals(2, stores.size());
//    Store s1 = stores.get(0);
//    Store s2 = stores.get(1);
//    Assertions.assertEquals("Upton Store", s1.getName());
//    Assertions.assertEquals("Upton Store", s2.getName());
//    Assertions.assertFalse(s1 == store);
//    Assertions.assertFalse(s1 == s2);
//    // TEMP

        Assertions.assertEquals(1, stores.size());
        Store s = stores.get(0);
        Assertions.assertEquals("Upton Store", s.getName());
        Assertions.assertFalse(s == store);

        Collection<Item> items = s.getItems();
        Assertions.assertEquals(2, items.size());

        em.detach(store);
        tx.begin();
        Store sr = em.find(Store.class, store.getId());
        em.remove(sr);
        em.remove(item1);
        em.remove(item2);
        em.remove(item3);
        tx.commit();

        em.close();
    }

    @Test
    public void jpqlLeftJoin() throws Exception {
        final EntityManager em = emf.createEntityManager();
        Store store = new Store();
        store.setName("Upton Store");

        Item item1 = new Item();
        item1.setName("Notepad");
        item1.setModel("Free Inch");

        Item item2 = new Item();
        item2.setName("Pencil");
        item2.setModel("Staedtler");

        Item item3 = new Item();
        item3.setName("Pen");
        item3.setModel("Castles");

        Item item4 = new Item();
        item4.setName("Notepad");
        item4.setModel("Free Inch");

        Item item5 = new Item();
        item5.setName("Pencil");
        item5.setModel("Staedtler");

        store.setItems(Arrays.asList(item1, item2, item3));

        Store store2 = new Store();
        store2.setName("Upton Store 2nd");
        store2.setItems(Arrays.asList(item4, item5));

        Store store3 = new Store();
        store3.setName("Upton Store 3rd");

        final EntityTransaction tx = em.getTransaction();
        tx.begin();

        em.persist(item1);
        em.persist(store);
        em.persist(store2);
        em.persist(store3);
        em.persist(item2);
        em.persist(item3);
        em.persist(item4);
        em.persist(item5);

        tx.commit();

        Assertions.assertFalse(store.getItems().isEmpty());

        em.detach(store);
        em.detach(store2);
        em.detach(store3);

        tx.begin();
        Query q = em.createQuery("select distinct s from Store s left outer join s.items i");
        List<Store> stores = q.getResultList();
        tx.commit();

        Assertions.assertFalse(stores.isEmpty());
        Assertions.assertEquals(3, stores.size());
        Store s = stores.get(0);
        Assertions.assertEquals("Upton Store", s.getName());
        Assertions.assertFalse(s == store);
        Assertions.assertEquals("Upton Store 2nd", stores.get(1).getName());
        Assertions.assertEquals("Upton Store 3rd", stores.get(2).getName());

        Collection<Item> items = s.getItems();
        Assertions.assertEquals(3, items.size());

        em.detach(store);
        em.detach(store2);
        em.detach(store3);

        tx.begin();
        removeStore(store.getId(), em);
        removeStore(store2.getId(), em);
        removeStore(store3.getId(), em);
        tx.commit();

        em.close();
    }

    @Test
    public void jpqlFetchJoin() throws Exception {
        final EntityManager em = emf.createEntityManager();
        Store store = new Store();
        store.setName("Upton Store");

        Item item1 = new Item();
        item1.setName("Notepad");
        item1.setModel("Free Inch");

        Item item2 = new Item();
        item2.setName("Pencil");
        item2.setModel("Castles");

        Item item3 = new Item();
        item3.setName("Pen");
        item3.setModel("Castles");

        store.setItems(Arrays.asList(item1, item2, item3));

        final EntityTransaction tx = em.getTransaction();
        tx.begin();

        em.persist(item1);
        em.persist(store);
        em.persist(item2);
        em.persist(item3);

        tx.commit();

        Assertions.assertFalse(store.getItems().isEmpty());

        em.detach(store);

        tx.begin();
        Query q = em.createQuery("select distinct s from Store s inner join fetch s.items i where i.model like :title");
        q.setParameter("title", "%Castles%");
        List<Store> stores = q.getResultList();
        tx.commit();
        Assertions.assertFalse(stores.isEmpty());

        Assertions.assertEquals(1, stores.size());
        Store s = stores.get(0);
        Assertions.assertEquals("Upton Store", s.getName());
        Assertions.assertFalse(s == store);

        Collection<Item> items = s.getItems();
        Assertions.assertEquals(2, items.size());

        em.detach(store);
        tx.begin();
        Store sr = em.find(Store.class, store.getId());
        em.remove(sr);
        em.remove(item1);
        em.remove(item2);
        em.remove(item3);
        tx.commit();

        em.close();
    }

    private void removeStore(long id, EntityManager em) {
        Store sr = em.find(Store.class, id);
        Collection<Item> items = sr.getItems();
        em.remove(sr);
        items.forEach(em::remove);
    }

}
