package com.tietoevry.quarkus.resteasy.problem.postprocessing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.slf4j.LoggerFactory;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;

/**
 * Container for prioritised list of Problem post-processors.
 */
public class PostProcessorsRegistry {

    private final List<ProblemPostProcessor> processors = new ArrayList<>();

    public PostProcessorsRegistry() {
        reset();
    }

    synchronized void reset() {
        processors.clear();
        register(new ProblemLogger(LoggerFactory.getLogger("http-problem")));
    }

    synchronized void register(ProblemPostProcessor processor) {
        processors.add(processor);
        processors.sort(Comparator.comparingInt(ProblemPostProcessor::priority).reversed());
    }

    /**
     * Applies all registered post-processors on given Problem, in prioritized order.
     *
     * @param problem Original Problem produced by Exception Mapper
     * @param context Additional info on cause (original exception caught by ExceptionMapper) and HTTP request
     * @return Enhanced version of original Problem
     */
    public Problem applyPostProcessing(Problem problem, ProblemContext context) {
        ProblemBuilder builder = mutableCopyOf(problem);
        for (ProblemPostProcessor processor : processors) {
            builder = processor.apply(builder, context);
        }
        return builder.build();
    }

    private ProblemBuilder mutableCopyOf(Problem problem) {
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
