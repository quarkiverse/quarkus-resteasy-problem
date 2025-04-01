package io.quarkiverse.resteasy.problem;

import io.quarkiverse.resteasy.problem.openapi.HttpProblemSchema;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/openapi/")
@Produces(APPLICATION_JSON)
public class OpenApiResource {

    @POST
    @Path("/hello-world")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Hello received and understood"),
            @APIResponse(responseCode = "403", description = "Hello received but rejected")
    })
    public String hello(@QueryParam("identity") String identity) {
        if ("quarkus".equals(identity)) {
            return "Hello " + identity;
        }

        throw HttpProblem.builder()
                .withStatus(Response.Status.FORBIDDEN)
                .withDetail("I know who you are but I don't trust you")
                .build();
    }

}
