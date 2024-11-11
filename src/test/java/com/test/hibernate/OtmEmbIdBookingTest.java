package com.test.hibernate;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.test.hibernate.model.HotelBookingDetail;
import com.test.hibernate.model.HotelCustomer;
import com.test.hibernate.model.RoomBookingId;

/**
 * @author Antonio Damato <anto.damato@gmail.com>
 */
public class OtmEmbIdBookingTest {

    private static EntityManagerFactory emf;

    @BeforeAll
    public static void beforeAll() {
        emf = Persistence.createEntityManagerFactory("otm_emb_booking", PersistenceUnitProperties.getProperties());
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

        HotelCustomer hotelCustomer1 = new HotelCustomer();
        hotelCustomer1.setName("Mark Bold");
        em.persist(hotelCustomer1);

        HotelCustomer hotelCustomer2 = new HotelCustomer();
        hotelCustomer2.setName("Alexandra Bell");
        em.persist(hotelCustomer2);

        RoomBookingId roomBookingId = new RoomBookingId();
        Date date = Date.valueOf(LocalDate.of(2020, 10, 1));
        roomBookingId.setDateof(date);
        roomBookingId.setRoomNumber(23);

        HotelBookingDetail hotelBookingDetail = new HotelBookingDetail();
        hotelBookingDetail.setRoomBookingId(roomBookingId);
        hotelBookingDetail.setCustomers(Arrays.asList(hotelCustomer1, hotelCustomer2));
        hotelBookingDetail.setPrice(45.5f);

        em.persist(hotelBookingDetail);

        Assertions.assertNotNull(hotelBookingDetail.getRoomBookingId());
        tx.commit();

        tx.begin();
        HotelBookingDetail b = em.find(HotelBookingDetail.class, hotelBookingDetail.getRoomBookingId());
        Assertions.assertTrue(b == hotelBookingDetail);
        Assertions.assertNotNull(b);
        RoomBookingId bookingId = b.getRoomBookingId();
        Assertions.assertNotNull(bookingId);
        Assertions.assertEquals(date, bookingId.getDateof());

        em.detach(hotelBookingDetail);
        b = em.find(HotelBookingDetail.class, hotelBookingDetail.getRoomBookingId());
        Assertions.assertFalse(b == hotelBookingDetail);
        Assertions.assertNotNull(b);

        HotelBookingDetail b2 = em.find(HotelBookingDetail.class, b.getRoomBookingId());
        Assertions.assertTrue(b2 == b);

        em.remove(hotelCustomer1);
        em.remove(hotelCustomer2);
        em.remove(b);
        tx.commit();
        em.close();
        emf.close();
    }

}
