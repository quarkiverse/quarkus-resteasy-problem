package com.tietoevry.quarkus.resteasy.problem;

import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.USER)
public class ProblemMapper extends ExceptionMapperBase<ThrowableProblem> {

    @Override
    protected Problem toProblem(ThrowableProblem exception) {
        return exception;
    }
}
