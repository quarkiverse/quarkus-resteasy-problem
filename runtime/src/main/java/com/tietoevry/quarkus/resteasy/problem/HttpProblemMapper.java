package com.tietoevry.quarkus.resteasy.problem;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;

@Priority(Priorities.USER)
public final class HttpProblemMapper extends ExceptionMapperBase<HttpProblem> {

    @Override
    protected HttpProblem toProblem(HttpProblem exception) {
        return exception;
    }
}
