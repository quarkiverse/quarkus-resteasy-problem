package com.tietoevry.quarkus.resteasy.problem.deployment;

import java.util.function.BooleanSupplier;

final class ZalandoProblemDetector implements BooleanSupplier {

    @Override
    public boolean getAsBoolean() {
        try {
            Class.forName("org.zalando.problem.Problem");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
