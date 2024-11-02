package com.test.hibernate;

import com.test.hibernate.model.onetomany.Store;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.test.hibernate.model.Property;
import com.test.hibernate.model.PropertyOwner;
import com.test.hibernate.model.PropertyType;

import java.util.Arrays;
import java.util.Collection;
import javax.persistence.PersistenceException;

import org.junit.jupiter.api.Assertions;

/**
 * @author Antonio Damato <anto.damato@gmail.com>
 */
public class PropertyTest {

    private static EntityManagerFactory emf;

    @BeforeAll
    public static void beforeAll() {
        emf = Persistence.createEntityManagerFactory("property_many_to_many_uni",
                PersistenceUnitProperties.getProperties());
    }

    @AfterAll
    public static void afterAll() {
        emf.close();
    }

    @Test
    public void properties() throws Exception {
        final EntityManager em = emf.createEntityManager();

        final EntityTransaction tx = em.getTransaction();
        tx.begin();

        PropertyOwner owner1 = new PropertyOwner();
        owner1.setName("Media Ltd");
        em.persist(owner1);

        PropertyOwner owner2 = new PropertyOwner();
        owner2.setName("Simply Ltd");
        em.persist(owner2);

        Property property = new Property();
        property.setAddress("England Rd, London");
        property.setOwners(Arrays.asList(owner1, owner2));
        property.setPropertyType(PropertyType.apartment);
        em.persist(property);

        tx.commit();

        em.detach(property);
        Property p = em.find(Property.class, property.getId());
        Collection<PropertyOwner> owners = p.getOwners();
        Assertions.assertNotNull(owners);
        Assertions.assertEquals(2, owners.size());

        em.close();
    }

    @Test
    public void optional() {
        final EntityManager em = emf.createEntityManager();

        final EntityTransaction tx = em.getTransaction();
        tx.begin();

        PropertyOwner owner1 = new PropertyOwner();
        owner1.setName("Media Ltd");
        em.persist(owner1);

        PropertyOwner owner2 = new PropertyOwner();
        owner2.setName("Simply Ltd");
        em.persist(owner2);

        Property property = new Property();
        property.setAddress("England Rd, London");
        property.setOwners(Arrays.asList(owner1, owner2));

        Assertions.assertThrows(PersistenceException.class, () -> {
            em.persist(property);
        });

        tx.commit();

        em.close();
    }


}
