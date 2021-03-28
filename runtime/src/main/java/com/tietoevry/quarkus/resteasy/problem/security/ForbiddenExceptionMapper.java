package com.tietoevry.quarkus.resteasy.problem.security;

import static org.zalando.problem.Status.FORBIDDEN;

import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import io.quarkus.security.ForbiddenException;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import org.zalando.problem.Problem;

/**
 * Overriding default RESTEasy exception mapper to make all error responses compliant with RFC7807.
 *
 * @see io.quarkus.resteasy.runtime.ForbiddenExceptionMapper
 */
@Priority(Priorities.USER)
public class ForbiddenExceptionMapper extends ExceptionMapperBase<ForbiddenException> {

    @Override
    protected Problem toProblem(ForbiddenException e) {
        return Problem.valueOf(FORBIDDEN, e.getMessage());
    }

}
