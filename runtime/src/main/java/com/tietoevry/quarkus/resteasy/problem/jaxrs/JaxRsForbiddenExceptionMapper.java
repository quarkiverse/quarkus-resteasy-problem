package com.tietoevry.quarkus.resteasy.problem.jaxrs;

import static javax.ws.rs.core.Response.Status.FORBIDDEN;

import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import javax.annotation.Priority;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Priorities;

/**
 * Mapper overriding default Quarkus exception mapper to make all error responses compliant with RFC7807.
 *
 * @see io.quarkus.resteasy.runtime.ForbiddenExceptionMapper
 */
@Priority(Priorities.USER)
public final class JaxRsForbiddenExceptionMapper extends ExceptionMapperBase<ForbiddenException> {

    @Override
    protected HttpProblem toProblem(ForbiddenException e) {
        return HttpProblem.valueOf(FORBIDDEN, e.getMessage());
    }

}
