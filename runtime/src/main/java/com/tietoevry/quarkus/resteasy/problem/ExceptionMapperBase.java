package com.tietoevry.quarkus.resteasy.problem;

import java.util.Optional;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import org.apiguardian.api.API;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.StatusType;

/**
 * Base class for all ExceptionMappers in this extension, takes care of mapping Exceptions to Problems, triggering
 * post-processing stage, and creating final JaxRS Response.
 */
public abstract class ExceptionMapperBase<E extends Throwable> implements ExceptionMapper<E> {

    public static final MediaType APPLICATION_PROBLEM_JSON = new MediaType("application", "problem+json");
    public static final PostProcessorsRegistry postProcessorsRegistry = new PostProcessorsRegistry();

    @Context
    UriInfo uriInfo;

    @Override
    public final Response toResponse(E exception) {
        Problem problem = toProblem(exception);
        ProblemContext context = new ProblemContext(exception, uriInfo);
        Problem finalProblem = postProcessorsRegistry.applyPostProcessing(problem, context);
        return toResponse(finalProblem, exception);
    }

    protected abstract Problem toProblem(E exception);

    /**
     * This is an internal API. It may be changed or removed without notice in any release.
     */
    @API(status = API.Status.INTERNAL)
    protected Response toResponse(Problem problem, E originalException) {
        StatusType status = Optional.ofNullable(problem.getStatus())
                .orElse(Status.INTERNAL_SERVER_ERROR);

        return Response
                .status(status.getStatusCode())
                .type(APPLICATION_PROBLEM_JSON)
                .entity(problem)
                .build();
    }

}
