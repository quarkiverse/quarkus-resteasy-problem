package com.tietoevry.quarkus.resteasy.problem;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.hibernate.validator.constraints.Length;

@Path("/throw/validation/")
@Produces(MediaType.APPLICATION_JSON)
public class ValidationExceptionsResource {

    @GET
    @Path("/validation-exception")
    public void throwValidationException(@QueryParam("message") String message) {
        throw new ValidationException(message);
    }

    @GET
    @Path("/constraint-declaration-exception")
    public void throwConstraintDeclarationException(@AssertFalse @QueryParam("message") String message) {
    }

    @POST
    @Path("/constraint-violation-exception")
    public void throwConstraintViolationException(@Valid TestRequestBody body) {
    }

    @POST
    @Path("/constraint-violation-exception/{param_name4}")
    public void throwConstraintViolationException(
            @Valid @QueryParam("param_name") @Length(min = 10, max = 15) String invalidQueryParam,
            @Valid @QueryParam("param_name2") @Length(min = 1, max = 500) String validQueryParam,
            @Valid @HeaderParam("param_name3") @Length(min = 10, max = 15) String invalidHeaderParam,
            @Valid @PathParam("param_name4") @Length(min = 10, max = 15) String invalidPathParam,
            @Valid TestRequestBody invalidPayload) {
    }

    public static final class TestRequestBody {
        @Min(15)
        public int phraseName;
    }

}
