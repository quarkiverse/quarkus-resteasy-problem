package io.quarkiverse.resteasy.problem;

import io.quarkus.security.ForbiddenException;
import io.quarkus.security.UnauthorizedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/openapi/")
@Produces(APPLICATION_JSON)
public class OpenApiResource {

    @POST
    @Path("/documented")
    @APIResponse(responseCode = "409", description = "Request received but there has been a conflict")
    // content intentionally not defined
    public String documentedEndpoint() {
        throw HttpProblem.builder()
                .withStatus(Response.Status.CONFLICT)
                .withDetail("There has been a conflict")
                .build();
    }

    @POST
    @Path("/throwing")
    public String throwingEndpoint() throws ForbiddenException, UnauthorizedException, NotFoundException {
        throw new ForbiddenException();
    }

    @POST
    @Path("/throwing-and-documented")
    @APIResponse(responseCode = "401", description = "You are unauthorized")
    public String throwingAndDocumentedEndpoint() throws Exception {
        throw new RuntimeException();
    }

}
