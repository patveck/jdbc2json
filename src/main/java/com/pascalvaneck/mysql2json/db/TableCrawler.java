package com.pascalvaneck.mysql2json.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class TableCrawler {

    private static final Log LOG = LogFactory.getLog(TableCrawler.class);

    private static final Pattern SQL92_IDENTIFIER_PATTERN = Pattern.compile("[A-Za-z]\\w*");

    private final Connection conn;

    public TableCrawler(Connection conn) {
        this.conn = conn;
    }

    public void crawl(@Nonnull final String tableName, @Nonnull final DbVisitor visitor) throws SQLException {
        if (isValidSQL92Identifier(tableName)) {
            for (String key : getPrimaryKeys(tableName).keySet()) {
                iterateRows(tableName, key, visitor);
            }
        } else {
            throw new SQLException("Illegal SQL92 table name");
        }
    }

    private void iterateRows(@Nonnull String tableName, @Nonnull final String primaryKey, @Nonnull final DbVisitor visitor) throws SQLException {
        try (Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM " + tableName)
        ) {
            ResultSetMetaData md = rs.getMetaData();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int col = 1; col <= md.getColumnCount(); col++) {
                    row.put(md.getColumnName(col), rs.getObject(col));
                }
                visitor.visitRow(row);
            }
        }
    }

    @Nonnull
    private Map<String, Short> getPrimaryKeys(@Nonnull final String tableName) throws SQLException {
        DatabaseMetaData md = conn.getMetaData();
        Map<String, Short> result = new HashMap<>();
        try (ResultSet rs = md.getPrimaryKeys(conn.getCatalog(), conn.getSchema(), tableName)) {
            while (rs.next()) {
                result.put(rs.getString("COLUMN_NAME"), rs.getShort("KEY_SEQ"));
            }
        }
        return result;
    }

    private boolean isValidSQL92Identifier(String tableName) {
        return SQL92_IDENTIFIER_PATTERN.matcher(tableName).matches() && tableName.length() <= 128;
    }

}
