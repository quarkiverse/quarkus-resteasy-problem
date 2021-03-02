package com.tietoevry.quarkus.resteasy.problem.javax;

import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import org.zalando.problem.Problem;

import javax.annotation.Priority;
import javax.validation.ValidationException;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.Provider;

import static org.zalando.problem.Status.BAD_REQUEST;

@Provider
@Priority(Priorities.USER)
public class ValidationExceptionMapper extends ExceptionMapperBase<ValidationException> {

    @Override
    public Problem toProblem(ValidationException exception) {
        return Problem.valueOf(BAD_REQUEST, exception.getMessage());
    }
}
