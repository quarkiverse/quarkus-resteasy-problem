package io.quarkiverse.resteasy.problem;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

/**
 * @deprecated v4.0.0 all configuration will be evaluated in build time, Use {@link ProblemBuildConfig} instead.
 *             This configuration now reads from build-time values for backward compatibility.
 */
@ConfigMapping(prefix = "quarkus.resteasy.problem")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
@Deprecated(since = "3.20.0", forRemoval = true)
public interface ProblemRuntimeConfig {

    /**
     * Config for ConstraintViolationException mapper
     *
     * @deprecated Use ProblemBuildConfig.constraintViolation() instead
     */
    @WithName("constraint-violation")
    @Deprecated(since = "3.20.0", forRemoval = true)
    ConstraintViolationMapperConfig constraintViolation();

    /**
     * @deprecated Use {@link io.quarkiverse.resteasy.problem.ConstraintViolationMapperConfig} instead.
     *             This interface is kept for backward compatibility and extends the new interface.
     */
    @Deprecated(since = "3.20.0", forRemoval = true)
    interface ConstraintViolationMapperConfig extends io.quarkiverse.resteasy.problem.ConstraintViolationMapperConfig {

        /**
         * Response status code when ConstraintViolationException is thrown.
         *
         * @deprecated Use build-time configuration instead
         */
        @WithDefault("400")
        @Deprecated(since = "3.20.0", forRemoval = true)
        int status();

        /**
         * Response title when ConstraintViolationException is thrown.
         *
         * @deprecated Use build-time configuration instead
         */
        @WithDefault("Bad Request")
        @Deprecated(since = "3.20.0", forRemoval = true)
        String title();

        /**
         * OpenApi description for ConstraintViolationExceptions.
         *
         * @deprecated Use build-time configuration instead
         */
        @WithDefault("Bad request: server would not process the request due to something the server considered to be a client error")
        @Deprecated(since = "3.20.0", forRemoval = true)
        String description();
    }
}
