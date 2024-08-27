package io.quarkiverse.resteasy.problem.security;

import static io.quarkiverse.resteasy.problem.ExceptionMapperAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import io.quarkus.security.AuthenticationRedirectException;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

class AuthenticationRedirectExceptionMapperTest {

    AuthenticationRedirectExceptionMapper mapper = new AuthenticationRedirectExceptionMapper();

    @Test
    void shouldHaveHigherPriorityThanBuiltInMapper() {
        assertThat(mapper.getClass())
                .hasPrecedenceOver(io.quarkus.resteasy.runtime.AuthenticationRedirectExceptionMapper.class);
    }

    @Test
    void shouldProduceHttp302WithAllNeededHeaders() {
        AuthenticationRedirectException exception = new AuthenticationRedirectException("/login");

        Response response = mapper.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(302);
        assertThat(response.getHeaderString(HttpHeaders.LOCATION)).isEqualTo("/login");
        assertThat(response.getHeaderString(HttpHeaders.CACHE_CONTROL)).isEqualTo("no-store");
        assertThat(response.getHeaderString("Pragma")).isEqualTo("no-cache");
    }
}
