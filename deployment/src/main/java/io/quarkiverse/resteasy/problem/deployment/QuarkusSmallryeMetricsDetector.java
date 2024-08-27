package io.quarkiverse.resteasy.problem.deployment;

final class QuarkusSmallryeMetricsDetector extends ClasspathDetector {

    public QuarkusSmallryeMetricsDetector() {
        super("io.quarkus.smallrye.metrics.runtime.SmallRyeMetricsRecorder");
    }

}