package io.quarkiverse.resteasy.problem.postprocessing;

import io.quarkiverse.resteasy.problem.HttpProblem;
import io.smallrye.metrics.MetricRegistries;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Tag;

/**
 * Provides simple metrics to Microprofile Metrics Registry. Example result:
 *
 * <pre>
 * application_http_error_total{status="401"} 3.0
 * application_http_error_total{status="500"} 5.0
 * </pre>
 */
final class MicroprofileMetricsCollector implements ProblemPostProcessor {

    private static final String METRIC_NAME = "http.error";
    private static final String METRIC_DESCRIPTION = "The number of http errors returned since the start of the server.";
    private static final String STATUS_TAG = "status";

    private final MetricRegistry registry = MetricRegistries.get(MetricRegistry.Type.APPLICATION);

    public MicroprofileMetricsCollector() {
        initTagMetadata();
    }

    private void initTagMetadata() {
        if (!registry.getMetadata().containsKey(METRIC_NAME)) {
            Metadata metadata = Metadata.builder()
                    .withName(METRIC_NAME)
                    .withDescription(METRIC_DESCRIPTION)
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
    public HttpProblem apply(HttpProblem problem, ProblemContext context) {
        Tag tag = new Tag(STATUS_TAG, String.valueOf(problem.getStatusCode()));
        registry.counter(METRIC_NAME, tag).inc();

        return problem;
    }

}
