package com.tietoevry.quarkus.resteasy.problem.postprocessing;

import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Replaces <code>null</code> value of <code>instance</code> with URI of currently served endpoint, i.e
 * <code>/products/123</code>
 */
final class ProblemDefaultsProvider implements ProblemPostProcessor {

    @Override
    public int priority() {
        return 99;
    }

    @Override
    public HttpProblem apply(HttpProblem problem, ProblemContext context) {
        if (problem.getInstance() != null) {
            return problem;
        }

        return HttpProblem.builder(problem)
                .withInstance(defaultInstance(context))
                .build();
    }

    private URI defaultInstance(ProblemContext context) {
        if (context.path == null) {
            return null;
        }
        try {
            return new URI(context.path.replaceAll(" ", "%20"));
        } catch (URISyntaxException e) {
            return null;
        }
    }

}
