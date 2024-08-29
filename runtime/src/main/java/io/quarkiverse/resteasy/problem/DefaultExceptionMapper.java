package io.quarkiverse.resteasy.problem;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;

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
