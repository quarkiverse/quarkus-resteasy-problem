package com.tietoevry.quarkus.resteasy.problem;

import org.zalando.problem.Problem;

import java.util.function.BiFunction;

interface ProblemProcessor extends BiFunction<Problem, Throwable, Problem> {

    /**
     * Defines order in which processors are triggered. Bigger value means precedence before processors
     * with lower priority.
     */
    default int priority() {
        return Integer.MIN_VALUE;
    }
}
