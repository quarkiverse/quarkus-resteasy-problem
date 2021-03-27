package com.tietoevry.quarkus.resteasy.problem;

import java.net.URI;
import java.util.Locale;
import java.util.Optional;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;
import org.zalando.problem.StatusType;

/**
 * Replaces null/default values of <i>type</i> and <i>instnace</i> with sensible defaults:<br/>
 * <ul>
 * <li><i>type</i> - URI made out of http status phrase in kebab-case, e.g `/bad-request` for HTTP 400</li>
 * <li><i>instance</i> - URI of currently served endpoint, i.e `/products/123`</li>
 * </ul>
 */
class ProblemDefaultsProvider implements ProblemProcessor {

    private static final URI DEFAULT_URI = URI.create("about:blank");

    private final String typePrefix;

    public ProblemDefaultsProvider(String typePrefix) {
        this.typePrefix = typePrefix;
    }

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

        if (problem.getType() == null || DEFAULT_URI.equals(problem.getType())) {
            builder.withType(statusURI(status));
        }
        if (problem.getInstance() == null) {
            builder.withInstance(URI.create(context.uriInfo.getPath()));
        }
        return builder.build();
    }

    private URI statusURI(StatusType statusCode) {
        return URI.create(typePrefix + kebabCase(statusCode.getReasonPhrase()));
    }

    private String kebabCase(String input) {
        return input
                .replaceAll(" ", "-")
                .replaceAll("[^a-zA-Z0-9-]", "")
                .toLowerCase(Locale.ENGLISH);
    }

}
