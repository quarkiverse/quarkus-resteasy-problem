package io.quarkiverse.resteasy.problem.jsonb;

import io.quarkiverse.resteasy.problem.HttpProblem;
import io.quarkiverse.resteasy.problem.InstanceUtils;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

/**
 * Low level JsonB serializer for Problem type.
 */
public final class JsonbProblemSerializer implements JsonbSerializer<HttpProblem> {

    @Override
    public void serialize(HttpProblem problem, JsonGenerator generator, SerializationContext ctx) {
        generator.writeStartObject();
        if (problem.getType() != null) {
            generator.write("type", problem.getType().toASCIIString());
        }
        generator.write("status", problem.getStatusCode());

        if (problem.getTitle() != null) {
            generator.write("title", problem.getTitle());
        }
        if (problem.getDetail() != null) {
            generator.write("detail", problem.getDetail());
        }
        if (problem.getInstance() != null) {
            generator.write("instance", InstanceUtils.instanceToPath(problem.getInstance()));
        }

        problem.getParameters().forEach((key, value) -> ctx.serialize(key, value, generator));

        generator.writeEnd();
    }
}
