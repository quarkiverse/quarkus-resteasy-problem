package io.quarkiverse.resteasy.problem;

import io.smallrye.config.WithDefault;

/**
 * Configuration interface for constraint violation exception mapping.
 */
public interface ConstraintViolationMapperConfig {

    static ConstraintViolationMapperConfig defaults() {
        return new ConstraintViolationMapperConfig() {
            @Override
            public int status() {
                return 400;
            }

            @Override
            public String title() {
                return "Bad Request";
            }

            @Override
            public String description() {
                return "Bad request: server would not process the request due to something the server considered to be a client error";
            }
        };
    }

    /**
     * Response status code when ConstraintViolationException is thrown.
     */
    @WithDefault("400")
    int status();

    /**
     * Response title when ConstraintViolationException is thrown.
     */
    @WithDefault("Bad Request")
    String title();

    /**
     * OpenApi description for ConstraintViolationExceptions.
     */
    @WithDefault("Bad request: server would not process the request due to something the server considered to be a client error")
    String description();
}