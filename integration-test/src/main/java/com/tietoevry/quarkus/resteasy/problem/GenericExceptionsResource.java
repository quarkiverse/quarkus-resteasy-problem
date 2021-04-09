package com.tietoevry.quarkus.resteasy.problem;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/throw/generic/")
@Produces(MediaType.APPLICATION_JSON)
public class GenericExceptionsResource {

    @GET
    @Path("/runtime-exception")
    public void throwRuntimeException(@QueryParam("message") String message) {
        throw new TestRuntimeException(message);
    }

    static final class TestRuntimeException extends RuntimeException {
        TestRuntimeException(String message) {
            super(message, new RuntimeException("Root cause"));
        }
    }

    @GET
    @Path("/http-problem")
    public void throwHttpProblem(@QueryParam("status") int status, @QueryParam("title") String title,
            @QueryParam("detail") String detail) {

        throw HttpProblem.builder()
                .withHeader("X-RFC7807", "IsAlive")
                .withStatus(Response.Status.fromStatusCode(status))
                .withTitle(title)
                .withDetail(detail)
                .build();
    }

}
