package com.tietoevry.quarkus.resteasy.problem;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.zalando.problem.Problem;

/**
 * Container for prioritised list of Problem post-processors.
 */
class PostProcessorsRegistry {

    private final List<ProblemProcessor> processors = new ArrayList<>();

    public PostProcessorsRegistry() {
        reset();
    }

    synchronized void reset() {
        processors.clear();
        register(new LoggingProcessor(LoggerFactory.getLogger("http-problem")));
        register(new ProblemDefaultsProvider());
    }

    synchronized void register(ProblemProcessor processor) {
        processors.add(processor);
        processors.sort(ProblemProcessor.DEFAULT_ORDERING);
    }

    /**
     * Applies all registered post-processors on given Problem, in prioritized order.
     *
     * @param problem Original Problem produced by Exception Mapper
     * @param context Additional info on cause (original exception caught by ExceptionMapper) and HTTP request
     * @return Enhanced version of original Problem
     */
    public Problem applyPostProcessing(Problem problem, ProblemContext context) {
        for (ProblemProcessor processor : processors) {
            problem = processor.apply(problem, context);
        }
        return problem;
    }

}
