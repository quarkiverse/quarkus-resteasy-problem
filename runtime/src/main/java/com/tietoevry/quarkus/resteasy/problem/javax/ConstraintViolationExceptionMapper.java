package com.tietoevry.quarkus.resteasy.problem.javax;

import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
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
    public HttpProblem toProblem(ConstraintViolationException exception) {
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
                dropFirstTwoPathElements(constraintViolation.getPropertyPath()));
    }

    private String dropFirstTwoPathElements(Path propertyPath) {
        Iterator<Path.Node> propertyPathIterator = propertyPath.iterator();
        propertyPathIterator.next();
        propertyPathIterator.next();

        List<String> allNamesExceptFirstTwo = new ArrayList<>();
        while (propertyPathIterator.hasNext()) {
            allNamesExceptFirstTwo.add(propertyPathIterator.next().getName());
        }

        return String.join(".", allNamesExceptFirstTwo);
    }

}
