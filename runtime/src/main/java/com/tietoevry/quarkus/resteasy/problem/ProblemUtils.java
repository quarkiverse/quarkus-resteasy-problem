package com.tietoevry.quarkus.resteasy.problem;

import javax.ws.rs.core.MediaType;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;

public final class ProblemUtils {

    public static final MediaType APPLICATION_PROBLEM_JSON = new MediaType("application", "problem+json");

    private ProblemUtils() {
    }

    public static ProblemBuilder toBuilder(Problem problem) {
        ProblemBuilder builder = Problem.builder()
                .withType(problem.getType())
                .withInstance(problem.getInstance())
                .withTitle(problem.getTitle())
                .withStatus(problem.getStatus())
                .withDetail(problem.getDetail());
        problem.getParameters().forEach(builder::with);
        return builder;
    }

}
