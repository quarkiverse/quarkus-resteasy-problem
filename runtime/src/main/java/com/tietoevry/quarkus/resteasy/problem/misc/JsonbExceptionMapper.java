package com.tietoevry.quarkus.resteasy.problem.misc;

import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import org.zalando.problem.Problem;

import javax.annotation.Priority;
import javax.json.bind.JsonbException;
import javax.ws.rs.Priorities;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.ext.Provider;

import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

/**
 * Unfortunately if payload body is malformed, and if JsonB is used, then JsonbException is thrown not directly,
 * but is wrapped into ProcessingException
 */
@Provider
@Priority(Priorities.USER)
public class JsonbExceptionMapper extends ExceptionMapperBase<ProcessingException> {

    @Override
    public Problem toProblem(ProcessingException exception) {
        if (exception.getCause() instanceof JsonbException) {
            return Problem.valueOf(BAD_REQUEST, exception.getCause().getMessage());
        } else {
            return Problem.valueOf(INTERNAL_SERVER_ERROR);
        }
    }
}
