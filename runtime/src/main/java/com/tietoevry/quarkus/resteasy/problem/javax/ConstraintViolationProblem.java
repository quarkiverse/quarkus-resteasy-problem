package com.tietoevry.quarkus.resteasy.problem.javax;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Path;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.StatusType;

class ConstraintViolationProblem implements Problem {

    private final Set<ConstraintViolation<?>> constraintViolations;

    public ConstraintViolationProblem(Set<ConstraintViolation<?>> constraintViolations) {
        this.constraintViolations = constraintViolations;
    }

    @Override
    public String getTitle() {
        return Status.BAD_REQUEST.getReasonPhrase();
    }

    @Override
    public StatusType getStatus() {
        return Status.BAD_REQUEST;
    }

    @Override
    public Map<String, Object> getParameters() {
        List<Violation> violations = constraintViolations
                .stream()
                .map(this::toViolation)
                .collect(Collectors.toList());
        return Collections.singletonMap("violations", violations);
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
