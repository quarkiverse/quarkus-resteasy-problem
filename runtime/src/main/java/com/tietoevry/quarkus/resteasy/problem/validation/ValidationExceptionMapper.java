package com.tietoevry.quarkus.resteasy.problem.validation;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import jakarta.annotation.Priority;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.Priorities;

/**
 * More generic Exception Mapper compared to ConstraintViolationException - does not provide any details except the message.
 */
@Priority(Priorities.USER)
public final class ValidationExceptionMapper extends ExceptionMapperBase<ValidationException> {

    @Override
    protected HttpProblem toProblem(ValidationException exception) {
        return HttpProblem.valueOf(BAD_REQUEST, exception.getMessage());
    }
}
