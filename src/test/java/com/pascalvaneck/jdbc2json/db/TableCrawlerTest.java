package com.pascalvaneck.jdbc2json.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static com.pascalvaneck.jdbc2json.db.TestUtils.setUpSimpleTable;
import static com.pascalvaneck.jdbc2json.db.TestUtils.tearDownSimpleTable;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TableCrawlerTest {

    Connection conn;
    final DbVisitor visitor = mock(DbVisitor.class);
    TableCrawler tc;

    @Before
    public void setUp() throws SQLException {
        conn = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "");
        setUpSimpleTable(conn);
        tc = new TableCrawler(conn);
    }

    @After
    public void tearDown() throws SQLException {
        tearDownSimpleTable(conn);
        conn.close();
    }

    @Test
    public void testCrawl() throws Exception {
        Map<String, Object> expected = new HashMap<>();
        expected.put("NAME", "piet");
        expected.put("EMAIL", "piet@lut.nl");
        expected.put("PHONE", "0612345678");
        tc.crawl("CONTACTS", visitor);
        verify(visitor).visitRow(expected);
    }

    @Test
    public void testEmptyTableName() {
        final String expected = "Illegal SQL92 table name";
        try {
            tc.crawl("", visitor);
        } catch (SQLException e) {
            assertEquals("Exception message should be \"" + expected +"\"", expected, e.getMessage());
        }
    }

    @Test
    public void testIllegalTableName() {
        final String expected = "Illegal SQL92 table name";
        try {
            tc.crawl("CONTACTS; DROP DATABASE;", visitor);
        } catch (SQLException e) {
            assertEquals("Exception message should be \"" + expected +"\"", expected, e.getMessage());
        }
    }

}
