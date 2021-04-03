package com.tietoevry.quarkus.resteasy.problem;

import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import org.zalando.problem.Problem;

/**
 * Default exception mapper processing all exceptions not matching any more specific mapper.
 */
@Priority(Priorities.USER)
public final class DefaultExceptionMapper extends ExceptionMapperBase<Exception> {

    @Override
    protected Problem toProblem(Exception exception) {
        return Problem.valueOf(INTERNAL_SERVER_ERROR);
    }
}
