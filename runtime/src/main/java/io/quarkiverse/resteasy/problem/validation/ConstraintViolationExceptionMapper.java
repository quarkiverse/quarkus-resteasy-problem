package io.quarkiverse.resteasy.problem.validation;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.annotation.Priority;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import io.quarkiverse.resteasy.problem.ConstraintViolationMapperConfig;
import io.quarkiverse.resteasy.problem.ExceptionMapperBase;

/**
 * Exception Mapper for ConstraintViolationException from Bean Validation API. Hibernate Validator, among others throw
 * these exceptions. Adds 'violations' field into `application/problem` responses.
 */
@Priority(Priorities.USER)
@APIResponse(responseCode = ConstraintViolationExceptionMapper.HTTP_VALIDATION_PROBLEM_STATUS_CODE)
public final class ConstraintViolationExceptionMapper extends ExceptionMapperBase<ConstraintViolationException>
        implements ExceptionMapper<ConstraintViolationException> {

    /**
     * APIResponse annotations' responseCode must be set according to the configuration, it can't be hardcoded as for
     * other mappers. OpenApiProblemFilter handles this accordingly.
     */
    public static final String HTTP_VALIDATION_PROBLEM_STATUS_CODE = "[HttpValidationProblem]";

    private static ConstraintViolationMapperConfig config = ConstraintViolationMapperConfig.defaults();

    @Context
    ResourceInfo resourceInfo;

    public static void configure(ConstraintViolationMapperConfig config) {
        ConstraintViolationExceptionMapper.config = config;
    }

    @Override
    protected HttpValidationProblem toProblem(ConstraintViolationException exception) {
        return new HttpValidationProblem(config.status(), config.title(), toViolations(exception.getConstraintViolations()));
    }

    private List<Violation> toViolations(Set<ConstraintViolation<?>> constraintViolations) {
        if (constraintViolations == null) {
            return List.of();
        }
        return constraintViolations
                .stream()
                .map(this::toViolation)
                .collect(Collectors.toList());
    }

    private Violation toViolation(ConstraintViolation<?> constraintViolation) {
        return matchEndpointMethodParameter(constraintViolation)
                .map(param -> createViolation(constraintViolation, param))
                .orElseGet(() -> {
                    String field = isDeclarativeValidation(constraintViolation)
                            ? dropMethodName(constraintViolation.getPropertyPath())
                            : constraintViolation.getPropertyPath().toString();
                    return Violation.In.unknown
                            .field(field)
                            .message(constraintViolation.getMessage());
                });

    }

    private Optional<Parameter> matchEndpointMethodParameter(ConstraintViolation<?> violation) {
        if (resourceInfo == null) {
            return Optional.empty();
        }

        Iterator<Path.Node> propertyPathIterator = violation.getPropertyPath().iterator();
        if (!propertyPathIterator.hasNext()) {
            return Optional.empty();
        }
        propertyPathIterator.next();
        if (!propertyPathIterator.hasNext()) {
            return Optional.empty();
        }
        String paramName = propertyPathIterator.next().getName();

        return Optional.ofNullable(resourceInfo.getResourceMethod())
                .flatMap(method -> Stream.of(method.getParameters())
                        .filter(param -> param.getName().equals(paramName))
                        .findFirst());
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

    private boolean isDeclarativeValidation(ConstraintViolation<?> violation) {
        return hasJaxRsContext() &&
                (rootBeanResourceClassMatches(violation) || propertyPathStartsWithMethod(violation));
    }

    private boolean hasJaxRsContext() {
        return resourceInfo != null;
    }

    private boolean rootBeanResourceClassMatches(ConstraintViolation<?> violation) {
        Object rootBean = violation.getRootBean();
        return rootBean != null && resourceInfo.getResourceClass().isInstance(rootBean);
    }

    private boolean propertyPathStartsWithMethod(ConstraintViolation<?> violation) {
        String propertyPath = violation.getPropertyPath().toString();
        Method method = resourceInfo.getResourceMethod();
        return method != null && propertyPath.startsWith(method.getName() + ".");
    }

}
