package com.pascalvaneck.mysql2json.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TestUtils {

    private static final Log LOG = LogFactory.getLog(TestUtils.class);

    static void setUpSimpleTable(@Nonnull final Connection conn) {
        LOG.info("Connection to database established.");
        try (Statement stm = conn.createStatement()) {
            stm.executeUpdate(
                "create table contacts (name varchar(45) primary key,email varchar(45),phone varchar(45))");
            stm.executeUpdate("insert into contacts (name, email, phone) values ('piet','piet@lut.nl','0612345678')");
            LOG.info("Table contacts created and populated");
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    static void tearDownSimpleTable(@Nonnull final Connection conn) {
        try (Statement stm = conn.createStatement()) {
            stm.execute("drop table contacts");
            LOG.info("Table contacts deleted.");
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
