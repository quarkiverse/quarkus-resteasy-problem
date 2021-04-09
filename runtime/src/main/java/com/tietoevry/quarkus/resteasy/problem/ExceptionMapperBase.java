package com.tietoevry.quarkus.resteasy.problem;

import com.tietoevry.quarkus.resteasy.problem.postprocessing.PostProcessorsRegistry;
import com.tietoevry.quarkus.resteasy.problem.postprocessing.ProblemContext;
import java.util.Objects;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Base class for all ExceptionMappers in this extension, takes care of mapping Exceptions to Problems, triggering
 * post-processing stage, and creating final JaxRS Response.
 */
public abstract class ExceptionMapperBase<E extends Throwable> implements ExceptionMapper<E> {

    public static final PostProcessorsRegistry postProcessorsRegistry = new PostProcessorsRegistry();

    @Context
    UriInfo uriInfo;

    @Override
    public final Response toResponse(E exception) {
        HttpProblem problem = toProblem(exception);
        Objects.requireNonNull(problem.getStatus(), "Status must not be null");

        ProblemContext context = ProblemContext.of(exception, uriInfo);
        HttpProblem finalProblem = postProcessorsRegistry.applyPostProcessing(problem, context);
        return toFinalResponse(finalProblem);
    }

    protected abstract HttpProblem toProblem(E exception);

    private Response toFinalResponse(HttpProblem problem) {
        Objects.requireNonNull(problem.getStatus());

        Response.ResponseBuilder builder = Response
                .status(problem.getStatus().getStatusCode())
                .type(HttpProblem.MEDIA_TYPE)
                .entity(problem);

        problem.getHeaders().forEach(builder::header);

        return builder.build();
    }

}
