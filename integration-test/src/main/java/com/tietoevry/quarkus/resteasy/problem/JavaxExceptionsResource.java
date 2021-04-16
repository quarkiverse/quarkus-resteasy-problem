package com.tietoevry.quarkus.resteasy.problem;

import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/throw/javax/")
@Produces(MediaType.APPLICATION_JSON)
public class JavaxExceptionsResource {

    @GET
    @Path("/violation-exception")
    public void throwViolationException(@QueryParam("message") String message) {
        throw new ValidationException(message);
    }

    @POST
    @Path("/constraint-violation-exception")
    public void throwConstraintViolationException(@Valid TestRequestBody body) {
    }

    public static final class TestRequestBody {
        @Min(15)
        public int key;
    }

    @GET
    @Path("/constraint-violation-exception-primitive")
    public void throwConstraintViolationException(@Valid @Length(min = 10) String phrase) {
    }
}