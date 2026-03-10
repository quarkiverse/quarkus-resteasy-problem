package io.quarkiverse.resteasy.problem.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "self")
public interface SelfRestClient {
    @GET
    @Path("/throw")
    void doThrow();

    @GET
    @Path("/upstream/{segment}/throw")
    void doThrowAtPathWithSegment(@PathParam("segment") String segment);
}