package com.tietoevry.quarkus.resteasy.problem.jsonb;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import javax.annotation.Priority;
import javax.json.bind.JsonbException;
import javax.ws.rs.Priorities;

@Priority(Priorities.USER)
public final class ReactiveJsonbExceptionMapper extends ExceptionMapperBase<JsonbException> {

    @Override
    public HttpProblem toProblem(JsonbException exception) {
        return HttpProblem.valueOf(BAD_REQUEST, exception.getCause().getMessage());
    }
}
