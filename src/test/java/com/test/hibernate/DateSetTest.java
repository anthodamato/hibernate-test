/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.test.hibernate;

import com.test.hibernate.model.DateSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author Antonio Damato <anto.damato@gmail.com>
 */
public class DateSetTest {

    private static final Logger
            LOG = LogManager.getLogger(DateSetTest.class);

    private static EntityManagerFactory emf;

    @BeforeAll
    public static void beforeAll() {
        emf = Persistence.createEntityManagerFactory("date_set", PersistenceUnitProperties.getProperties());
    }

    @AfterAll
    public static void afterAll() {
        emf.close();
    }

    @Test
    public void dates() throws Exception {
        DateSet dateSet = new DateSet();
        Duration duration = Duration.ofDays(2);
        dateSet.setDuration(duration);
        Instant instant = Instant.now();
        dateSet.setInstant(instant);
        LocalDate localDate = LocalDate.now();
        dateSet.setLocalDate(localDate);
        LocalDateTime localDateTime = LocalDateTime.now();
        dateSet.setLocalDateTime(localDateTime);
        LocalTime localTime = LocalTime.now();
        dateSet.setLocalTime(localTime);
        OffsetDateTime offsetDateTime = OffsetDateTime.now();
        dateSet.setOffsetDateTime(offsetDateTime);
        OffsetTime offsetTime = OffsetTime.now();
        dateSet.setOffsetTime(offsetTime);
        java.sql.Date sqlDate = new java.sql.Date(instant.toEpochMilli());
        dateSet.setSqlDate(sqlDate);
        Time time = Time.valueOf(localTime);
        dateSet.setSqlTime(time);
        Timestamp timestamp = Timestamp.valueOf(localDateTime);
        dateSet.setSqlTimestamp(timestamp);
        Calendar calendar = new GregorianCalendar(2021, 2, 14, 10, 20, 30);
        dateSet.setUtilCalendar(calendar);
        java.util.Date utilDate = new java.util.Date();
        dateSet.setUtilDate(utilDate);
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        dateSet.setZonedDateTime(zonedDateTime);

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(dateSet);
        em.flush();

        em.detach(dateSet);

        DateSet ds = em.find(DateSet.class, dateSet.getId());

        Assertions.assertEquals(duration, ds.getDuration());

        // MariaDB timestamp doesn't keep nanoseconds precision
        Assertions.assertEquals(instant.getEpochSecond(), ds.getInstant().getEpochSecond());

        Assertions.assertEquals(localDate, ds.getLocalDate());
        Assertions.assertEquals(localDateTime.toEpochSecond(ZoneOffset.UTC), ds.getLocalDateTime().toEpochSecond(ZoneOffset.UTC));

        Assertions.assertEquals(localTime.getHour(), ds.getLocalTime().getHour());
        Assertions.assertEquals(localTime.getMinute(), ds.getLocalTime().getMinute());
        Assertions.assertEquals(localTime.getSecond(), ds.getLocalTime().getSecond());

        Assertions.assertEquals(offsetDateTime.toEpochSecond(), ds.getOffsetDateTime().toEpochSecond());
        Assertions.assertEquals(offsetDateTime.getOffset(), ds.getOffsetDateTime().getOffset());

//	log(offsetTime, "1");
//	log(ds.getOffsetTime(), "2");
        Assertions.assertEquals(offsetTime.getHour(), ds.getOffsetTime().getHour());
        Assertions.assertEquals(offsetTime.getMinute(), ds.getOffsetTime().getMinute());
        Assertions.assertEquals(offsetTime.getSecond(), ds.getOffsetTime().getSecond());
        Assertions.assertEquals(offsetTime.getOffset(), ds.getOffsetTime().getOffset());

        Assertions.assertEquals(sqlDate.toLocalDate(), ds.getSqlDate().toLocalDate());

        Assertions.assertEquals(time, ds.getSqlTime());
        Assertions.assertEquals(timestamp.getTime() / 1000, ds.getSqlTimestamp().getTime() / 1000);
        Assertions.assertEquals(calendar, ds.getUtilCalendar());
        Assertions.assertEquals(utilDate.getTime() / 1000, ds.getUtilDate().getTime() / 1000);
        Assertions.assertEquals(zonedDateTime.toEpochSecond(), ds.getZonedDateTime().toEpochSecond());

        em.remove(ds);

        em.getTransaction().commit();
        em.close();
    }
}
