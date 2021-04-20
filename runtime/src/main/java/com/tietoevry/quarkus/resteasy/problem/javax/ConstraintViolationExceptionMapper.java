package com.tietoevry.quarkus.resteasy.problem.javax;

import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Priority;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.Response;

/**
 * Exception Mapper for ConstraintViolationException from Bean Validation API. Hibernate Validator, among others throw
 * these exceptions. Adds 'violations' field into `application/problem` responses.
 */
@Priority(Priorities.USER)
public final class ConstraintViolationExceptionMapper extends ExceptionMapperBase<ConstraintViolationException> {

    @Override
    protected HttpProblem toProblem(ConstraintViolationException exception) {
        List<Violation> violations = exception.getConstraintViolations()
                .stream()
                .map(this::toViolation)
                .collect(Collectors.toList());

        return HttpProblem.builder()
                .withStatus(Response.Status.BAD_REQUEST)
                .withTitle(Response.Status.BAD_REQUEST.getReasonPhrase())
                .with("violations", violations)
                .build();
    }

    private Violation toViolation(ConstraintViolation<?> constraintViolation) {
        return new Violation(
                constraintViolation.getMessage(),
                dropMethodNameAndArgumentPositionFromPath(constraintViolation.getPropertyPath()));
    }

    private String dropMethodNameAndArgumentPositionFromPath(Path propertyPath) {
        return StreamSupport.stream(propertyPath.spliterator(), false)
                .reduce((first, second) -> second)
                .map(Path.Node::getName)
                .orElse("");
    }

}
