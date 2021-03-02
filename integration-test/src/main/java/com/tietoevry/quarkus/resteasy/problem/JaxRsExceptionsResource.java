package com.tietoevry.quarkus.resteasy.problem;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

@Path("/throw/jax-rs/")
@Produces(MediaType.APPLICATION_JSON)
public class JaxRsExceptionsResource {

    @GET
    @Path("/web-application-exception")
    public void throwWebApplicationException(@QueryParam("status") int status) {
        throw new WebApplicationException(new TestRuntimeException(), status);
    }

    @GET
    @Path("/not-found-exception")
    public void throwNotFoundException(@QueryParam("message") String message) {
        throw new NotFoundException(message);
    }

    @GET
    @Path("/forbidden-exception")
    public void throwForbiddenException(@QueryParam("message") String message) {
        throw new ForbiddenException(message);
    }

    private static final class TestRuntimeException extends RuntimeException {
        TestRuntimeException() {
            super("First cause", new RuntimeException("Root cause"));
        }
    }

}