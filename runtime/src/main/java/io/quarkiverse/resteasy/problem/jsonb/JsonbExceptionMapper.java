package io.quarkiverse.resteasy.problem.jsonb;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import jakarta.annotation.Priority;
import jakarta.json.bind.JsonbException;
import jakarta.ws.rs.Priorities;

import io.quarkiverse.resteasy.problem.ExceptionMapperBase;
import io.quarkiverse.resteasy.problem.HttpProblem;

@Priority(Priorities.USER)
public final class JsonbExceptionMapper extends ExceptionMapperBase<JsonbException> {

    @Override
    protected HttpProblem toProblem(JsonbException exception) {
        return HttpProblem.valueOf(BAD_REQUEST, exception.getCause().getMessage());
    }
}
