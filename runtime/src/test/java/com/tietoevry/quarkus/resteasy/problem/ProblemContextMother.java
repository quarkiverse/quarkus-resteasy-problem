package com.tietoevry.quarkus.resteasy.problem;

import org.jboss.resteasy.specimpl.ResteasyUriInfo;

public class ProblemContextMother {

    static ProblemContext simpleContext() {
        return withCause(new RuntimeException());
    }

    public static ProblemContext withCause(Throwable cause) {
        return new ProblemContext(cause, new ResteasyUriInfo("http://localhost/endpoint", "endpoint"));
    }
}
