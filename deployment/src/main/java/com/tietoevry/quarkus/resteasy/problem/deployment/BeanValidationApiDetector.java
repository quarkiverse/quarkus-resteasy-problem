package com.tietoevry.quarkus.resteasy.problem.deployment;

import java.util.function.BooleanSupplier;

final class BeanValidationApiDetector implements BooleanSupplier {

    @Override
    public boolean getAsBoolean() {
        try {
            Class.forName("javax.validation.ValidationException");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
