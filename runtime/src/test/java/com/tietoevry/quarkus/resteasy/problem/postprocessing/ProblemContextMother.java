package com.tietoevry.quarkus.resteasy.problem.postprocessing;

import org.jboss.resteasy.specimpl.ResteasyUriInfo;

class ProblemContextMother {

    static ProblemContext simpleContext() {
        return withCause(new RuntimeException());
    }

    static ProblemContext withCause(Throwable cause) {
        return ProblemContext.of(cause, new ResteasyUriInfo("http://localhost/endpoint", "endpoint"));
    }
}
