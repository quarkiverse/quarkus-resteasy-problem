package com.tietoevry.quarkus.resteasy.problem.security;

import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;

import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import io.quarkus.security.AuthenticationCompletionException;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;

/**
 * Mapper overriding default Quarkus exception mapper to make all error responses compliant with RFC7807.<br>
 * <br>
 * From AuthenticationCompletionException javadocs on WWW-Authenticate header:
 *
 * <pre>
 * Exception indicating that a user authentication flow has failed and no challenge is required.
 * </pre>
 *
 * <br>
 *
 * @see io.quarkus.resteasy.runtime.AuthenticationCompletionExceptionMapper
 */
@Priority(Priorities.USER - 1)
public final class AuthenticationCompletionExceptionMapper extends ExceptionMapperBase<AuthenticationCompletionException> {

    @Override
    protected HttpProblem toProblem(AuthenticationCompletionException exception) {
        return HttpProblem.valueOf(UNAUTHORIZED, exception.getMessage());
    }

}
