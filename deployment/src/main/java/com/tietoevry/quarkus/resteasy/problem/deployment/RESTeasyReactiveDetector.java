package com.tietoevry.quarkus.resteasy.problem.deployment;

import java.util.function.BooleanSupplier;

final class RESTeasyReactiveDetector implements BooleanSupplier {

    @Override
    public boolean getAsBoolean() {
        try {
            Class.forName("io.quarkus.resteasy.reactive.server.runtime.QuarkusContextProducers");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
