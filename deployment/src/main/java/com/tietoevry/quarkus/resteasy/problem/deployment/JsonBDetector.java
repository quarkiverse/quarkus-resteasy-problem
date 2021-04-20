package com.tietoevry.quarkus.resteasy.problem.deployment;

final class JsonBDetector extends ClasspathDetector {

    public JsonBDetector() {
        super("io.quarkus.jsonb.JsonbProducer");
    }

}