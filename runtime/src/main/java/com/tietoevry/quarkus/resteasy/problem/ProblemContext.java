package com.tietoevry.quarkus.resteasy.problem;

import javax.ws.rs.core.UriInfo;

class ProblemContext {

    final Throwable cause;
    final UriInfo uriInfo;

    public ProblemContext(Throwable cause, UriInfo uriInfo) {
        this.cause = cause;
        this.uriInfo = uriInfo;
    }

}
