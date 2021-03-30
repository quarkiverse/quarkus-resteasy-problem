package com.tietoevry.quarkus.resteasy.problem;

import java.util.Comparator;
import java.util.function.BiFunction;
import org.zalando.problem.Problem;

interface ProblemProcessor extends BiFunction<Problem, ProblemContext, Problem> {

    Comparator<ProblemProcessor> DEFAULT_ORDERING = Comparator.comparingInt(ProblemProcessor::priority).reversed();

    /**
     * Defines order in which processors are triggered. Bigger value means precedence before processors
     * with lower priority.
     */
    default int priority() {
        return Integer.MIN_VALUE;
    }

}
