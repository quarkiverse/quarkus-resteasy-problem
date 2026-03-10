package io.quarkiverse.resteasy.problem.client;

import io.quarkiverse.resteasy.problem.HttpProblem;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DemoResource {

    @GET
    @Path("/throw")
    @Produces()
    public Response throwProblem() {
        throw HttpProblem.builder()
                .withStatus(409)
                .withTitle("Conflict from upstream service")
                .withDetail("Nothing to add")
                .build();
    }

    @Inject
    @RestClient
    SelfRestClient selfClient;

    @GET
    @Path("/throw-via-rest-client")
    public void throwViaRestClient() {
        selfClient.doThrow();
    }

    @Inject
    @RestClient
    SelfRestClientWithExceptionMapper selfClientWithMapper;

    @GET
    @Path("/throw-via-rest-client-with-mapper")
    public void throwViaRestClientWithMapper() {
        selfClientWithMapper.doThrow();
    }

    @GET
    @Path("/upstream/{segment}/throw")
    public Response throwAtPathWithSegment(@PathParam("segment") String segment) {
        throw HttpProblem.builder()
                .withStatus(404)
                .withTitle("Not Found")
                .build();
    }

    @GET
    @Path("/throw-via-rest-client-with-encoded-segment")
    public void throwViaRestClientWithEncodedSegment() {
        selfClientWithMapper.doThrowAtPathWithSegment("X 1");
    }

}