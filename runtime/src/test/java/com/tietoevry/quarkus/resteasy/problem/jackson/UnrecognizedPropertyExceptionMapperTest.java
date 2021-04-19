package com.tietoevry.quarkus.resteasy.problem.jackson;

import static com.tietoevry.quarkus.resteasy.problem.ExceptionMapperAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import java.util.ArrayList;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

class UnrecognizedPropertyExceptionMapperTest {

    UnrecognizedPropertyExceptionMapper mapper = new UnrecognizedPropertyExceptionMapper();

    @Test
    void shouldHaveHigherPriorityThanBuiltInMapper() {
        assertThat(UnrecognizedPropertyExceptionMapper.class)
                .hasPrecedenceOver(org.jboss.resteasy.plugins.providers.jackson.UnrecognizedPropertyExceptionHandler.class);
    }

    @Test
    void shouldProduceHttp400() {
        UnrecognizedPropertyException exception = UnrecognizedPropertyException.from(mock(JsonParser.class),
                this.getClass(), "unknown_field", new ArrayList<>());

        Response response = mapper.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getMediaType()).isEqualTo(HttpProblem.MEDIA_TYPE);
        assertThat(response.getEntity())
                .isInstanceOf(HttpProblem.class)
                .hasFieldOrPropertyWithValue("detail",
                        "Unrecognized field \"unknown_field\" (class com.tietoevry.quarkus.resteasy.problem.jackson.UnrecognizedPropertyExceptionMapperTest), not marked as ignorable");
    }

}
