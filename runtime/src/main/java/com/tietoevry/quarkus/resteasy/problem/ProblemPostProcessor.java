package com.tietoevry.quarkus.resteasy.problem;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.BiFunction;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;

/**
 * Post-processors use, change or enhance Problems created by ExceptionMappers via 'apply' method, before they get passed on to
 * serializers.
 */
public interface ProblemPostProcessor extends BiFunction<Problem, ProblemContext, Problem> {

    Comparator<ProblemPostProcessor> DEFAULT_ORDERING = Comparator.comparingInt(ProblemPostProcessor::priority).reversed();

    /**
     * Defines order in which processors are triggered. Bigger value means precedence before processors
     * with lower priority.
     */
    default int priority() {
        return Integer.MIN_VALUE;
    }

    default ProblemBuilder mutableCopyOf(Problem problem) {
        ProblemBuilder builder = Problem.builder()
                .withType(problem.getType())
                .withInstance(problem.getInstance())
                .withTitle(problem.getTitle())
                .withStatus(Optional.ofNullable(problem.getStatus()).orElse(Status.INTERNAL_SERVER_ERROR))
                .withDetail(problem.getDetail());
        problem.getParameters().forEach(builder::with);
        return builder;
    }

}
