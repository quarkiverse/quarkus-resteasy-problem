package com.tietoevry.quarkus.resteasy.problem.deployment;

import java.util.function.BooleanSupplier;

final class RestEasyClassicDetector implements BooleanSupplier {

    @Override
    public boolean getAsBoolean() {
        try {
            Class.forName("io.quarkus.resteasy.common.runtime.ResteasyContextProvider");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
