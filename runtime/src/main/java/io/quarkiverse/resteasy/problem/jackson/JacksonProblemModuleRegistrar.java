package io.quarkiverse.resteasy.problem.jackson;

import jakarta.inject.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.quarkiverse.resteasy.problem.HttpProblem;
import io.quarkus.jackson.ObjectMapperCustomizer;

@Singleton
public final class JacksonProblemModuleRegistrar implements ObjectMapperCustomizer {

    @Override
    public void customize(ObjectMapper mapper) {
        registerProblemModule(mapper);
    }

    public static ObjectMapper registerProblemModule(ObjectMapper mapper) {
        mapper.registerModule(
                new SimpleModule("RFC7807 problem")
                        .addSerializer(HttpProblem.class, new JacksonProblemSerializer())
                        .addDeserializer(HttpProblem.class, new JacksonProblemDeserializer(mapper)));
        return mapper;
    }

}
