package com.tietoevry.quarkus.resteasy.problem.javax;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import javax.annotation.Priority;
import javax.validation.ValidationException;
import javax.ws.rs.Priorities;

/**
 * More generic Exception Mapper compared to ConstraintViolationException - does not provide any details except the message.
 */
@Priority(Priorities.USER)
public final class ValidationExceptionMapper extends ExceptionMapperBase<ValidationException> {

    @Override
    public HttpProblem toProblem(ValidationException exception) {
        return HttpProblem.valueOf(BAD_REQUEST, exception.getMessage());
    }
}
