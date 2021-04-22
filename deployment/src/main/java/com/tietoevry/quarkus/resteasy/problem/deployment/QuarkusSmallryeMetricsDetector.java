package com.tietoevry.quarkus.resteasy.problem.deployment;

final class QuarkusSmallryeMetricsDetector extends ClasspathDetector {

    public QuarkusSmallryeMetricsDetector() {
        super("io.quarkus.smallrye.metrics.runtime.SmallRyeMetricsRecorder");
    }

}