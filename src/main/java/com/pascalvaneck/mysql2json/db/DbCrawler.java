package com.pascalvaneck.mysql2json.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbCrawler {

    private static final Log LOG = LogFactory.getLog(DbCrawler.class);

    private Connection conn;

    public DbCrawler(String url) throws SQLException {
        conn = DriverManager.getConnection(url);
    }

    public void crawl(@Nonnull DbVisitor visitor) throws SQLException {
        DatabaseMetaData md = conn.getMetaData();
        ResultSet rs = md.getTables(conn.getCatalog(), conn.getSchema(), "%", null);
        while (rs.next()) {
            String tableName = rs.getString("TABLE_NAME");
            visitor.visitTable(tableName);
            TableCrawler tc = new TableCrawler(conn);
            tc.crawl(tableName, visitor);
        }
        rs.close();
    }

}
