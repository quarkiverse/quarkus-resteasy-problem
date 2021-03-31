package com.tietoevry.quarkus.resteasy.problem;

import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;

public class ProblemUtils {

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
