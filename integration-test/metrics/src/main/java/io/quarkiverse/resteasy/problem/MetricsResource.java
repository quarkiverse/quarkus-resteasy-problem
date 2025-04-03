package io.quarkiverse.resteasy.problem;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;

@Path("/metrics/")
@Produces(MediaType.APPLICATION_JSON)
public class MetricsResource {

    @GET
    @Path("/throw-web-application-exception")
    public void throwWebApplicationException(@QueryParam("status") int status) {
        throw new WebApplicationException(new TestRuntimeException(), status);
    }

    private static final class TestRuntimeException extends RuntimeException {
        TestRuntimeException() {
            super("First cause", new RuntimeException("Root cause"));
        }
    }

}
