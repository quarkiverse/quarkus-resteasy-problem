package com.tietoevry.quarkus.resteasy.problem.jaxrs;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import jakarta.annotation.Priority;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Priorities;

@Priority(Priorities.USER)
public final class NotFoundExceptionMapper extends ExceptionMapperBase<NotFoundException> {

    @Override
    protected HttpProblem toProblem(NotFoundException exception) {
        return HttpProblem.valueOf(NOT_FOUND, exception.getMessage());
    }
}
