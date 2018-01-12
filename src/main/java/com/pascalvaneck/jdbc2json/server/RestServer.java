package com.pascalvaneck.jdbc2json.server;

import com.pascalvaneck.jdbc2json.server.rest.IpBasedFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RestServer {

    private static final Log LOG = LogFactory.getLog(RestServer.class);

    private final Server jettyServer;

    public RestServer() {
        final ResourceConfig config = new ResourceConfig();
        config.packages("com.pascalvaneck.jdbc2json.server.rest");
        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
        config.register(new LoggingFeature(Logger.getLogger(LoggingFeature.DEFAULT_LOGGER_NAME),
            Level.INFO, LoggingFeature.Verbosity.PAYLOAD_ANY, Integer.MAX_VALUE));
        config.register(new IpBasedFilter(new String[] {"0:0:0:0:0:0:0:1", "127.0.0.1"}, new String[0]));
        final URI baseUri = UriBuilder.fromUri("http://localhost/").port(9998).build();
        jettyServer = JettyHttpContainerFactory.createServer(baseUri, config);
    }

    public void run() {
        try {
            jettyServer.start();
            jettyServer.join();
        } catch (Exception e) {
            LOG.fatal("Cannot start server", e);
        } finally {
            jettyServer.destroy();
        }
    }

    public static void main(String[] args) {
        RestServer server = new RestServer();
        server.run();
    }
}
