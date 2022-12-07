package com.tietoevry.quarkus.resteasy.problem.jaxrs;

import static com.tietoevry.quarkus.resteasy.problem.ExceptionMapperAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

class NotFoundExceptionMapperTest {

    NotFoundExceptionMapper mapper = new NotFoundExceptionMapper();

    @Test
    void shouldHaveHigherPriorityThanBuiltInMapper() {
        assertThat(mapper.getClass())
                .hasPrecedenceOver(io.quarkus.resteasy.runtime.NotFoundExceptionMapper.class);
    }

    @Test
    void shouldProduceHttp404() {
        NotFoundException exception = new NotFoundException();

        Response response = mapper.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(404);
    }

}
