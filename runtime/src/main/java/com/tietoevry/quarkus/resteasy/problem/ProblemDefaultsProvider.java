package com.tietoevry.quarkus.resteasy.problem;

import java.net.URI;
import java.util.Locale;
import java.util.Optional;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;
import org.zalando.problem.StatusType;

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
        return URI.create(typePrefix
                + statusCode.getReasonPhrase()
                        .replaceAll(" ", "-")
                        .replaceAll("[^a-zA-Z0-9-]", "")
                        .toLowerCase(Locale.ENGLISH));
    }

}
