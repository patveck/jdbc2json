package com.pascalvaneck.jdbc2json.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbCrawler {

    private static final Log LOG = LogFactory.getLog(DbCrawler.class);

    private final Connection conn;

    protected final List<String> includes;

    protected final List<String> excludes;

    public DbCrawler(@Nonnull final String url, final List<String> includes, final List<String> excludes) throws SQLException {
        conn = DriverManager.getConnection(url);
        this.includes = includes;
        this.excludes = excludes;
    }

    public void crawl(@Nonnull final DbVisitor visitor) throws SQLException {
        final DatabaseMetaData md = conn.getMetaData();
        if (includes != null) {
            for (String tableName : includes) {
                doTable(visitor, tableName);
            }
        } else {
            try (final ResultSet rs = md.getTables(conn.getCatalog(), conn.getSchema(), "%", null)) {
                while (rs.next()) {
                    final String tableName = rs.getString("TABLE_NAME");
                    doTable(visitor, tableName);
                }
            }
        }
    }

    private void doTable(@Nonnull final DbVisitor visitor, @Nonnull final String tableName) throws SQLException {
        if (excludes != null && !excludes.contains(tableName)) {
            final TableCrawler tc = new TableCrawler(conn, getPrimaryKeys(tableName));
            tc.crawl(tableName, visitor);
        }
    }

    @Nonnull
    private List<String> getPrimaryKeys(@Nonnull final String tableName) {
        List<String> result = new ArrayList<String>();
        try (ResultSet rs = conn.getMetaData().getPrimaryKeys(conn.getCatalog(), conn.getSchema(), tableName)) {
            while (rs.next()) {
                result.add(rs.getString("COLUMN_NAME"));
            }
            rs.close();
        } catch (SQLException e) {
            LOG.fatal(e.getMessage(), e);
        }
        return result;
    }

    /*
    @Nonnull
    private Map<Short, String> getPrimaryKeys(@Nonnull final String tableName) throws SQLException {
        final DatabaseMetaData md = conn.getMetaData();
        final Map<Short, String> result = new HashMap<>();
        try (ResultSet rs = md.getPrimaryKeys(conn.getCatalog(), conn.getSchema(), tableName)) {
            while (rs.next()) {
                result.put( rs.getShort("KEY_SEQ"), rs.getString("COLUMN_NAME"));
            }
        }
        return result;
    }
    */

}
