package com.tietoevry.quarkus.resteasy.problem.jackson;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

class InvalidFormatExceptionMapperTest {

    InvalidFormatExceptionMapper mapper = new InvalidFormatExceptionMapper();

    @Test
    void shouldProduceHttp400WithFieldInfo() {
        InvalidFormatException exception = new InvalidFormatException(mock(JsonParser.class),
                "Invalid format of the field", this, this.getClass());
        exception.prependPath(new JsonMappingException.Reference(this, "customFieldName"));

        Response response = mapper.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getMediaType()).isEqualTo(HttpProblem.MEDIA_TYPE);
        assertThat(response.getEntity())
                .isInstanceOf(HttpProblem.class)
                .hasFieldOrPropertyWithValue("detail", "Invalid format of the field")
                .hasFieldOrPropertyWithValue("parameters.field", "customFieldName");
    }

}
