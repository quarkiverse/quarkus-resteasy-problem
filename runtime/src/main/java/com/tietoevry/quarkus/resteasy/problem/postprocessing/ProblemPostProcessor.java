package com.tietoevry.quarkus.resteasy.problem.postprocessing;

import java.util.function.BiFunction;
import org.zalando.problem.ProblemBuilder;

/**
 * Post-processors use, change or enhance Problems created by ExceptionMappers via 'apply' method, before they get passed on to
 * serializers.
 */
public interface ProblemPostProcessor extends BiFunction<ProblemBuilder, ProblemContext, ProblemBuilder> {

    /**
     * Defines order in which processors are triggered. Bigger value means precedence before processors
     * with lower priority.
     */
    default int priority() {
        return Integer.MIN_VALUE;
    }

}
