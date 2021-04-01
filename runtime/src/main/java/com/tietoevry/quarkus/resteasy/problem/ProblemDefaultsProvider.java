package com.tietoevry.quarkus.resteasy.problem;

import java.net.URI;
import javax.ws.rs.core.UriInfo;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;

/**
 * Replaces <code>null</code> value of <code>instance</code> with URI of currently served endpoint, i.e
 * <code>/products/123</code>
 */
class ProblemDefaultsProvider implements ProblemPostProcessor {

    @Override
    public int priority() {
        return 99;
    }

    @Override
    public Problem apply(Problem problem, ProblemContext context) {
        if (problem.getInstance() != null) {
            return problem;
        }

        ProblemBuilder builder = ProblemUtils.toBuilder(problem);
        builder.withInstance(defaultInstance(context));
        return builder.build();
    }

    private URI defaultInstance(ProblemContext context) {
        UriInfo uriInfo = context.uriInfo;
        return (uriInfo == null) ? null : URI.create(uriInfo.getPath());
    }

}
