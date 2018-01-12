package com.pascalvaneck.jdbc2json.server.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("{database: [A-Za-z][A-Za-z0-9_]*}")
public class ApiController {

    private static final Log LOG = LogFactory.getLog(ApiController.class);

    @GET
    @Path("hello")
    @Produces(MediaType.APPLICATION_JSON)
    public Response helloWorld(@PathParam( "database" ) String name) {
        LOG.info("About to return a greeting in the name of %s.");
        return Response.status(Response.Status.OK).entity(String.format("Hello, %s!", name)).build();
    }
}
