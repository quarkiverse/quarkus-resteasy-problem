package com.tietoevry.quarkus.resteasy.problem.jsonb;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import jakarta.annotation.Priority;
import jakarta.json.bind.JsonbException;
import jakarta.ws.rs.Priorities;

@Priority(Priorities.USER)
public final class JsonbExceptionMapper extends ExceptionMapperBase<JsonbException> {

    @Override
    protected HttpProblem toProblem(JsonbException exception) {
        return HttpProblem.valueOf(BAD_REQUEST, exception.getCause().getMessage());
    }
}
