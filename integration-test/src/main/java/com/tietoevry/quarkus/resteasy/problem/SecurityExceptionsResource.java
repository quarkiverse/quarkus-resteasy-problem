package com.tietoevry.quarkus.resteasy.problem;

import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.ForbiddenException;
import io.quarkus.security.UnauthorizedException;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/throw/security/")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class SecurityExceptionsResource {

    @GET
    @Path("/unauthorized-exception")
    public void throwUnauthorizedException(@QueryParam("message") String message) {
        throw new UnauthorizedException(message);
    }

    @GET
    @Path("/authentication-failed-exception")
    public void throwAuthenticationFailedException(@QueryParam("message") String message) {
        throw new AuthenticationFailedException(message);
    }

    @GET
    @Path("/forbidden-exception")
    public void throwForbiddenException(@QueryParam("message") String message) {
        throw new ForbiddenException(message);
    }

    @RolesAllowed("rfc7807")
    @GET
    @Path("/secured-resource")
    public void securedResource() {
    }

}
