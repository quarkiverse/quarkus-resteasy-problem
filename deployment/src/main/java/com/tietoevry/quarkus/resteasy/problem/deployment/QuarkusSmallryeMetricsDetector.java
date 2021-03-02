package com.tietoevry.quarkus.resteasy.problem.deployment;

import java.util.function.BooleanSupplier;

final class QuarkusSmallryeMetricsDetector implements BooleanSupplier {

    @Override
    public boolean getAsBoolean() {
        try {
            Class.forName("io.quarkus.smallrye.metrics.runtime.SmallRyeMetricsRecorder");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
