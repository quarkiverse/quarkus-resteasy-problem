package com.tietoevry.quarkus.resteasy.problem;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

/**
 * Mapper for ThrowableProblem exception from Zalando Problem library.
 */
@Priority(Priorities.USER)
public final class ProblemMapper extends ExceptionMapperBase<ThrowableProblem> {

    @Override
    protected Problem toProblem(ThrowableProblem exception) {
        if (exception.getStatus() != null) {
            return exception;
        }
        return ProblemUtils.toBuilder(exception)
                .withStatus(Status.INTERNAL_SERVER_ERROR)
                .build();
    }

}
