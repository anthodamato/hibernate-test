/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.test.hibernate;

import com.test.hibernate.model.RandomData;
import com.test.hibernate.model.RandomGroup;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 *
 * @author adamato
 */
public class LongRunningTest {

	private static EntityManagerFactory emf;
	private final int ngroups = 100;

	@BeforeAll
	public static void beforeAll() {
		emf = Persistence.createEntityManagerFactory("long_running", PersistenceUnitProperties.getProperties());
	}

	@AfterAll
	public static void afterAll() {
		emf.close();
	}

	@Disabled
	@Test
	public void randomGroups() throws Exception {
//		org.apache.log4j.Logger.getLogger("org.hibernate.SQL").setLevel(org.apache.log4j.Level.ALL);

		Instant start = Instant.now();
		final EntityManager em = emf.createEntityManager();
		try {
			final EntityTransaction tx = em.getTransaction();
			tx.begin();

			for (int i = 0; i < ngroups; ++i) {
				RandomGroup randomGroup = randomGroup(Integer.toString(i));
				randomGroup.getRandomDataValues().stream().forEach(d -> em.persist(d));
				em.persist(randomGroup);
			}

			tx.commit();
		} finally {
			em.close();
			Instant end = Instant.now();
			System.out.println("Seconds: " + (end.toEpochMilli() - start.toEpochMilli()) / 1000);
		}
	}

	private RandomGroup randomGroup(String name) {
		RandomGroup randomGroup = new RandomGroup();
		randomGroup.setName(name);
		Collection<RandomData> list = dataList(1000);
		randomGroup.setRandomDataValues(list);
		return randomGroup;
	}

	private RandomData data(Integer addendum) {
		RandomData randomData = new RandomData();
		randomData.setFib1(0 + addendum);
		randomData.setFib2(1 + addendum);
		randomData.setFib3(2 + addendum);
		randomData.setFib4(3 + addendum);
		randomData.setFib5(5 + addendum);
		String fibs = randomData.getFib1().toString() + randomData.getFib2().toString()
				+ randomData.getFib3().toString() + randomData.getFib4().toString() + randomData.getFib5().toString()
				+ addendum.toString();
		randomData.setFibString(fibs);
		randomData.setMultiply2(1);
		return randomData;
	}

	private Collection<RandomData> dataList(int n) {
		List<RandomData> list = new ArrayList<>();
		for (int i = 0; i < n; ++i) {
			RandomData randomData = data(i);
			list.add(randomData);
		}

		return list;
	}
}
