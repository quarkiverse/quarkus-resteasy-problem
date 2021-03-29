package com.tietoevry.quarkus.resteasy.problem.jaxrs;

import static org.zalando.problem.Status.NOT_FOUND;

import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import javax.annotation.Priority;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Priorities;
import org.zalando.problem.Problem;

/**
 * Mapper overriding default Quarkus exception mapper to make all error responses compliant with RFC7807.
 *
 * @see io.quarkus.resteasy.runtime.NotFoundExceptionMapper
 */
@Priority(Priorities.USER)
public class NotFoundExceptionMapper extends ExceptionMapperBase<NotFoundException> {

    @Override
    public Problem toProblem(NotFoundException exception) {
        return Problem.valueOf(NOT_FOUND, exception.getMessage());
    }
}
