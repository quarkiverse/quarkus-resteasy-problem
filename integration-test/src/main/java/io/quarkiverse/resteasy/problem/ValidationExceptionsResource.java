package io.quarkiverse.resteasy.problem;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
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
