package com.tietoevry.quarkus.resteasy.problem;

import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import org.zalando.problem.Problem;

@Priority(Priorities.USER)
public class DefaultExceptionMapper extends ExceptionMapperBase<Exception> {

    @Override
    protected Problem toProblem(Exception exception) {
        return Problem.valueOf(INTERNAL_SERVER_ERROR);
    }
}
