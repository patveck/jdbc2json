package com.pascalvaneck.jdbc2json.server.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Request;

import javax.annotation.Nonnull;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

public class IpBasedFilter implements ContainerRequestFilter {

    private static final Log LOG = LogFactory.getLog(IpBasedFilter.class);

    /**
     *  One would normally inject a httpServletRequest like so: @Context private HttpServletRequest req;
     *  However, as indicated at {@link jersey/containers/jetty-http/src/main/java/org/glassfish/jersey/jetty/JettyHttpContainer.java},
     *  The Jetty Jersey container injects a Jetty Request, not a container-agnostic HttpServletRequest. Moreover,
     *  it injects a javax.inject.Provider, because a Jetty Request is not proxiable.
     */
    @Context
    private javax.inject.Provider<Request> req;

    private final HashSet<String> whiteList;
    private final HashSet<String> blackList;

    public IpBasedFilter(@Nonnull final String[] whiteList, @Nonnull final String[] blackList) {
        this.whiteList = new HashSet<String>(Arrays.asList((whiteList)));
        this.blackList = new HashSet<String>(Arrays.asList((blackList)));
    }

    @Override
    public void filter(@Nonnull final ContainerRequestContext requestContext) throws IOException {
        if (req == null) {
            LOG.fatal("No HTTP request available to filter.");
            requestContext.abortWith(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Cannot access resource")
                .build());
        }
        final String remoteAddr = req.get().getRemoteAddr();
        LOG.debug(String.format("IpBasedFilter::filter starting for address %s.", remoteAddr));
        if ((!blackList.isEmpty() && blackList.contains(remoteAddr)) ||
            (!whiteList.isEmpty() && !whiteList.contains(remoteAddr))) {
            LOG.warn(String.format("IpBasedFilter:: filter ended for address %s (blocked).", remoteAddr));
            requestContext.abortWith(Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity("User cannot access the resource.")
                .build());
        } else {
            LOG.debug(String.format("IpBasedFilter::filter ended for address %s (pass).", remoteAddr));
        }
    }
}
