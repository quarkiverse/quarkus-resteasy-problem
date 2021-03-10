package com.tietoevry.quarkus.resteasy.problem.deployment;

import java.util.function.BooleanSupplier;

final class JaxBDetector implements BooleanSupplier {

    @Override
    public boolean getAsBoolean() {
        try {
            Class.forName("io.quarkus.jaxb.runtime.graal.JAXBSubstitutions");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
