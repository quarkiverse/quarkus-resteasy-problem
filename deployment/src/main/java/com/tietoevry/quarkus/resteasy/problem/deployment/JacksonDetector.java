package com.tietoevry.quarkus.resteasy.problem.deployment;

final class JacksonDetector extends ClasspathDetector {

    public JacksonDetector() {
        super("io.quarkus.jackson.ObjectMapperCustomizer");
    }

}
