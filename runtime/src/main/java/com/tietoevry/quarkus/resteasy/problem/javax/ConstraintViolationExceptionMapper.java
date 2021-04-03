package com.tietoevry.quarkus.resteasy.problem.javax;

import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import java.util.Set;
import javax.annotation.Priority;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Priorities;
import org.zalando.problem.Problem;

/**
 * Exception Mapper for ConstraintViolationException from Bean Validation API. Hibernate Validator, among others throw
 * these exceptions. Adds 'violations' field into `application/problem` responses.
 */
@Priority(Priorities.USER)
public final class ConstraintViolationExceptionMapper extends ExceptionMapperBase<ConstraintViolationException> {

    @Override
    public Problem toProblem(ConstraintViolationException exception) {
        final Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        return new ConstraintViolationProblem(constraintViolations);
    }
}
