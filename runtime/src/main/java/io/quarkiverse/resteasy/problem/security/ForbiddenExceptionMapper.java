package io.quarkiverse.resteasy.problem.security;

import static jakarta.ws.rs.core.Response.Status.FORBIDDEN;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import io.quarkiverse.resteasy.problem.ExceptionMapperBase;
import io.quarkiverse.resteasy.problem.HttpProblem;
import io.quarkus.security.ForbiddenException;

/**
 * Mapper overriding default Quarkus exception mapper to make all error responses compliant with RFC7807.
 */
@Priority(Priorities.USER)
@APIResponse(responseCode = "403", description = "Forbidden: server understood the request but refused to process it")
public final class ForbiddenExceptionMapper extends ExceptionMapperBase<ForbiddenException>
        implements ExceptionMapper<ForbiddenException> {

    @Override
    protected HttpProblem toProblem(ForbiddenException e) {
        return HttpProblem.valueOf(FORBIDDEN, e.getMessage());
    }

}
