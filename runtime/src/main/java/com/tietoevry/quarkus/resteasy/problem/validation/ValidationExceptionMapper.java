package com.tietoevry.quarkus.resteasy.problem.validation;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import javax.annotation.Priority;
import javax.validation.ValidationException;
import javax.ws.rs.Priorities;

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
