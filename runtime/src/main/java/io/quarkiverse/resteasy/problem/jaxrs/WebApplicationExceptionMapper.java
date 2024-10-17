package io.quarkiverse.resteasy.problem.jaxrs;

import java.util.Optional;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import io.quarkiverse.resteasy.problem.ExceptionMapperBase;
import io.quarkiverse.resteasy.problem.HttpProblem;

/**
 * Generic exception mapper for JaxRS WebApplicationExceptions - it passes status and message to application/problem response.
 */
@Priority(Priorities.USER)
public final class WebApplicationExceptionMapper extends ExceptionMapperBase<WebApplicationException> {

    @Override
    protected HttpProblem toProblem(WebApplicationException exception) {
        Response.StatusType status = Response.Status.fromStatusCode(exception.getResponse().getStatus());

        HttpProblem.Builder problem = HttpProblem.builder()
                .withTitle(status.getReasonPhrase())
                .withStatus(status)
                .withDetail(exception.getMessage());

        Optional.ofNullable(exception.getResponse().getHeaders())
                .ifPresent(headers -> {
                    headers.forEach((header, values) -> values.forEach(value -> problem.withHeader(header, value)));
                });

        return problem.build();
    }

}
