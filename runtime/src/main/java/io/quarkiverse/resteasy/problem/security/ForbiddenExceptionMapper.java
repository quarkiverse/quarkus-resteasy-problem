package io.quarkiverse.resteasy.problem.security;

import static jakarta.ws.rs.core.Response.Status.FORBIDDEN;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;

import io.quarkiverse.resteasy.problem.ExceptionMapperBase;
import io.quarkiverse.resteasy.problem.HttpProblem;
import io.quarkus.security.ForbiddenException;

/**
 * Mapper overriding default Quarkus exception mapper to make all error responses compliant with RFC7807.
 */
@Priority(Priorities.USER)
public final class ForbiddenExceptionMapper extends ExceptionMapperBase<ForbiddenException> {

    @Override
    protected HttpProblem toProblem(ForbiddenException e) {
        return HttpProblem.valueOf(FORBIDDEN, e.getMessage());
    }

}
