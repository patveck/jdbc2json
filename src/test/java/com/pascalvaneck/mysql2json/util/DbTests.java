package com.pascalvaneck.mysql2json.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class DbTests {

    private static final Log LOG = LogFactory.getLog(DbTests.class);

    Connection conn = null;

    @Before
    public void setUpHsqldb() {
        try {
            conn = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "");
            LOG.info("Connection to Hsqldb testdb established.");
            conn.createStatement()
                .executeUpdate("create table contacts (name varchar(45),email varchar(45),phone varchar(45))");
            conn.createStatement()
                .executeUpdate("insert into contacts (name, email, phone) values ('piet','piet@lut.nl','0612345678')");
            LOG.info("Table contacts created and populated");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDbReady() {
        try (ResultSet rs = conn.createStatement().executeQuery("select * from contacts")) {
            while (rs.next()) {
                assertEquals("Name should be piet", "piet", rs.getString("name"));
                assertEquals("email should be piet@lut.nl", "piet@lut.nl", rs.getString("email"));
                assertEquals("phone should be 0612345678", "0612345678", rs.getString("phone"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @After
    public void cleanUpHsqldb() {
        try {
            conn.createStatement().execute("drop table contacts if exists");
            conn.createStatement().execute( "shutdown");
            conn.close();
            LOG.info("Connection to Hsqldb testdb closed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
