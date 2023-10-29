package com.tietoevry.quarkus.resteasy.problem;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "quarkus.resteasy.problem")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface ProblemRuntimeConfig {

    /**
     * Config for ConstraintViolationException mapper
     */
    @WithName("constraint-violation")
    ConstraintViolationMapperConfig constraintViolation();

    interface ConstraintViolationMapperConfig {
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
    }
}
