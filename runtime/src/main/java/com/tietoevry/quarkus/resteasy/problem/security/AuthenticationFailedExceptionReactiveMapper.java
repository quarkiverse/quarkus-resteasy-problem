package com.tietoevry.quarkus.resteasy.problem.security;

import javax.ws.rs.Priorities;
import javax.ws.rs.core.Response;

import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import com.tietoevry.quarkus.resteasy.problem.postprocessing.ProblemContext;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import com.tietoevry.quarkus.resteasy.problem.HttpProblem;

import io.quarkus.security.AuthenticationFailedException;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;

/**
 * Mapper overriding default Quarkus exception mapper to make all error responses compliant with RFC7807.
 */
final class AuthenticationFailedExceptionReactiveMapper {

    @ServerExceptionMapper(value = AuthenticationFailedException.class, priority = Priorities.USER - 1)
    public Uni<Response> handle(RoutingContext routingContext, AuthenticationFailedException exception) {
        return AuthChallengeExtractor.toProblem(routingContext, exception)
                .map(problem -> {
                    ProblemContext context = ProblemContext.of(exception, routingContext.normalisedPath());
                    HttpProblem finalProblem = ExceptionMapperBase.postProcessorsRegistry.applyPostProcessing(problem,
                            context);
                    return finalProblem.toResponse();
                });
    }
    
}
