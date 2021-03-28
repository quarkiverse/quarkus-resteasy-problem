package com.tietoevry.quarkus.resteasy.problem.jsonb;

import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import javax.annotation.Priority;
import javax.json.bind.JsonbException;
import javax.ws.rs.Priorities;
import javax.ws.rs.ProcessingException;
import org.zalando.problem.Problem;

@Priority(Priorities.USER)
public class JsonbExceptionMapper extends ExceptionMapperBase<ProcessingException> {

    /**
     * Unfortunately Quarkus+JsonB throws ProcessingException, not JsonbException in case of malformed payload body, so `cause`
     * needs to be checked explicitly.
     */
    @Override
    public Problem toProblem(ProcessingException exception) {
        if (exception.getCause() instanceof JsonbException) {
            return Problem.valueOf(BAD_REQUEST, exception.getCause().getMessage());
        } else {
            return Problem.valueOf(INTERNAL_SERVER_ERROR);
        }
    }
}
