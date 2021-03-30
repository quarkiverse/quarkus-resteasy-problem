package com.tietoevry.quarkus.resteasy.problem;

import java.net.URI;
import java.util.Optional;
import javax.ws.rs.core.UriInfo;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;
import org.zalando.problem.StatusType;

/**
 * Replaces <code>null</code> values of <code>status</code> with default HTTP500
 * and/or <code>instance</code> with URI of currently served endpoint, i.e <code>/products/123</code>
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
        URI instance = Optional.ofNullable(problem.getInstance())
                .orElseGet(() -> defaultUri(context));

        ProblemBuilder builder = Problem.builder()
                .withType(problem.getType())
                .withInstance(instance)
                .withTitle(problem.getTitle())
                .withStatus(status)
                .withDetail(problem.getDetail());

        problem.getParameters().forEach(builder::with);

        return builder.build();
    }

    private URI defaultUri(ProblemContext context) {
        UriInfo uriInfo = context.uriInfo;
        return (uriInfo == null) ? null : URI.create(uriInfo.getPath());
    }

}
