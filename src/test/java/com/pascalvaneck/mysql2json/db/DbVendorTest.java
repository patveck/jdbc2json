package com.pascalvaneck.mysql2json.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static com.pascalvaneck.mysql2json.db.DbVendor.listAllDrivers;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class DbVendorTest {

    private static final Log LOG = LogFactory.getLog(DbVendorTest.class);

    @Test
    public void mysqlTestDefaultPort() {
        DbVendor dbv = DbVendor.MYSQL;
        String conn = dbv.getConnectionString("localhost", "mydb", "myuser", "mypw");
        assertEquals("connection string", "jdbc:mysql://localhost:3306/mydb?user=myuser&password=mypw", conn);
        try {
            assertTrue("Must accept", DriverManager.getDriver(conn).acceptsURL(conn));
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Test
    public void mysqlTestWithPort() {
        DbVendor dbv = DbVendor.MYSQL;
        String actual = dbv.getConnectionString("localhost", 1234, "mydb", "myuser", "mypw");
        assertEquals("connection string", "jdbc:mysql://localhost:1234/mydb?user=myuser&password=mypw",
            actual);
    }

    @Test
    public void postgresqlTestDefaultPort() {
        DbVendor dbv = DbVendor.POSTGRESQL;
        String actual = dbv.getConnectionString("localhost", "mydb", "myuser", "mypw");
        assertEquals("connection string", "jdbc:postgresql://localhost:5432/mydb?user=myuser&password=mypw",
            actual);
    }

    @Test
    public void postgresqlTestWithPort() {
        DbVendor dbv = DbVendor.POSTGRESQL;
        assertEquals("connection string", "jdbc:postgresql://localhost:5432/mydb?user=myuser&password=mypw",
            dbv.getConnectionString("localhost", 5432, "mydb", "myuser", "mypw"));
    }

    @Test
    public void listAllVendorsTest() {
        final String expected = "JDBC drivers found: ";
        final String s = listAllDrivers();
        assertEquals("Starts with \"" + expected + "\"", expected, s.substring(0, expected.length()));
        List<String> drivers = Arrays.asList("org.hsqldb.jdbc.JDBCDriver", "org.mariadb.jdbc.Driver",
            "org.postgresql.Driver", "org.sqlite.JDBC");
        for (String driver : drivers) {
            assertThat("Contains \"" + driver + "\"", s, containsString(driver));
        }
    }

}
