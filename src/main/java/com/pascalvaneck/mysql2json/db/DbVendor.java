package com.pascalvaneck.mysql2json.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Nonnull;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

public enum DbVendor {
    MYSQL("jdbc:mysql://%s:%s/%s?user=%s&password=%s", true, 3306),
    MARIADB("jdbc:mariadb://%s:%s/%s?user=%s&password=%s", true, 3306),
    POSTGRESQL("jdbc:postgresql://%s:%s/%s?user=%s&password=%s", true, 5432),
    SQLITE("jdbc:sqlite:%s", false, 0),
    HSQLDBSERVER("jdbc:hsqldb:hsql://%s:%s/%s;user=%s;password=%s", true, 9001),
    HSQLDBFILE("jdbc:hsqldb:file:%s", false, 0);

    private static final Log LOG = LogFactory.getLog(DbVendor.class);

    private final String url;
    private final boolean networked;
    private final int defaultPort;

    DbVendor(final String s, final boolean networked, final int port) {
        this.url = s;
        this.networked = networked;
        this.defaultPort = port;
    }

    public String getConnectionString(String hostname, int port, String dbname, String username, String password) {
        // String.format silently ignores superfluous arguments:
        return String.format(url, hostname, port, dbname, username, password);
    }

    public String getConnectionString(String hostname, String dbname, String username, String password) {
        // String.format silently ignores superfluous arguments:
        return String.format(url, hostname, defaultPort, dbname, username, password);
    }

    /**
     * Get connection string for the default port for this vendor on localhost. For SQLite, username and password are
     * ignored.
     *
     * @param dbname Database name (file path in the case of SQLITE).
     * @param username Username
     * @param password Password
     * @return The connection string
     */
    public String getConnectionString(String dbname, String username, String password) {
        if (networked) {
            return String.format(url, "localhost", defaultPort, dbname, username, password);
        } else {
            return String.format(url, dbname);
        }
    }

    @Nonnull
    public static String listAllDrivers() {
        final StringBuilder sb = new StringBuilder("JDBC drivers found: ");
        for (Enumeration<Driver> e = DriverManager.getDrivers(); e.hasMoreElements();) {
            sb.append(e.nextElement().getClass().getName());
            sb.append(e.hasMoreElements() ? ", " : ".");
        }
        return sb.toString();
    }

}
