package com.tietoevry.quarkus.resteasy.problem.jackson;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.tietoevry.quarkus.resteasy.problem.ProblemMother;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

class JacksonProblemSerializerTest {

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    JsonGenerator jsonGenerator;

    JacksonProblemSerializer serializer = new JacksonProblemSerializer();

    @BeforeEach
    void setUp() throws IOException {
        jsonGenerator = new JsonFactory()
                .createGenerator(outputStream, JsonEncoding.UTF8);
    }

    @Test
    @DisplayName("Should serialize all provided fields")
    void shouldSerializeAllFields() throws IOException {
        Problem problem = ProblemMother.complexProblem().build();

        serializer.serialize(problem, jsonGenerator, null);

        jsonGenerator.close();
        assertThat(outputStream.toString(StandardCharsets.UTF_8.name()))
                .isEqualTo(ProblemMother.SERIALIZED_COMPLEX_PROBLEM);
    }

    @Test
    @DisplayName("Should serialize only not null fields")
    void shouldSerializeOnlyNotNullFields() throws IOException {
        Problem problem = Problem.builder()
                .withStatus(Status.BAD_REQUEST)
                .withTitle("Something wrong in the dirt")
                .build();

        serializer.serialize(problem, jsonGenerator, null);

        jsonGenerator.close();
        assertThat(outputStream.toString(StandardCharsets.UTF_8.name()))
                .isEqualTo(ProblemMother.SERIALIZED_BAD_REQUEST_PROBLEM);
    }

}
