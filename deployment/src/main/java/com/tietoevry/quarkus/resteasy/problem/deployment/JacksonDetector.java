package com.tietoevry.quarkus.resteasy.problem.deployment;

final class JacksonDetector extends ClasspathDetector {

    JacksonDetector() {
        super("io.quarkus.jackson.ObjectMapperCustomizer");
    }

}
