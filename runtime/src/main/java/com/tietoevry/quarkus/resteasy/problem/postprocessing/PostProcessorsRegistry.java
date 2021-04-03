package com.tietoevry.quarkus.resteasy.problem.postprocessing;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.zalando.problem.Problem;

/**
 * Container for prioritised list of Problem post-processors.
 */
public final class PostProcessorsRegistry {

    private final List<ProblemPostProcessor> processors = new ArrayList<>();

    public PostProcessorsRegistry() {
        reset();
    }

    /**
     * Removes all registered post-processors and registers default ones. Used mainly for Quarkus dev mode (live-reload) tests
     * where there's a need to reset registered processors because of config change.
     */
    synchronized void reset() {
        processors.clear();
        register(new ProblemLogger(LoggerFactory.getLogger("http-problem")));
        register(new ProblemDefaultsProvider());
    }

    synchronized void register(ProblemPostProcessor processor) {
        processors.add(processor);
        processors.sort(ProblemPostProcessor.DEFAULT_ORDERING);
    }

    /**
     * Applies all registered post-processors on a given Problem, in prioritized order.
     *
     * @param problem Original Problem produced by Exception Mapper
     * @param context Additional info on cause (original exception caught by ExceptionMapper) and HTTP request
     * @return Enhanced version of original Problem
     */
    public Problem applyPostProcessing(Problem problem, ProblemContext context) {
        Problem finalProblem = problem;
        for (ProblemPostProcessor processor : processors) {
            finalProblem = processor.apply(finalProblem, context);
        }
        return finalProblem;
    }

}
