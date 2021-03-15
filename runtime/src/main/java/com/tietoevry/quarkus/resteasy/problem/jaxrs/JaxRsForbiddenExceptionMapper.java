package com.tietoevry.quarkus.resteasy.problem.jaxrs;

import static org.zalando.problem.Status.FORBIDDEN;

import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import javax.annotation.Priority;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.Provider;
import org.zalando.problem.Problem;

/**
 * Overriding default resteasy ExceptionMapper to make all error responses compliant with RFC7807.
 *
 * @see io.quarkus.resteasy.runtime.ForbiddenExceptionMapper
 */
@Provider
@Priority(Priorities.USER)
public class JaxRsForbiddenExceptionMapper extends ExceptionMapperBase<ForbiddenException> {

    @Override
    protected Problem toProblem(ForbiddenException e) {
        return Problem.valueOf(FORBIDDEN, e.getMessage());
    }

}
