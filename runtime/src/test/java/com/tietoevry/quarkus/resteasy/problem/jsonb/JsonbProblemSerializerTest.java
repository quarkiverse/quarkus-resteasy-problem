package com.tietoevry.quarkus.resteasy.problem.jsonb;

import static org.assertj.core.api.Assertions.assertThat;

import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import com.tietoevry.quarkus.resteasy.problem.HttpProblemMother;
import jakarta.json.Json;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.eclipse.parsson.JsonProviderImpl;
import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.SerializationContextImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JsonbProblemSerializerTest {

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    JsonGenerator jsonGenerator = Json.createGenerator(outputStream);
    SerializationContext context = new SerializationContextImpl(new JsonbContext(new JsonbConfig(), new JsonProviderImpl()));

    JsonbProblemSerializer serializer = new JsonbProblemSerializer();

    @Test
    @DisplayName("Should serialize all provided fields")
    void shouldSerializeAllFields() {
        HttpProblem problem = HttpProblemMother.complexProblem().build();

        serializer.serialize(problem, jsonGenerator, context);

        jsonGenerator.close();
        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .isEqualTo(HttpProblemMother.SERIALIZED_COMPLEX_PROBLEM);
    }

    @Test
    @DisplayName("Should serialize only not null fields")
    void shouldSerializeOnlyNotNullFields() {
        HttpProblem problem = HttpProblemMother.badRequestProblem();

        serializer.serialize(problem, jsonGenerator, context);

        jsonGenerator.close();
        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .isEqualTo(HttpProblemMother.SERIALIZED_BAD_REQUEST_PROBLEM);
    }

}
