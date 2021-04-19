package com.tietoevry.quarkus.resteasy.problem.security;

import static com.tietoevry.quarkus.resteasy.problem.ExceptionMapperAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import io.quarkus.security.ForbiddenException;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

class ForbiddenExceptionMapperTest {

    ForbiddenExceptionMapper mapper = new ForbiddenExceptionMapper();

    @Test
    void shouldHaveHigherPriorityThanBuiltInMapper() {
        assertThat(mapper.getClass())
                .hasPrecedenceOver(io.quarkus.resteasy.runtime.ForbiddenExceptionMapper.class);
    }

    @Test
    void shouldProduceHttp403() {
        ForbiddenException exception = new ForbiddenException();

        Response response = mapper.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(403);
    }

}
