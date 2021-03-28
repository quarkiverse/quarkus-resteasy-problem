package com.tietoevry.quarkus.resteasy.problem.postprocessing;

import java.net.URI;
import java.util.Objects;

import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;

/**
 * Replaces null/default value of <i>instance</i> with URI of currently served endpoint, i.e `/products/123`
 */
class ProblemDefaultsProvider implements ProblemPostProcessor {

    @Override
    public int priority() {
        return 99;
    }

    @Override
    public ProblemBuilder apply(ProblemBuilder builder, ProblemContext context) {
        Problem problem = builder.build();
        Objects.requireNonNull(problem.getStatus());

        if (problem.getInstance() == null) {
            builder.withInstance(URI.create(context.uriInfo.getPath()));
        }
        return builder;
    }

}
