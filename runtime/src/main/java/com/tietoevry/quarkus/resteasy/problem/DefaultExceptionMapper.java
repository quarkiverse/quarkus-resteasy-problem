package com.tietoevry.quarkus.resteasy.problem;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;

/**
 * Default exception mapper processing all exceptions not matching any more specific mapper.
 */
@Priority(Priorities.USER)
public final class DefaultExceptionMapper extends ExceptionMapperBase<Exception> {

    @Override
    protected HttpProblem toProblem(Exception exception) {
        return HttpProblem.valueOf(INTERNAL_SERVER_ERROR);
    }
}
