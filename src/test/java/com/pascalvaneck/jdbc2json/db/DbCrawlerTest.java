package com.pascalvaneck.jdbc2json.db;

import com.fasterxml.jackson.jr.ob.JSON;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static com.pascalvaneck.jdbc2json.db.TestUtils.setUpSimpleTable;
import static com.pascalvaneck.jdbc2json.db.TestUtils.tearDownSimpleTable;

public class DbCrawlerTest {

    private static final Log LOG = LogFactory.getLog(DbCrawlerTest.class);

    private Connection conn = null;

    @Before
    public void setUp() throws SQLException {
        conn = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "");
        setUpSimpleTable(conn);
    }

    @Test
    public void testDbCrawler() {
        try {
            DbCrawler dbc = new DbCrawler("jdbc:hsqldb:mem:testdb;user=SA", null, null);
            dbc.crawl(new DbVisitor() {

                @Override
                public void visitTable(@Nonnull final String s, @Nonnull final List<String> keyColumnNames) {
                    LOG.info("Visiting " + s + ".");
                }

                @Override
                public void visitRow(@Nonnull final Map<String, Object> row) {
                    try {
                        LOG.info("Got " + JSON.std.asString(row) + ".");
                    } catch (IOException e) {
                        LOG.error("Problem serializing to JSON", e);
                    }
                }

                @Override
                public void close() {}
            });
        } catch (SQLException e) {
            LOG.error("Caught SQLException", e);
        }
    }

    @After
    public void cleanUpHsqldb() throws SQLException {
        tearDownSimpleTable(conn);
        conn.close();
    }

}
