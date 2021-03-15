package com.tietoevry.quarkus.resteasy.problem;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

class JacksonProblemSerializerTest {

    JacksonProblemSerializer jacksonProblemSerializer = new JacksonProblemSerializer();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    JsonGenerator jsonGenerator;

    @BeforeEach
    void setUp() throws IOException {
        jsonGenerator = new JsonFactory()
                .createGenerator(outputStream, JsonEncoding.UTF8);
    }

    @Test
    @DisplayName("Should serialize all provided fields")
    void shouldSerializeAllFields() throws IOException {
        Problem problem = Problem.builder()
                .withType(URI.create("URI:goeshere"))
                .withStatus(Status.BAD_REQUEST)
                .withTitle("Something wrong in the dirt")
                .withDetail("Deep down wrongness, zażółć gęślą jaźń for Håkensth")
                .with("custom_field_1", "too long")
                .with("custom_field_2", "too short")
                .build();

        jacksonProblemSerializer.serialize(problem, jsonGenerator, null);

        jsonGenerator.close();
        assertThat(outputStream.toString(StandardCharsets.UTF_8.name()))
                .isEqualTo(
                        "{\"type\":\"URI:goeshere\",\"status\":400,\"title\":\"Something wrong in the dirt\",\"detail\":\"Deep down wrongness, zażółć gęślą jaźń for Håkensth\",\"custom_field_1\":\"too long\",\"custom_field_2\":\"too short\"}");
    }

    @Test
    @DisplayName("Should serialize only not null fields")
    void shouldSerializeOnlyNotNullFields() throws IOException {
        Problem problem = Problem.builder()
                .withStatus(Status.BAD_REQUEST)
                .withTitle("Something wrong in the dirt")
                .build();

        jacksonProblemSerializer.serialize(problem, jsonGenerator, null);

        jsonGenerator.close();
        assertThat(outputStream.toString(StandardCharsets.UTF_8.name()))
                .isEqualTo("{\"status\":400,\"title\":\"Something wrong in the dirt\"}");
    }

}
