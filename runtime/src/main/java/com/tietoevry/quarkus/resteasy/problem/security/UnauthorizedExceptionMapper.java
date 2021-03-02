package com.tietoevry.quarkus.resteasy.problem.security;

import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import io.quarkus.security.UnauthorizedException;
import org.zalando.problem.Problem;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.Provider;

import static org.zalando.problem.Status.UNAUTHORIZED;

/**
 * Overriding default RESTEasy exception mapper to make all error responses compliant with RFC7807.
 *
 * @see io.quarkus.resteasy.runtime.UnauthorizedExceptionMapper
 */
@Provider
@Priority(Priorities.USER)
public class UnauthorizedExceptionMapper extends ExceptionMapperBase<UnauthorizedException> {

    @Override
    protected Problem toProblem(UnauthorizedException exception) {
        return Problem.valueOf(UNAUTHORIZED, exception.getMessage());
    }

}
