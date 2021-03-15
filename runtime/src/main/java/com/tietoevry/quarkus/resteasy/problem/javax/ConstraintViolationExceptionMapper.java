package com.tietoevry.quarkus.resteasy.problem.javax;

import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import org.zalando.problem.Problem;

import javax.annotation.Priority;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.Provider;
import java.util.Set;

@Provider
@Priority(Priorities.USER)
public class ConstraintViolationExceptionMapper extends ExceptionMapperBase<ConstraintViolationException> {

    @Override
    public Problem toProblem(ConstraintViolationException exception) {
        final Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        return new ConstraintViolationProblem(constraintViolations);
    }
}
