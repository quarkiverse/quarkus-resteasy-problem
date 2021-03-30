package com.tietoevry.quarkus.resteasy.problem;

import io.smallrye.metrics.MetricRegistries;
import java.util.Optional;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Tag;
import org.zalando.problem.Problem;
import org.zalando.problem.StatusType;

class HttpErrorMetricsProcessor implements ProblemProcessor {

    private static final String METRIC_NAME = "http.error";

    @Override
    public int priority() {
        return 100;
    }

    @Override
    public Problem apply(Problem problem, ProblemContext context) {
        int statusCode = Optional.ofNullable(problem.getStatus())
                .map(StatusType::getStatusCode)
                .orElse(500);

        Tag tag = new Tag("status", String.valueOf(statusCode));

        MetricRegistry registry = MetricRegistries.get(MetricRegistry.Type.APPLICATION);
        if (!registry.getMetadata().containsKey(METRIC_NAME)) {
            Metadata metadata = Metadata.builder()
                    .withName(METRIC_NAME)
                    .withDescription("The number of http errors returned since the start of the server.")
                    .build();
            registry.counter(metadata, tag);
        }
        registry.counter(METRIC_NAME, tag).inc();
        return problem;
    }

}
