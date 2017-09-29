package com.pascalvaneck.jdbc2json.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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

    public void crawl(@Nonnull DbVisitor visitor) throws SQLException {
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

    private void doTable(@Nonnull DbVisitor visitor, String tableName) throws SQLException {
        if (excludes != null && !excludes.contains(tableName)) {
            visitor.visitTable(tableName);
            final TableCrawler tc = new TableCrawler(conn);
            tc.crawl(tableName, visitor);
        }
    }

}
