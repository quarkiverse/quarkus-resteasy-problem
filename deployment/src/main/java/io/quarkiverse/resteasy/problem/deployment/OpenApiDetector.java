package io.quarkiverse.resteasy.problem.deployment;

final class OpenApiDetector extends ClasspathDetector {

    OpenApiDetector() {
        super("io.quarkus.smallrye.openapi.OpenApiFilter");
    }

}
