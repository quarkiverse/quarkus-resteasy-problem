package com.tietoevry.quarkus.resteasy.problem.deployment;

import java.util.function.BooleanSupplier;

final class JsonBDetector implements BooleanSupplier {

    @Override
    public boolean getAsBoolean() {
        try {
            Class.forName("io.quarkus.resteasy.jsonb.vertx.VertxJson");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
