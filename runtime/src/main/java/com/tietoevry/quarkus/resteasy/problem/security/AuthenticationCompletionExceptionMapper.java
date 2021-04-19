package com.tietoevry.quarkus.resteasy.problem.security;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

import com.tietoevry.quarkus.resteasy.problem.ExceptionMapperBase;
import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import io.quarkus.security.AuthenticationCompletionException;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;

/**
 * Mapper overriding default Quarkus exception mapper to make all error responses compliant with RFC7807.
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
