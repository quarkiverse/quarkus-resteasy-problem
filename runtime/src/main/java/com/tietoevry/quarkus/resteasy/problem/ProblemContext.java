package com.tietoevry.quarkus.resteasy.problem;

import javax.ws.rs.core.UriInfo;

/**
 * Context info wrapper for everything ProblemProcessor implementations may need to do their job.
 * It can be easily extended without changing ProblemProcessor interface.
 *
 * @see ProblemProcessor
 */
class ProblemContext {

    final Throwable cause;
    final UriInfo uriInfo;

    public ProblemContext(Throwable cause, UriInfo uriInfo) {
        this.cause = cause;
        this.uriInfo = uriInfo;
    }

}
