package com.tietoevry.quarkus.resteasy.problem.security;

import static com.tietoevry.quarkus.resteasy.problem.ExceptionMapperAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import io.quarkus.security.UnauthorizedException;
import io.quarkus.vertx.http.runtime.CurrentVertxRequest;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UnauthorizedExceptionMapperTest {

    UnauthorizedExceptionMapper mapper = new UnauthorizedExceptionMapper();

    @BeforeEach
    void setup() {
        mapper.currentVertxRequest = mock(CurrentVertxRequest.class);
    }

    @Test
    void shouldHaveHigherPriorityThanBuiltInMapper() {
        assertThat(mapper.getClass())
                .hasPrecedenceOver(io.quarkus.resteasy.runtime.UnauthorizedExceptionMapper.class);
    }

    @Test
    void shouldProduceHttp401() {
        UnauthorizedException exception = new UnauthorizedException();

        Response response = mapper.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(401);
    }

}
