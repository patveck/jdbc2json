package com.pascalvaneck.jdbc2json.db;

import com.pascalvaneck.jdbc2json.util.ListUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class TableCrawler {

    private static final Log LOG = LogFactory.getLog(TableCrawler.class);

    private static final Pattern SQL92_IDENTIFIER_PATTERN = Pattern.compile("[A-Za-z]\\w*");
    public static final int SQL92_MAX_IDENTIFIER_LENGTH = 128;

    private static final String QUERY = "SELECT %s, %s FROM %s ORDER BY %s";

    private final Connection conn;

    private final List<String> primaryKeys;

    public TableCrawler(@Nonnull final Connection conn, @Nonnull final List<String> primaryKeys) {
        this.conn = conn;
        this.primaryKeys= primaryKeys;
    }

    public void crawl(@Nonnull final String tableName, @Nonnull final DbVisitor visitor) throws SQLException {
        if (isValidSql92Identifier(tableName)) {
            visitor.visitTable(tableName, primaryKeys);
            for (String key : primaryKeys) {
                iterateRows(tableName, key, visitor);
            }
        } else {
            throw new SQLException("Illegal SQL92 table name");
        }
    }

    private void iterateRows(@Nonnull String tableName, @Nonnull final String primaryKey,
                             @Nonnull final DbVisitor visitor) throws SQLException {
        try (Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery(buildQuery(tableName))
        ) {
            final ResultSetMetaData md = rs.getMetaData();
            while (rs.next()) {
                final Map<String, Object> row = new HashMap<>();
                for (int col = 1; col <= md.getColumnCount(); col++) {
                    row.put(md.getColumnName(col), rs.getObject(col));
                }
                visitor.visitRow(row);
            }
        }
    }

    private String buildQuery(@Nonnull final String tableName) throws SQLException {
        String result = String.format(QUERY,
                                      String.join( ", ", primaryKeys),
                                      String.join(", ",
                                                  ListUtils.listWithoutList(getAllColumns(tableName), primaryKeys)),
                                      tableName,
                                      String.join(", ", primaryKeys));
        LOG.info(result);
        return result;
    }

    @Nonnull
    private List<String> getAllColumns(@Nonnull final String tableName) {
        List<String> result = new ArrayList<String>();
        try (ResultSet rs = conn.getMetaData().getColumns(conn.getCatalog(), conn.getSchema(), tableName, null)) {
            while (rs.next()) {
                result.add(rs.getString("COLUMN_NAME"));
            }
            rs.close();
        } catch (SQLException e) {
            LOG.fatal(e.getMessage(), e);
        }
        return result;
    }


    private boolean isValidSql92Identifier(String tableName) {
        return SQL92_IDENTIFIER_PATTERN.matcher(tableName).matches()
            && tableName.length() <= SQL92_MAX_IDENTIFIER_LENGTH;
    }

}
