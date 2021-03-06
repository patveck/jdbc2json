package com.pascalvaneck.jdbc2json;

import com.pascalvaneck.jdbc2json.db.DbCrawler;
import com.pascalvaneck.jdbc2json.db.DbVendor;
import com.pascalvaneck.jdbc2json.export.JsonExporter;
import com.pascalvaneck.jdbc2json.export.Syntax;
import com.pascalvaneck.jdbc2json.util.ListUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

class Jdbc2Json {

    private static final Log LOG = LogFactory.getLog(Jdbc2Json.class);

    private static final Jdbc2Json jdbc2Json = new Jdbc2Json();

    public static final String SEPARATOR_REGEX = ",";

    @Option(name = "-e", metaVar = "type", usage = "Server type (e.g., MySQL)", aliases = "--engine", forbids = "--url",
        depends = {"-h", "-u", "-p"})
    private DbVendor dbVendor;

    @Option(name = "-h", metaVar = "hostname", usage = "Connect to server on given host", aliases = "--host",
        forbids = "--url", depends = {"-e", "-u", "-p"})
    private String dbHostname;

    // TODO: argument to set port.

    @Option(name = "-u", metaVar = "username", usage = "The user name to use when connecting to the server",
        aliases = "--user", forbids = "--url", depends = {"-e", "-h", "-p"})
    private String dbUsername = "";

    @Option(name = "-p", metaVar = "password", usage = "The password to use when connecting to the server",
        aliases = "--password", forbids = "--url", depends = {"-e", "-h", "-u"})
    private String dbPassword = "";

    @Option(name = "--url", metaVar = "url", usage = "The JDBC connection string", forbids = {"-e", "-h", "-u", "-p"})
    private String dbUrl;

    @Option(name = "-f", metaVar = "format", usage = "Output format", aliases = "--format")
    private Syntax syntax;

    @Option(name = "-O", metaVar = "dir", usage = "Output directory", aliases = "--outputdir")
    private File outputDir = new File(System.getProperty("user.dir"));

    public File getOutputDir() {
        return outputDir;
    }

    @Option(name = "--version", usage = "Print version information")
    private boolean version;

    @Argument(metaVar = "db", usage = "The database to export")
    private String dbName;

    private List<String> includes;

    private List<String> excludes;

    private Jdbc2Json() { }

    public static Jdbc2Json getInstance() {
        return jdbc2Json;
    }

    public void run() {
        final String conn;
        if (dbHostname != null) {
            conn = dbVendor.getConnectionString(dbHostname, dbName, dbUsername, dbPassword);
        } else {
            conn = dbVendor.getConnectionString(dbName, dbUsername, dbPassword);
        }
        try {
            final DbCrawler dbCrawler = new DbCrawler(conn, includes, excludes);
            final JsonExporter jsonExporter = new JsonExporter(outputDir.toPath(), syntax);
            dbCrawler.crawl(jsonExporter);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        LOG.info("Method run() called.");
    }

    public String getDbHostname() {
        return dbHostname;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbName;
    }

    public String getDbName() {
        return dbName;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public DbVendor getDbVendor() {
        return dbVendor;
    }

    public Syntax getSyntax() {
        return syntax;
    }

    @Option(name = "-I", aliases = "--include", metaVar = "list",
        usage = "Comma-separated list of table names to include.")
    public void setIncludes(String includes) {
        this.includes = ListUtils.parseString(includes, SEPARATOR_REGEX);
    }

    public List<String> getIncludes() {
        return includes;
    }

    @Option(name = "-E", aliases = "--exclude", metaVar = "list",
        usage = "Comma-separated list of table names to exclude.")
    public void setExcludes(final String excludes) {
        this.excludes = ListUtils.parseString(excludes, SEPARATOR_REGEX);
    }

    public List<String> getExcludes() {
        return excludes;
    }

    public static void main(final String[] args) {
        try {
            jdbc2Json.parseArguments(args);
        } catch (CmdLineException e) {
            LOG.fatal(e.getMessage());
            System.exit(1);
        }
        jdbc2Json.run();
    }

    void parseArguments(final String[] args) throws CmdLineException {
        final CmdLineParser parser = new CmdLineParser(jdbc2Json);
        try {
            parser.parseArgument(args);
            if (version) {
                LOG.info(getVersion());
                System.exit(0);
            }
            checkArguments(parser);
        } catch (CmdLineException e) {
            printUsage(parser);
            throw e;
        }
    }

    void checkArguments(@Nonnull final CmdLineParser parser) throws CmdLineException {
        if (dbUrl == null && (dbHostname == null || dbUsername == null || dbPassword == null)) {
            throw new CmdLineException(parser, new Throwable("Either a connection string or hostname, "
                    + "username and password have to be set."));
        }
        if (syntax == null) {
            throw new CmdLineException(parser, new Throwable("A format (syntax) needs to be set."));
        }
        if (dbName == null || "".equals(dbName)) {
            throw new CmdLineException(parser, new Throwable("A non-empty database name needs to be given."));
        }
    }

    private void printUsage(@Nonnull final CmdLineParser parser) {
        final ByteArrayOutputStream err = new ByteArrayOutputStream();
        parser.printUsage(err);
        LOG.error(err.toString(java.nio.charset.StandardCharsets.UTF_8));
    }

    @Nonnull public String getVersion() {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("git.properties"));
            return String.format("Jdbc2json version: %s", properties.getProperty("git.commit.id.describe"));
        } catch (IOException e) {
            LOG.error("Cannot read git.properties", e);
        }
        return "No version information available";
    }

}
