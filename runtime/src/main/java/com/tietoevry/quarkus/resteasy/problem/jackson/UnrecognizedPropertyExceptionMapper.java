package com.tietoevry.quarkus.resteasy.problem.jackson;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;

/**
 * UnrecognizedPropertyException is thrown by Jackson, when request payload json does not fit DTO object with @Valid annotation
 * (e.g field has different name) and FAIL_ON_UNKNOWN_PROPERTIES is enabled (default changed in quarkus 1.11)
 */
@Priority(Priorities.USER - 1)
public final class UnrecognizedPropertyExceptionMapper extends ExceptionMapperBase<UnrecognizedPropertyException> {

    @Override
    protected HttpProblem toProblem(UnrecognizedPropertyException exception) {
        return HttpProblem.valueOf(BAD_REQUEST, exception.getOriginalMessage());
    }
}
