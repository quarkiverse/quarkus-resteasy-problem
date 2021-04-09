package com.tietoevry.quarkus.resteasy.problem.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.tietoevry.quarkus.resteasy.problem.HttpProblem;
import java.io.IOException;
import java.util.Map;

/**
 * Low level Jackson serializer for HttpProblem type.
 */
public final class JacksonProblemSerializer extends StdSerializer<HttpProblem> {

    public JacksonProblemSerializer() {
        this(null);
    }

    public JacksonProblemSerializer(Class<HttpProblem> t) {
        super(t);
    }

    @Override
    public void serialize(final HttpProblem problem, final JsonGenerator json, final SerializerProvider serializers)
            throws IOException {
        json.writeStartObject();
        if (problem.getType() != null) {
            json.writeStringField("type", problem.getType().toASCIIString());
        }
        if (problem.getStatus() != null) {
            json.writeNumberField("status", problem.getStatus().getStatusCode());
        }
        if (problem.getTitle() != null) {
            json.writeStringField("title", problem.getTitle());
        }
        if (problem.getDetail() != null) {
            json.writeStringField("detail", problem.getDetail());
        }
        if (problem.getInstance() != null) {
            json.writeStringField("instance", problem.getInstance().toASCIIString());
        }

        for (Map.Entry<String, Object> entry : problem.getParameters().entrySet()) {
            json.writeObjectField(entry.getKey(), entry.getValue());
        }

        json.writeEndObject();
    }
}
