package com.tietoevry.quarkus.resteasy.problem.jackson;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.Response;

/**
 * UnrecognizedPropertyException is thrown by Jackson, when request payload json does not fit DTO object with @Valid annotation
 * (e.g field has different name) and FAIL_ON_UNKNOWN_PROPERTIES is enabled (default changed in quarkus 1.11)
 */
@Priority(Priorities.USER - 1)
public final class UnrecognizedPropertyExceptionMapper extends ExceptionMapperBase<UnrecognizedPropertyException> {

    @Override
    protected HttpProblem toProblem(UnrecognizedPropertyException exception) {
        String msg = "Unrecognized field \"" + exception.getPropertyName() + "\", not marked as ignorable";

        return HttpProblem.builder()
                .withStatus(Response.Status.BAD_REQUEST)
                .withTitle(Response.Status.BAD_REQUEST.getReasonPhrase())
                .withDetail(msg)
                .with("field", exception.getPropertyName())
                .build();
    }
}
