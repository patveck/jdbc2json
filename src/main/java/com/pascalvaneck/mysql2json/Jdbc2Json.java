package com.pascalvaneck.mysql2json;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Jdbc2Json {

    private static final Log LOG = LogFactory.getLog(Jdbc2Json.class);

    public static final String SEPARATOR_REGEX = ",";

    @Option(name="-h",metaVar="hostname",usage="Connect to MySQl server on given host",aliases="--host")
    private String dbHostname;

    @Option(name="-u",metaVar="username",usage="The MySQL user name to use when connecting to the server",aliases="--user")
    private String dbUsername;

    @Option(name="-p",metaVar="password",usage="The password to use when connecting to the server",aliases="--password")
    private String dbPassword;

    @Option(name="--url",metaVar="url",usage="The JDBC connection string")
    private String dbUrl;

    private File outputDir;

    @Option(name="-O",metaVar="dir",usage="Output directory",aliases="--outputdir")
    private void setOutputDir(File outputDir) {
        if (outputDir == null) {
            this.outputDir = new File(System.getProperty("user.dir"));
        } else {
            this.outputDir = outputDir;
        }
    }

    @Argument(required=true,metaVar="db",usage="The MySQL database to export")
    private String dbName;

    private List<String> includes;

    private List<String> excludes;

    public void run() {
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

    @Option(name="-I",aliases="--include",metaVar="list",usage="Comma-separated list of table names to include.")
    public void setIncludes(String includes) {
        this.includes = parseString(includes, SEPARATOR_REGEX);
    }

    public List<String> getIncludes() {
        return includes;
    }

    @Option(name="-E",aliases="--exclude",metaVar="list",usage="Comma-separated list of table names to exclude.")
    public void setExcludes(String excludes) {
        this.excludes = parseString(excludes, SEPARATOR_REGEX);
    }

    public List<String> getExcludes() {
        return excludes;
    }

    private List<String> parseString(String s, String regex) {
        List<String> result = new ArrayList<>();
        String[] names = s.split(regex);
        Collections.addAll(result, names);
        return result;
    }

    public static void main(String[] args) {
        Jdbc2Json jdbc2Json = new Jdbc2Json();
        CmdLineParser parser = new CmdLineParser(jdbc2Json);
        try {
            parser.parseArgument(args);
            jdbc2Json.run();
        } catch (CmdLineException e) {
            LOG.error(e.getMessage());
            ByteArrayOutputStream err = new ByteArrayOutputStream();
            parser.printUsage(err);
            LOG.error(err.toString(java.nio.charset.StandardCharsets.UTF_8));
        }
        jdbc2Json.run();
    }

}
