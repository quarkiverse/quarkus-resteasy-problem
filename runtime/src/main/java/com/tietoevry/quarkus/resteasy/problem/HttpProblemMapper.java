package com.tietoevry.quarkus.resteasy.problem;

import static com.tietoevry.quarkus.resteasy.problem.ProblemUtils.APPLICATION_PROBLEM_JSON;

import com.tietoevry.quarkus.resteasy.problem.postprocessing.ProblemContext;
import java.util.Objects;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

@Priority(Priorities.USER)
public final class HttpProblemMapper implements ExceptionMapper<HttpProblem> {

    @Context
    UriInfo uriInfo;

    @Override
    public final Response toResponse(HttpProblem exception) {
        HttpProblem problem = toProblem(exception);
        ProblemContext context = ProblemContext.of(exception, uriInfo);
        Problem finalProblem = ExceptionMapperBase.postProcessorsRegistry.applyPostProcessing(problem, context);
        return toResponse(finalProblem, exception);
    }

    private HttpProblem toProblem(HttpProblem exception) {
        if (exception.getStatus() != null) {
            return exception;
        }
        return HttpProblem.builder(exception)
                .withStatus(Status.INTERNAL_SERVER_ERROR)
                .build();
    }

    private Response toResponse(Problem problem, HttpProblem originalException) {
        Objects.requireNonNull(problem.getStatus());

        Response.ResponseBuilder responseBuilder = Response
                .status(problem.getStatus().getStatusCode())
                .type(APPLICATION_PROBLEM_JSON)
                .entity(problem);

        originalException.getHeaders().forEach(responseBuilder::header);

        return responseBuilder.build();
    }
}
