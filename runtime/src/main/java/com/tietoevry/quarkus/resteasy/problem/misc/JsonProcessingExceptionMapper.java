package com.tietoevry.quarkus.resteasy.problem.misc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import org.zalando.problem.Problem;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.Provider;

import static org.zalando.problem.Status.BAD_REQUEST;

@Provider
@Priority(Priorities.USER)
public class JsonProcessingExceptionMapper extends ExceptionMapperBase<JsonProcessingException> {

    @Override
    public Problem toProblem(JsonProcessingException exception) {
        return Problem.valueOf(BAD_REQUEST, exception.getMessage());
    }
}
