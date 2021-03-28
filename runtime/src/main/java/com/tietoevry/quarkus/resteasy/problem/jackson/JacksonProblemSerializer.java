package com.tietoevry.quarkus.resteasy.problem.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import org.zalando.problem.Problem;

public class JacksonProblemSerializer extends StdSerializer<Problem> {

    private static final URI DEFAULT_URI = URI.create("about:blank");

    public JacksonProblemSerializer() {
        this(null);
    }

    public JacksonProblemSerializer(Class<Problem> t) {
        super(t);
    }

    @Override
    public void serialize(final Problem problem, final JsonGenerator json, final SerializerProvider serializers)
            throws IOException {
        json.writeStartObject();
        if (problem.getType() != null && !problem.getType().equals(DEFAULT_URI)) {
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
