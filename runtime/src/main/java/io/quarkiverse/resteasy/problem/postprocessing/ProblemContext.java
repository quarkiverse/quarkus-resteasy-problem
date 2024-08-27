package io.quarkiverse.resteasy.problem.postprocessing;

import jakarta.ws.rs.core.UriInfo;

/**
 * Context wrapper for everything ProblemPostProcessor implementations may need to do their job.
 * It can be easily extended without changing ProblemProcessor interface.
 *
 * @see ProblemPostProcessor
 */
public final class ProblemContext {

    /**
     * * Original exception caught by ExceptionMapper.
     */
    public final Throwable cause;

    /**
     * URI path of the endpoint.
     */
    public final String path;

    private ProblemContext(Throwable cause, String path) {
        this.cause = cause;
        this.path = path;
    }

    public static ProblemContext of(Throwable exception, UriInfo uriInfo) {
        try {
            return new ProblemContext(exception, (uriInfo == null) ? null : uriInfo.getPath());
        } catch (Exception e) { // quarkus-reactive throws ContextNotActiveException or NullPointerException when json request payload is malformed
            return new ProblemContext(exception, null);
        }
    }

    public static ProblemContext of(Throwable exception, String path) {
        return new ProblemContext(exception, path);
    }
}
