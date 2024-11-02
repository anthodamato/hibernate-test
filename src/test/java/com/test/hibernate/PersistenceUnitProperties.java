/*
 * Copyright (C) 2021 Antonio Damato <anto.damato@gmail.com>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.test.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Antonio Damato <anto.damato@gmail.com>
 */
public class PersistenceUnitProperties {

    private static final Logger LOG = LogManager.getLogger(PersistenceUnitProperties.class);

    public static Map<String, String> getProperties() {
        String hibernateTest = System.getProperty("hibernate.test");
        if (hibernateTest == null || hibernateTest.isBlank())
            return null;

        LOG.info("getProperties: hibernateTest=" + hibernateTest);
        if (hibernateTest.equals("mysql")) {
            Map<String, String> map = new HashMap<>();
            map.put("javax.persistence.jdbc.url", "jdbc:mysql://localhost:3306/test?user=root&password=password");
            map.put("javax.persistence.jdbc.driver", "com.mysql.cj.jdbc.Driver");
            map.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
            return map;
        }

        if (hibernateTest.equals("mariadb")) {
            Map<String, String> map = new HashMap<>();
            map.put("javax.persistence.jdbc.url", "jdbc:mariadb://localhost:3306/test");
            map.put("javax.persistence.jdbc.driver", "org.mariadb.jdbc.Driver");
            map.put("javax.persistence.jdbc.user", "root");
            map.put("javax.persistence.jdbc.password", "password");
            map.put("hibernate.dialect", "org.hibernate.dialect.MariaDBDialect");
            return map;
        }

        if (hibernateTest.equals("postgres")) {
            Map<String, String> map = new HashMap<>();
            map.put("javax.persistence.jdbc.url", "jdbc:postgresql://localhost/test");
            map.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
            map.put("javax.persistence.jdbc.user", "postgres");
            map.put("javax.persistence.jdbc.password", "password");
            map.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            return map;
        }

        if (hibernateTest.equals("oracle")) {
            Map<String, String> map = new HashMap<>();
            map.put("javax.persistence.jdbc.url", "jdbc:oracle:thin:@localhost:1521:ORCLCDB");
            map.put("javax.persistence.jdbc.driver", "oracle.jdbc.OracleDriver");
            map.put("javax.persistence.jdbc.user", "test");
            map.put("javax.persistence.jdbc.password", "password");
            map.put("hibernate.dialect", "org.hibernate.dialect.Oracle12cDialect");
            return map;
        }

        if (hibernateTest.equals("h2")) {
            Map<String, String> map = new HashMap<>();
            map.put("javax.persistence.jdbc.url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
            map.put("javax.persistence.jdbc.driver", "org.h2.Driver");
            map.put("javax.persistence.jdbc.user", "sa");
            map.put("javax.persistence.jdbc.password", "");
            map.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
            return map;
        }

        return null;
    }
}
