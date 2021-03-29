package com.tietoevry.quarkus.resteasy.problem;

import java.net.URI;
import java.util.Optional;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;
import org.zalando.problem.StatusType;

/**
 * Replaces null/default value of <i>instance</i> with URI of currently served endpoint, i.e `/products/123`
 */
class ProblemDefaultsProvider implements ProblemProcessor {

    @Override
    public int priority() {
        return 99;
    }

    @Override
    public Problem apply(Problem problem, ProblemContext context) {
        StatusType status = Optional.ofNullable(problem.getStatus())
                .orElse(Status.INTERNAL_SERVER_ERROR);

        ProblemBuilder builder = Problem.builder()
                .withType(problem.getType())
                .withInstance(problem.getInstance())
                .withTitle(problem.getTitle())
                .withStatus(status)
                .withDetail(problem.getDetail());

        problem.getParameters().forEach(builder::with);

        if (problem.getInstance() == null && context.uriInfo != null) {
            builder.withInstance(URI.create(context.uriInfo.getPath()));
        }
        return builder.build();
    }

}
