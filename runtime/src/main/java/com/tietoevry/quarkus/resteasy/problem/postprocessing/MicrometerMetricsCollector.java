package com.tietoevry.quarkus.resteasy.problem.postprocessing;

import io.smallrye.metrics.MetricRegistries;
import java.util.Objects;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Tag;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;

/**
 * Provides simple metrics to Micrometer Metrics Registry. Example result:
 *
 * <pre>
 * application_http_error_total{status="401"} 3.0
 * application_http_error_total{status="500"} 5.0
 * </pre>
 */
class MicrometerMetricsCollector implements ProblemPostProcessor {

    private static final String METRIC_NAME = "http.error";

    private final MetricRegistry registry = MetricRegistries.get(MetricRegistry.Type.APPLICATION);

    public MicrometerMetricsCollector() {
        if (!registry.getMetadata().containsKey(METRIC_NAME)) {
            Metadata metadata = Metadata.builder()
                    .withName(METRIC_NAME)
                    .withDescription("The number of http errors returned since the start of the server.")
                    .build();
            Tag tag = new Tag("status", "500");
            registry.counter(metadata, tag);
        }
    }

    @Override
    public int priority() {
        return 100;
    }

    @Override
    public ProblemBuilder apply(ProblemBuilder builder, ProblemContext context) {
        Problem problem = builder.build();
        Objects.requireNonNull(problem.getStatus());

        Tag tag = new Tag("status", String.valueOf(problem.getStatus().getStatusCode()));
        registry.counter(METRIC_NAME, tag).inc();

        return builder;
    }

}
