package com.tietoevry.quarkus.resteasy.problem.deployment;

import java.util.function.BooleanSupplier;

final class JacksonDetector implements BooleanSupplier {

    @Override
    public boolean getAsBoolean() {
        try {
            Class.forName("io.quarkus.jackson.ObjectMapperCustomizer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
