package com.tietoevry.quarkus.resteasy.problem.deployment;

final class RestEasyClassicDetector extends ClasspathDetector {

    public RestEasyClassicDetector() {
        super("io.quarkus.resteasy.common.runtime.ResteasyContextProvider");
    }

}