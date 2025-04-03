package io.quarkiverse.resteasy.problem.client;

import io.quarkiverse.resteasy.problem.HttpProblem;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DemoResource {

    @Inject
    @RestClient
    SelfRestClient selfClient;

    @GET
    @Path("/throw-via-rest-client")
    public void throwViaRestClient() {
        selfClient.doThrow(418, "I'm a teapot", "Nothing to add");
    }

    @GET
    @Path("/throw")
    public void throwProblem(@QueryParam("status") int status, @QueryParam("title") String title,
                             @QueryParam("detail") String detail) {
        throw HttpProblem.builder()
                .withTitle(title)
                .withStatus(status)
                .withDetail(detail)
                .build();
    }
}