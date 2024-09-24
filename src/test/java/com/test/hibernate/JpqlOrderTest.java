/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.test.hibernate;

import com.test.hibernate.model.LineItem;
import com.test.hibernate.model.SimpleOrder;
import com.test.hibernate.model.SimpleProduct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Antonio Damato <anto.damato@gmail.com>
 */
public class JpqlOrderTest {

    private static final Logger LOG = LogManager.getLogger(JpqlOrderTest.class);
    private static EntityManagerFactory emf;
    private static String testDb;

    @BeforeAll
    public static void beforeAll() {
        emf = Persistence.createEntityManagerFactory("simple_order", PersistenceUnitProperties.getProperties());
        testDb = System.getProperty("hibernate.test");
    }

    @AfterAll
    public static void afterAll() {
        emf.close();
    }

    @Test
    public void simpleOrder() throws Exception {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        SimpleOrder simpleOrder = persistEntities(em);
        tx.commit();

        tx.begin();
        Query query = em
                .createQuery("SELECT DISTINCT o FROM SimpleOrder AS o JOIN o.lineItems AS l WHERE l.shipped = FALSE");
        List list = query.getResultList();
        Assertions.assertFalse(list.isEmpty());
        Assertions.assertEquals(1, list.size());
        Object so = list.get(0);
        Assertions.assertTrue(so instanceof SimpleOrder);
        tx.commit();

        tx.begin();
        removeEntities((SimpleOrder) so, em);
        tx.commit();

        em.close();
    }

    @Test
    public void simpleOrderProductType() throws Exception {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        SimpleOrder simpleOrder = persistEntities(em);
        tx.commit();

        tx.begin();
        Query query = em
                .createQuery("SELECT DISTINCT o FROM SimpleOrder o JOIN o.lineItems l JOIN l.product p\n"
                        + "  WHERE p.productType = 'office_supplies'");
        List list = query.getResultList();
        Assertions.assertTrue(!list.isEmpty());
        Assertions.assertEquals(1, list.size());
        Object so = list.get(0);
        Assertions.assertTrue(so instanceof SimpleOrder);
        tx.commit();

        tx.begin();
        removeEntities((SimpleOrder) so, em);
        tx.commit();

        em.close();
    }

    @Test
    public void simpleOrderDates() throws Exception {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        SimpleOrder simpleOrder = persistEntities(em);
        tx.commit();

        tx.begin();
        if (testDb != null && testDb.equals("oracle")) {
            Query query = em.createQuery("SELECT o.id, CURRENT_DATE, CURRENT_TIMESTAMP"
                    + " FROM SimpleOrder o where o.createdAt >= CURRENT_DATE");
            List list = query.getResultList();
            Assertions.assertTrue(!list.isEmpty());
            Assertions.assertEquals(1, list.size());
            Object[] result = (Object[]) list.get(0);
            Assertions.assertTrue(result[0] instanceof Long);
            Assertions.assertTrue(result[1] instanceof java.sql.Date);
            Assertions.assertTrue(result[2] instanceof java.sql.Timestamp);
        } else {
            Query query = em.createQuery("SELECT o.id, CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP"
                    + " FROM SimpleOrder o where o.createdAt >= CURRENT_DATE");
            List list = query.getResultList();
            Assertions.assertTrue(!list.isEmpty());
            Assertions.assertEquals(1, list.size());
            Object[] result = (Object[]) list.get(0);
            Assertions.assertTrue(result[0] instanceof Long);
            Assertions.assertTrue(result[1] instanceof java.sql.Date);
            Assertions.assertTrue(result[2] instanceof java.sql.Time);
            Assertions.assertTrue(result[3] instanceof java.sql.Timestamp);
        }

        tx.commit();

        tx.begin();
        removeEntities(simpleOrder, em);
        tx.commit();

        em.close();
    }

    @Test
    public void simpleOrderTypedQuery() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        SimpleOrder simpleOrder = persistEntities(em);
        tx.commit();

        tx.begin();
        TypedQuery<SimpleOrder> query = em
                .createQuery("SELECT DISTINCT o FROM SimpleOrder AS o JOIN o.lineItems AS l WHERE l.shipped = FALSE",
                        SimpleOrder.class);
        List<SimpleOrder> list = query.getResultList();
        Assertions.assertFalse(list.isEmpty());
        Assertions.assertEquals(1, list.size());
        SimpleOrder so = list.get(0);
        Assertions.assertEquals(so.getId(), simpleOrder.getId());
        tx.commit();

        tx.begin();
        removeEntities(so, em);
        tx.commit();

        em.close();
    }

    /**
     * Run it singurarly.
     */
    @Disabled
    @Test
    public void simpleOrderTypedQueryException() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        SimpleOrder simpleOrder = persistEntities(em);
        tx.commit();

        tx.begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            TypedQuery<SimpleProduct> query = em
                    .createQuery("SELECT DISTINCT o FROM SimpleOrder AS o JOIN o.lineItems AS l WHERE l.shipped = FALSE",
                            SimpleProduct.class);
            query.getResultList();
        });
        tx.commit();
