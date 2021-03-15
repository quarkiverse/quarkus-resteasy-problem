package com.tietoevry.quarkus.resteasy.problem.security;

import static org.zalando.problem.Status.FORBIDDEN;

import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import io.quarkus.security.ForbiddenException;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.Provider;
import org.zalando.problem.Problem;

@Provider
@Priority(Priorities.USER)
public class ForbiddenExceptionMapper extends ExceptionMapperBase<ForbiddenException> {

    @Override
    protected Problem toProblem(ForbiddenException e) {
        return Problem.valueOf(FORBIDDEN, e.getMessage());
    }

}
