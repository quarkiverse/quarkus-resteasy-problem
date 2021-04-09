package com.tietoevry.quarkus.resteasy.problem.postprocessing;

import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import java.net.URI;
import javax.ws.rs.core.UriInfo;

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
        UriInfo uriInfo = context.uriInfo;
        return (uriInfo == null) ? null : URI.create(uriInfo.getPath());
    }

}
