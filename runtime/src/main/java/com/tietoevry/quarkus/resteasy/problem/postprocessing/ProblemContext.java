package com.tietoevry.quarkus.resteasy.problem.postprocessing;

import javax.ws.rs.core.UriInfo;

/**
 * Context wrapper for everything ProblemPostProcessor implementations may need to do their job.
 * It can be easily extended without changing ProblemProcessor interface.
 *
 * @see ProblemPostProcessor
 */
public class ProblemContext {

    /**
     * * Original exception caught by ExceptionMapper.
     */
    public final Throwable cause;

    /**
     * UriInfo for currently handled HTTP request.
     */
    public final UriInfo uriInfo;

    public ProblemContext(Throwable cause, UriInfo uriInfo) {
        this.cause = cause;
        this.uriInfo = uriInfo;
    }

}
