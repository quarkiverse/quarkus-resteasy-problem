package com.tietoevry.quarkus.resteasy.problem.security;

import static org.assertj.core.api.Assertions.assertThat;
import static com.tietoevry.quarkus.resteasy.problem.ExceptionMapperAssert.assertThat;

import io.quarkus.security.UnauthorizedException;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

class UnauthorizedExceptionMapperTest {

    UnauthorizedExceptionMapper mapper = new UnauthorizedExceptionMapper();

    @Test
    void shouldHaveHigherPriorityThanBuiltInMapper() {
        assertThat(UnauthorizedExceptionMapper.class)
                .hasPrecedenceOver(io.quarkus.resteasy.runtime.UnauthorizedExceptionMapper.class);
    }

    @Test
    void shouldProduceHttp401() {
        UnauthorizedException exception = new UnauthorizedException();

        Response response = mapper.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(401);
    }

}
