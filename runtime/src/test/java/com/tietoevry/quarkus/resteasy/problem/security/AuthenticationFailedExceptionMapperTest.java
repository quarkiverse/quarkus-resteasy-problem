package com.tietoevry.quarkus.resteasy.problem.security;

import static com.tietoevry.quarkus.resteasy.problem.ExceptionMapperAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import io.quarkus.security.AuthenticationFailedException;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

class AuthenticationFailedExceptionMapperTest {

    AuthenticationFailedExceptionMapper mapper = new AuthenticationFailedExceptionMapper();

    @Test
    void shouldHaveHigherPriorityThanBuiltInMapper() {
        assertThat(AuthenticationFailedExceptionMapper.class)
                .hasPrecedenceOver(io.quarkus.resteasy.runtime.AuthenticationFailedExceptionMapper.class);
    }

    @Test
    void shouldProduceHttp401() {
        AuthenticationFailedException exception = new AuthenticationFailedException();

        Response response = mapper.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(401);
    }
}
