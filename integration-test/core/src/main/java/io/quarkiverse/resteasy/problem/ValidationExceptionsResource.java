package io.quarkiverse.resteasy.problem;

import java.util.Set;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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

    @Inject
    Validator validator;

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

    @POST
    @Path("/constraint-violation-exception/programmatic")
    public void throwConstraintViolationExceptionProgrammatic(@QueryParam("name") String name) {
        ProgrammaticTestBean bean = new ProgrammaticTestBean();
        bean.name = name;
        bean.email = "invalid-email";
        bean.age = 5;
        
        Set<ConstraintViolation<ProgrammaticTestBean>> violations = validator.validate(bean);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    @POST
    @Path("/constraint-violation-exception/programmatic/nested")
    public void throwConstraintViolationExceptionProgrammaticNested(@QueryParam("companyName") String companyName) {
        ProgrammaticNestedTestBean bean = new ProgrammaticNestedTestBean();
        bean.companyName = companyName;
        bean.address = new ProgrammaticTestAddress();
        bean.address.street = "";
        bean.address.city = "A"; // Too short city name
        
        Set<ConstraintViolation<ProgrammaticNestedTestBean>> violations = validator.validate(bean);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    public static final class TestRequestBody {
        @Min(15)
        public int phraseName;
    }

    public static final class ProgrammaticTestBean {
        @NotNull
        @Length(min = 2, max = 50)
        public String name;
        
        @NotNull
        @jakarta.validation.constraints.Email
        public String email;
        
        @Min(18)
        public int age;
    }

    public static final class ProgrammaticNestedTestBean {
        @NotNull
        @Length(min = 3, max = 100)
        public String companyName;
        
        @Valid
        @NotNull
        public ProgrammaticTestAddress address;
    }

    public static final class ProgrammaticTestAddress {
        @NotNull
        @Length(min = 5, max = 200)
        public String street;
        
        @NotNull
        @Length(min = 2, max = 100)
        public String city;
    }

}
