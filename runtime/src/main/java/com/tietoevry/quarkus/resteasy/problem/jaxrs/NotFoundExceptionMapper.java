package com.tietoevry.quarkus.resteasy.problem.jaxrs;

import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import org.zalando.problem.Problem;

import javax.annotation.Priority;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.Provider;

import static org.zalando.problem.Status.NOT_FOUND;

/**
 * Overriding default RESTEasy exception mapper to make all error responses compliant with RFC7807.
 *
 * @see io.quarkus.resteasy.runtime.NotFoundExceptionMapper
 */
@Provider
@Priority(Priorities.USER)
public class NotFoundExceptionMapper extends ExceptionMapperBase<NotFoundException> {

    @Override
    public Problem toProblem(NotFoundException exception) {
        return Problem.valueOf(NOT_FOUND, exception.getMessage());
    }
}
