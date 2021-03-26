package com.tietoevry.quarkus.resteasy.problem;

import static javax.ws.rs.core.HttpHeaders.RETRY_AFTER;

import java.net.URI;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.RedirectionException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/throw/jax-rs/")
@Produces(MediaType.APPLICATION_JSON)
public class JaxRsExceptionsResource {

    static final MediaType MEDIA_TYPE_SHOULD_BE_IGNORED = MediaType.TEXT_PLAIN_TYPE;

    @GET
    @Path("/web-application-exception")
    public void throwWebApplicationException(@QueryParam("status") int status) {
        throw new WebApplicationException(new TestRuntimeException(), status);
    }

    @GET
    @Path("/web-application-exception-with-headers")
    public void throwWebApplicationExceptionWithHeaders(@QueryParam("status") int status) {
        throw new WebApplicationException(
                Response.status(status)
                        .header(RETRY_AFTER, 120)
                        .type(MEDIA_TYPE_SHOULD_BE_IGNORED)
                        .build());
    }

    @GET
    @Path("/redirection-exception")
    public void throwRedirection() {
        throw new RedirectionException(
                Response.Status.MOVED_PERMANENTLY,
                URI.create("/new-location"));
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
