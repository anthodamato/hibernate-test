package com.test.hibernate;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.Bindable.BindableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.Type.PersistenceType;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.test.hibernate.model.Address;
import com.test.hibernate.model.Citizen;

/**
 * java -jar $DERBY_HOME/lib/derbyrun.jar server start
 * <p>
 * connect 'jdbc:derby://localhost:1527/test';
 *
 * @author adamato
 */
public class PersistTest {
//	private Logger LOG = LoggerFactory.getLogger(FindTest.class);

    private static EntityManagerFactory emf;

    @BeforeAll
    public static void beforeAll() {
        emf = Persistence.createEntityManagerFactory("citizens", PersistenceUnitProperties.getProperties());
    }

    @AfterAll
    public static void afterAll() {
        emf.close();
    }

    @Test
    public void persist() throws Exception {
//		org.apache.log4j.Logger.getLogger("org.hibernate.SQL").setLevel(org.apache.log4j.Level.OFF);

        final EntityManager em = emf.createEntityManager();
        final EntityTransaction tx = em.getTransaction();
        tx.begin();

        Citizen citizen = new Citizen();
        citizen.setName("Marc");
        em.persist(citizen);

        Assertions.assertNotNull(citizen.getId());
        Citizen c = em.find(Citizen.class, citizen.getId());
        Assertions.assertNotNull(c);

        Address address = new Address();
        address.setName("Regent St");
        em.persist(address);

        c = em.find(Citizen.class, citizen.getId());
        Assertions.assertNotNull(c);

        Assertions.assertNotNull(address.getId());
        long id = address.getId() + 1;
        Citizen cn = em.find(Citizen.class, id);
        Assertions.assertNull(cn);

        em.remove(c);
        em.remove(address);

        tx.commit();
        em.close();
    }

    @Test
    public void update() throws Exception {
        final EntityManager em = emf.createEntityManager();
        final EntityTransaction tx = em.getTransaction();
        tx.begin();

        Citizen citizen = new Citizen();
        citizen.setName("Marc");
        em.persist(citizen);

        Address address = new Address();
        address.setName("Regent St");
        em.persist(address);

        long id = address.getId() + 1;
        Citizen cn = em.find(Citizen.class, id);
        Assertions.assertNull(cn);

        tx.commit();

        tx.begin();
        Citizen c = em.find(Citizen.class, citizen.getId());
        Assertions.assertNotNull(c);

        // no need to call persist
        c.setName("Joe");
        tx.commit();

        tx.begin();
        em.detach(citizen);
        System.out.println("After detach");
        c = em.find(Citizen.class, citizen.getId());
        Assertions.assertNotNull(c);
        Assertions.assertEquals("Joe", c.getName());

        em.remove(c);
        em.remove(address);

        tx.commit();
        em.close();
    }

    @Test
    public void metamodel() {
        final EntityManager em = emf.createEntityManager();
        Metamodel metamodel = em.getMetamodel();
        Assertions.assertNotNull(metamodel);

        Set<EntityType<?>> entityTypes = metamodel.getEntities();
        Assertions.assertEquals(2, entityTypes.size());

        for (EntityType<?> entityType : entityTypes) {
            System.out.println("metamodel: entityType.getName()=" + entityType.getName());
            if (entityType.getName().equals("Address")) {
                checkAddress(entityType);
            } else if (entityType.getName().equals("Citizen")) {
                checkCitizen(entityType);
            }
        }

        Set<ManagedType<?>> managedTypes = metamodel.getManagedTypes();
        Assertions.assertEquals(2, managedTypes.size());

        em.close();
    }

    private void checkAddress(EntityType<?> entityType) {
        Assertions.assertEquals("Address", entityType.getName());
        MetamodelUtils.checkType(entityType, Address.class, PersistenceType.ENTITY);
        MetamodelUtils.checkType(entityType.getIdType(), Long.class, PersistenceType.BASIC);

        Assertions.assertEquals(BindableType.ENTITY_TYPE, entityType.getBindableType());
        Assertions.assertEquals(Address.class, entityType.getBindableJavaType());

        List<String> names = MetamodelUtils.getAttributeNames(entityType);
        Assertions.assertTrue(CollectionUtils.containsAll(Arrays.asList("id", "name", "postcode", "tt"), names));

        MetamodelUtils.checkAttribute(entityType.getAttribute("name"), "name", String.class,
                PersistentAttributeType.BASIC, false, false);
        MetamodelUtils.checkAttribute(entityType.getAttribute("postcode"), "postcode", String.class,
                PersistentAttributeType.BASIC, false, false);
    }

    private void checkCitizen(EntityType<?> entityType) {
        Assertions.assertEquals("Citizen", entityType.getName());
        MetamodelUtils.checkType(entityType, Citizen.class, PersistenceType.ENTITY);
        MetamodelUtils.checkType(entityType.getIdType(), Long.class, PersistenceType.BASIC);

        Assertions.assertEquals(BindableType.ENTITY_TYPE, entityType.getBindableType());
        Assertions.assertEquals(Citizen.class, entityType.getBindableJavaType());

        List<String> names = MetamodelUtils.getAttributeNames(entityType);
        Assertions.assertTrue(CollectionUtils.containsAll(Arrays.asList("id", "name", "lastName", "version"), names));

        MetamodelUtils.checkAttribute(entityType.getAttribute("name"), "name", String.class,
                PersistentAttributeType.BASIC, false, false);
        MetamodelUtils.checkAttribute(entityType.getAttribute("lastName"), "lastName", String.class,
                PersistentAttributeType.BASIC, false, false);
    }

}