// TODO throws an exception it shouldn't
//        tx.begin();
//        removeEntities(simpleOrder, em);
//        tx.commit();

        em.close();
    }

    @Test
    public void simpleOrderNamedQuery() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        SimpleOrder simpleOrder = persistEntities(em);
        tx.commit();

        tx.begin();
        Query query = em.createNamedQuery("notShippedOrders").setParameter("shipped", false);
        List<?> list = query.getResultList();
        Assertions.assertFalse(list.isEmpty());
        Assertions.assertEquals(1, list.size());
        SimpleOrder so = (SimpleOrder) list.get(0);
        Assertions.assertEquals(so.getId(), simpleOrder.getId());
        tx.commit();

        tx.begin();
        removeEntities(so, em);
        tx.commit();

        em.close();
    }

    @Test
    public void simpleOrderTypedAndNamedQuery() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        SimpleOrder simpleOrder = persistEntities(em);
        tx.commit();

        tx.begin();
        TypedQuery<SimpleOrder> query = em.createNamedQuery("notShippedOrders", SimpleOrder.class)
                .setParameter("shipped", false);
        List<SimpleOrder> list = query.getResultList();
        Assertions.assertFalse(list.isEmpty());
        Assertions.assertEquals(1, list.size());
        SimpleOrder so = list.get(0);
        Assertions.assertEquals(so.getId(), simpleOrder.getId());
        tx.commit();

        tx.begin();
        removeEntities(so, em);
        tx.commit();

        em.close();
    }

    @Test
    public void notExistingNamedQuery() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        tx.begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            em.createNamedQuery("notExistingNamedQuery").setParameter("shipped", false);
        });
        tx.commit();

        em.close();
    }


    @Test
    public void simpleOrderNamedQueryByEmf() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        SimpleOrder simpleOrder = persistEntities(em);
        tx.commit();

        tx.begin();
        Query query = em.createQuery("SELECT DISTINCT o FROM SimpleOrder AS o JOIN o.lineItems AS l WHERE l.shipped = :shipped");
        em.getEntityManagerFactory().addNamedQuery("notShippedOrdersEmf", query);

        TypedQuery<SimpleOrder> typedQuery = em.createNamedQuery("notShippedOrdersEmf", SimpleOrder.class)
                .setParameter("shipped", false);
        List<SimpleOrder> list = typedQuery.getResultList();
        Assertions.assertFalse(list.isEmpty());
        Assertions.assertEquals(1, list.size());
        SimpleOrder so = list.get(0);
        Assertions.assertEquals(so.getId(), simpleOrder.getId());
        tx.commit();

        tx.begin();
        removeEntities(so, em);
        tx.commit();

        em.close();
    }


    @Test
    public void simpleOrderNamedNativeQuery() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        SimpleOrder simpleOrder = persistEntities(em);
        tx.commit();

        tx.begin();
        Query query = em.createNamedQuery("nativeNotShippedOrdersNoType").setParameter("shipped", false);
        List<?> list = query.getResultList();
        Assertions.assertFalse(list.isEmpty());
        Assertions.assertEquals(1, list.size());
        Object[] so = (Object[]) list.get(0);
        Long id = ((Number) so[0]).longValue();
        Assertions.assertEquals(id, simpleOrder.getId());
        Assertions.assertNotNull(so[1]);
        Assertions.assertTrue(so[1] instanceof Date);
        tx.commit();

        tx.begin();
        removeEntities(simpleOrder, em);
        tx.commit();

        em.close();
    }


    @Test
    public void simpleOrderTypedAndNamedNativeQuery() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        SimpleOrder simpleOrder = persistEntities(em);
        tx.commit();

        tx.begin();
        TypedQuery<SimpleOrder> query = em.createNamedQuery("nativeNotShippedOrders", SimpleOrder.class)
                .setParameter("shipped", false);
        List<SimpleOrder> list = query.getResultList();
        Assertions.assertFalse(list.isEmpty());
        Assertions.assertEquals(1, list.size());
        SimpleOrder so = list.get(0);
        Assertions.assertEquals(so.getId(), simpleOrder.getId());
        tx.commit();

        tx.begin();
        removeEntities(so, em);
        tx.commit();

        em.close();
    }


    @Test
    public void notExistingNamedNativeQuery() {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        tx.begin();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            em.createNamedQuery("notExistingNamedNativeQuery").setParameter("shipped", false);
        });
        tx.commit();

        em.close();
    }


    private SimpleOrder persistEntities(EntityManager em) {
        SimpleProduct simpleProduct1 = new SimpleProduct();
        simpleProduct1.setProductType("office_supplies");
        em.persist(simpleProduct1);
        LOG.info("simpleProduct1.getId()={}", simpleProduct1.getId());
        SimpleProduct simpleProduct2 = new SimpleProduct();
        simpleProduct2.setProductType("department_supplies");
        em.persist(simpleProduct2);
        LOG.info("simpleProduct2.getId()={}", simpleProduct2.getId());

        LineItem lineItem1 = new LineItem();
        lineItem1.setProduct(simpleProduct1);
        lineItem1.setShipped(Boolean.FALSE);
        em.persist(lineItem1);
        LOG.info("lineItem1.getId()={}", lineItem1.getId());

        LineItem lineItem2 = new LineItem();
        lineItem2.setProduct(simpleProduct2);
        lineItem2.setShipped(Boolean.FALSE);
        em.persist(lineItem2);
        LOG.info("lineItem2.getId()={}", lineItem2.getId());

        SimpleOrder simpleOrder = new SimpleOrder();
        simpleOrder.setLineItems(Arrays.asList(lineItem1, lineItem2));
        simpleOrder.setCreatedAt(java.sql.Date.valueOf(LocalDate.now()));
        em.persist(simpleOrder);
        LOG.info("simpleOrder.getId()={}", simpleOrder.getId());
        return simpleOrder;
    }

    private void removeEntities(SimpleOrder simpleOrder, EntityManager em) {
        simpleOrder.getLineItems().forEach(li -> {
            em.remove(li.getProduct());
            em.remove(li);
        });
        em.remove(simpleOrder);
    }
}
