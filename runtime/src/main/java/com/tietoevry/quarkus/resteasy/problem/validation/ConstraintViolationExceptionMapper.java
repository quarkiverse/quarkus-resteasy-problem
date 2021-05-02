package com.tietoevry.quarkus.resteasy.problem.validation;

import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
                        .field(dropMethodName(constraintViolation.getPropertyPath()))
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
            return Violation.In.query.field(field).message(message);
        }

        if (param.getAnnotation(PathParam.class) != null) {
            String field = param.getAnnotation(PathParam.class).value();
            return Violation.In.path.field(field).message(message);
        }

        if (param.getAnnotation(HeaderParam.class) != null) {
            String field = param.getAnnotation(HeaderParam.class).value();
            return Violation.In.header.field(field).message(message);
        }

        if (param.getAnnotation(FormParam.class) != null) {
            String field = param.getAnnotation(FormParam.class).value();
            return Violation.In.form.field(field).message(message);
        }

        String field = dropMethodNameAndArgumentPositionFromPath(constraintViolation.getPropertyPath());
        return Violation.In.body.field(field).message(message);
    }

    private String dropMethodNameAndArgumentPositionFromPath(Path propertyPath) {
        return serializePath(propertyPath, 2);
    }

    private String dropMethodName(Path propertyPath) {
        return serializePath(propertyPath, 1);
    }

    private String serializePath(Path propertyPath, int skipFirstSegments) {
        Iterator<Path.Node> segmentIterator = propertyPath.iterator();

        List<String> pathSegments = new ArrayList<>();
        while (segmentIterator.hasNext()) {
            if (skipFirstSegments > 0) {
                skipFirstSegments -= 1;
                segmentIterator.next();
            } else {
                pathSegments.add(segmentIterator.next().toString());
            }
        }
        return String.join(".", pathSegments);
    }

}
