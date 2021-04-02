package com.tietoevry.quarkus.resteasy.problem.jaxrs;

import static com.tietoevry.quarkus.resteasy.problem.ProblemUtils.APPLICATION_PROBLEM_JSON;

import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import com.tietoevry.quarkus.resteasy.problem.postprocessing.ProblemContext;
import java.util.Objects;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

/**
 * Generic exception mapper for JaxRS WebApplicationExceptions - it passes status and message to application/problem response.
 */
@Priority(Priorities.USER)
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    private static final Logger logger = LoggerFactory.getLogger(WebApplicationExceptionMapper.class);

    @Context
    UriInfo uriInfo;

    @Override
    public final Response toResponse(WebApplicationException exception) {
        Problem problem = toProblem(exception);
        ProblemContext context = ProblemContext.of(exception, uriInfo);
        Problem finalProblem = ExceptionMapperBase.postProcessorsRegistry.applyPostProcessing(problem, context);
        return toResponse(finalProblem, exception);
    }

    private Problem toProblem(WebApplicationException exception) {
        Status status = Status.INTERNAL_SERVER_ERROR;
        try {
            status = Status.valueOf(exception.getResponse().getStatus());
        } catch (IllegalArgumentException e) {
            logger.error("WebApplicationException status code is not a valid HTTP status: {}",
                    exception.getResponse().getStatus());
        }
        return Problem.valueOf(status, exception.getMessage());
    }

    private Response toResponse(Problem problem, WebApplicationException originalException) {
        Objects.requireNonNull(problem.getStatus());

        Response.ResponseBuilder responseBuilder = Response
                .status(problem.getStatus().getStatusCode())
                .type(APPLICATION_PROBLEM_JSON)
                .entity(problem);

        if (originalException.getResponse() != null) {
            originalException
                    .getResponse()
                    .getHeaders()
                    .forEach((header, values) -> values.forEach(value -> responseBuilder.header(header, value)));
        }

        return responseBuilder.build();
    }

}
