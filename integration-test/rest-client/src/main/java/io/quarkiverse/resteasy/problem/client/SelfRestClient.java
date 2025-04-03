package io.quarkiverse.resteasy.problem.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "self")
@RegisterProvider(value = HttpProblemClientExceptionMapper.class)
public interface SelfRestClient {
    @GET
    @Path("/throw")
    void doThrow(@QueryParam("status") int status, @QueryParam("title") String title, @QueryParam("detail") String detail);
}