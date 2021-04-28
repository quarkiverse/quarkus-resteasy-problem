package com.tietoevry.quarkus.resteasy.problem.javax;

import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import com.tietoevry.quarkus.resteasy.problem.HttpProblem;

import javax.annotation.Priority;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Priorities;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Exception Mapper for ConstraintViolationException from Bean Validation API. Hibernate Validator, among others throw
 * these exceptions. Adds 'violations' field into `application/problem` responses.
 */
@Priority(Priorities.USER)
public final class ConstraintViolationExceptionMapper extends ExceptionMapperBase<ConstraintViolationException> {

    @Context
    ResourceInfo resourceInfo;

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
        return matchEndpointMethodParameter(constraintViolation)
                .map(param -> createViolation(constraintViolation, param))
                .orElseGet(() -> Violation.In.unknown
                        .violation(dropMethodName(constraintViolation.getPropertyPath()))
                        .message(constraintViolation.getMessage()));
    }

    private Optional<Parameter> matchEndpointMethodParameter(ConstraintViolation<?> violation) {
        Iterator<Path.Node> propertyPathIterator = violation.getPropertyPath().iterator();
        if (!propertyPathIterator.hasNext()) {
            return Optional.empty();
        }
        propertyPathIterator.next();
        if (!propertyPathIterator.hasNext()) {
            return Optional.empty();
        }
        String paramName = propertyPathIterator.next().getName();
        Method method = resourceInfo.getResourceMethod();
        return Stream.of(method.getParameters())
                .filter(param -> param.getName().equals(paramName))
                .findFirst();
    }

    private Violation createViolation(ConstraintViolation<?> constraintViolation, Parameter param) {
        final String message = constraintViolation.getMessage();
        if (param.getAnnotation(QueryParam.class) != null) {
            String field = param.getAnnotation(QueryParam.class).value();
            return Violation.In.query.violation(field).message(message);
        }

        if (param.getAnnotation(PathParam.class) != null) {
            String field = param.getAnnotation(PathParam.class).value();
            return Violation.In.path.violation(field).message(message);
        }

        if (param.getAnnotation(HeaderParam.class) != null) {
            String field = param.getAnnotation(HeaderParam.class).value();
            return Violation.In.header.violation(field).message(message);
        }

        if (param.getAnnotation(FormParam.class) != null) {
            String field = param.getAnnotation(FormParam.class).value();
            return Violation.In.form.violation(field).message(message);
        }

        String field = dropMethodNameAndArgumentPositionFromPath(constraintViolation.getPropertyPath());
        return Violation.In.body.violation(field).message(message);
    }

    private String dropMethodNameAndArgumentPositionFromPath(Path propertyPath) {
        Iterator<Path.Node> propertyPathIterator = propertyPath.iterator();
        propertyPathIterator.next();
        propertyPathIterator.next();

        return getAllNamesExceptFirstTwo(propertyPathIterator);
    }

    private String dropMethodName(Path propertyPath) {
        Iterator<Path.Node> propertyPathIterator = propertyPath.iterator();
        propertyPathIterator.next();

        return getAllNamesExceptFirstTwo(propertyPathIterator);
    }

    private String getAllNamesExceptFirstTwo(Iterator<Path.Node> propertyPathIterator) {
        List<String> allNamesExceptFirstTwo = new ArrayList<>();
        while (propertyPathIterator.hasNext()) {
            allNamesExceptFirstTwo.add(propertyPathIterator.next().toString());
        }

        return String.join(".", allNamesExceptFirstTwo);
    }

}
