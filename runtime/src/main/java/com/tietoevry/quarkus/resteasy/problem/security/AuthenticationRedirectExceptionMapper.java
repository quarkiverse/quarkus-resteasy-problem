package com.tietoevry.quarkus.resteasy.problem.security;

import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import io.quarkus.security.AuthenticationRedirectException;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.HttpHeaders;

/**
 * Mapper overriding default Quarkus exception mapper to make all error responses compliant with RFC7807.
 *
 * @see io.quarkus.resteasy.runtime.AuthenticationRedirectExceptionMapper
 */
@Priority(Priorities.USER - 1)
public final class AuthenticationRedirectExceptionMapper extends ExceptionMapperBase<AuthenticationRedirectException> {

    @Override
    protected HttpProblem toProblem(AuthenticationRedirectException exception) {
        return HttpProblem.builder()
                .withStatus(exception.getCode())
                .withHeader(HttpHeaders.LOCATION, exception.getRedirectUri())
                .withHeader(HttpHeaders.CACHE_CONTROL, "no-store")
                .withHeader("Pragma", "no-cache")
                .build();
    }

}
