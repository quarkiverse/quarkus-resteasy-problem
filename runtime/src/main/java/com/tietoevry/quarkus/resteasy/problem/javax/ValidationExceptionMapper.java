package com.tietoevry.quarkus.resteasy.problem.javax;

import static org.zalando.problem.Status.BAD_REQUEST;

import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import javax.annotation.Priority;
import javax.validation.ValidationException;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.Provider;
import org.zalando.problem.Problem;

@Provider
@Priority(Priorities.USER)
public class ValidationExceptionMapper extends ExceptionMapperBase<ValidationException> {

    @Override
    public Problem toProblem(ValidationException exception) {
        return Problem.valueOf(BAD_REQUEST, exception.getMessage());
    }
}
