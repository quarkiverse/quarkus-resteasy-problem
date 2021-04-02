package com.tietoevry.quarkus.resteasy.problem.postprocessing;

import io.smallrye.metrics.MetricRegistries;
import java.util.Objects;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Tag;
import org.zalando.problem.Problem;

/**
 * Provides simple metrics to Microprofile Metrics Registry. Example result:
 *
 * <pre>
 * application_http_error_total{status="401"} 3.0
 * application_http_error_total{status="500"} 5.0
 * </pre>
 */
class MicroprofileMetricsCollector implements ProblemPostProcessor {

    private static final String METRIC_NAME = "http.error";
    private static final String STATUS_TAG = "status";

    private final MetricRegistry registry = MetricRegistries.get(MetricRegistry.Type.APPLICATION);

    public MicroprofileMetricsCollector() {
        if (!registry.getMetadata().containsKey(METRIC_NAME)) {
            Metadata metadata = Metadata.builder()
                    .withName(METRIC_NAME)
                    .withDescription("The number of http errors returned since the start of the server.")
                    .build();
            Tag tag = new Tag(STATUS_TAG, "500");
            registry.counter(metadata, tag);
        }
    }

    @Override
    public int priority() {
        return 100;
    }

    @Override
    public Problem apply(Problem problem, ProblemContext context) {
        Objects.requireNonNull(problem.getStatus());

        Tag tag = new Tag(STATUS_TAG, String.valueOf(problem.getStatus().getStatusCode()));
        registry.counter(METRIC_NAME, tag).inc();

        return problem;
    }

}
