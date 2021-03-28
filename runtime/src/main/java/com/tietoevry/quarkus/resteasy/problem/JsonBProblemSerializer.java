package com.tietoevry.quarkus.resteasy.problem;

import java.net.URI;
import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import org.zalando.problem.Problem;

/**
 * Low level JsonB serializer for Problem type.
 */
public class JsonBProblemSerializer implements JsonbSerializer<Problem> {

    private static final URI DEFAULT_URI = URI.create("about:blank");

    @Override
    public void serialize(Problem problem, JsonGenerator generator, SerializationContext ctx) {
        generator.writeStartObject();
        if (problem.getType() != null && !problem.getType().equals(DEFAULT_URI)) {
            generator.write("type", problem.getType().toASCIIString());
        }
        if (problem.getStatus() != null) {
            generator.write("status", problem.getStatus().getStatusCode());
        }
        if (problem.getTitle() != null) {
            generator.write("title", problem.getTitle());
        }
        if (problem.getDetail() != null) {
            generator.write("detail", problem.getDetail());
        }
        if (problem.getInstance() != null) {
            generator.write("instance", problem.getInstance().toASCIIString());
        }

        problem.getParameters().forEach((key, value) -> ctx.serialize(key, value, generator));

        generator.writeEnd();
    }
}
