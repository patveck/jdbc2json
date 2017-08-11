package com.pascalvaneck.mysql2json;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

class App {

    private static final Log LOG = LogFactory.getLog(App.class);

    @Option(name="-h",metaVar="hostname",usage="Connect to MySQl server on given host",aliases="--host")
    private String dbHostname;

    @Option(name="-u",metaVar="username",usage="The MySQL user name to use when connecting to the server",aliases="--user")
    private String dbUsername;

    @Option(name="-p",metaVar="password",usage="The password to use when connecting to the server",aliases="--password")
    private String dbPassword;

    @Argument(required=true,metaVar="db",usage="The MySQL database to export")
    private String dbName;

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

    public static void main(String[] args) {
        App app = new App();
        CmdLineParser parser = new CmdLineParser(app);
        try {
            parser.parseArgument(args);
            app.run();
        } catch (CmdLineException e) {
            LOG.error(e.getMessage());
            ByteArrayOutputStream err = new ByteArrayOutputStream();
            parser.printUsage(err);
            LOG.error(err.toString(java.nio.charset.StandardCharsets.UTF_8));
        }
        app.run();
    }
}
