package io.quarkiverse.resteasy.problem.security;

import static io.quarkiverse.resteasy.problem.ExceptionMapperAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.vertx.http.runtime.CurrentVertxRequest;

class AuthenticationFailedExceptionMapperTest {

    AuthenticationFailedExceptionMapper mapper = new AuthenticationFailedExceptionMapper();

    @BeforeEach
    void setup() {
        mapper.currentVertxRequest = mock(CurrentVertxRequest.class);
    }

    @Test
    void shouldHaveHigherPriorityThanBuiltInMapper() {
        assertThat(mapper.getClass())
                .hasPrecedenceOver(io.quarkus.resteasy.runtime.AuthenticationFailedExceptionMapper.class);
    }

    @Test
    void shouldProduceHttp401() {
        AuthenticationFailedException exception = new AuthenticationFailedException();

        Response response = mapper.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(401);
    }
}
