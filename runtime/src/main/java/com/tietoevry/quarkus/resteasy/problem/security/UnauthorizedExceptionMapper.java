package com.tietoevry.quarkus.resteasy.problem.security;

import static org.zalando.problem.Status.UNAUTHORIZED;

import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import io.quarkus.security.UnauthorizedException;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import org.zalando.problem.Problem;

/**
 * Overriding default RESTEasy exception mapper to make all error responses compliant with RFC7807.
 *
 * @see io.quarkus.resteasy.runtime.UnauthorizedExceptionMapper
 */
@Priority(Priorities.USER)
public class UnauthorizedExceptionMapper extends ExceptionMapperBase<UnauthorizedException> {

    @Override
    protected Problem toProblem(UnauthorizedException exception) {
        return Problem.valueOf(UNAUTHORIZED, exception.getMessage());
    }

}
