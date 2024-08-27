package io.quarkiverse.resteasy.problem.validation;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import io.quarkiverse.resteasy.problem.ExceptionMapperBase;
import io.quarkiverse.resteasy.problem.HttpProblem;
import jakarta.annotation.Priority;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.Priorities;

/**
 * Exception Mapper for generic ValidationException from Bean Validation API.
 * Unlike ConstraintViolationException these are not thrown if the input fails validation,
 * but are instead thrown on invalid use of the API.
 */
@Priority(Priorities.USER)
public final class ValidationExceptionMapper extends ExceptionMapperBase<ValidationException> {

    @Override
    protected HttpProblem toProblem(ValidationException exception) {
        return HttpProblem.valueOf(INTERNAL_SERVER_ERROR);
    }
}
