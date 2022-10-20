package com.tietoevry.quarkus.resteasy.problem.security;

import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import com.tietoevry.quarkus.resteasy.problem.postprocessing.ProblemContext;
import io.quarkus.security.UnauthorizedException;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

/**
 * Mapper overriding default Quarkus exception mapper to make all error responses compliant with RFC7807.
 *
 * @see io.quarkus.resteasy.runtime.UnauthorizedExceptionMapper
 */
public final class UnauthorizedExceptionReactiveMapper {

    @ServerExceptionMapper(value = UnauthorizedException.class, priority = Priorities.USER - 1)
    public Uni<Response> handle(RoutingContext routingContext, UnauthorizedException exception) {
        return HttpUnauthorizedUtils.toProblem(routingContext, exception)
                .map(problem -> {
                    ProblemContext context = ProblemContext.of(exception, routingContext.normalizedPath());
                    HttpProblem finalProblem = ExceptionMapperBase.postProcessorsRegistry.applyPostProcessing(problem,
                            context);
                    return finalProblem.toResponse();
                });
    }

}
