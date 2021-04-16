package com.tietoevry.quarkus.resteasy.problem.jsonb;

import static org.assertj.core.api.Assertions.assertThat;

import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import com.tietoevry.quarkus.resteasy.problem.HttpProblemMother;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.json.Json;
import javax.json.bind.JsonbConfig;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.Marshaller;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JsonbProblemSerializerTest {

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    JsonGenerator jsonGenerator = Json.createGenerator(outputStream);
    SerializationContext context = new Marshaller(new JsonbContext(new JsonbConfig(), null));

    JsonbProblemSerializer serializer = new JsonbProblemSerializer();

    @Test
    @DisplayName("Should serialize all provided fields")
    void shouldSerializeAllFields() throws IOException {
        HttpProblem problem = HttpProblemMother.complexProblem().build();

        serializer.serialize(problem, jsonGenerator, context);

        jsonGenerator.close();
        assertThat(outputStream.toString(StandardCharsets.UTF_8.name()))
                .isEqualTo(HttpProblemMother.SERIALIZED_COMPLEX_PROBLEM);
    }

    @Test
    @DisplayName("Should serialize only not null fields")
    void shouldSerializeOnlyNotNullFields() throws IOException {
        HttpProblem problem = HttpProblemMother.badRequestProblem();

        serializer.serialize(problem, jsonGenerator, context);

        jsonGenerator.close();
        assertThat(outputStream.toString(StandardCharsets.UTF_8.name()))
                .isEqualTo(HttpProblemMother.SERIALIZED_BAD_REQUEST_PROBLEM);
    }

}
