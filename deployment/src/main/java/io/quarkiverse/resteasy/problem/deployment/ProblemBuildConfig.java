package io.quarkiverse.resteasy.problem.deployment;

import java.util.Set;

import io.quarkiverse.resteasy.problem.ConstraintViolationMapperConfig;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "quarkus.resteasy.problem")
@ConfigRoot(phase = ConfigPhase.BUILD_TIME)
public interface ProblemBuildConfig {

    /**
     * MDC properties that should be included in problem responses.
     */
    @WithDefault("uuid")
    Set<String> includeMdcProperties();

    /**
     * Whether metrics should be enabled if quarkus-smallrye-metrics is used.
     */
    @WithName("metrics.enabled")
    @WithDefault("false")
    boolean metricsEnabled();

    /**
     * OpenApi related configuration
     */
    @WithName("openapi")
    OpenApiConfig openapi();

    interface OpenApiConfig {

        /**
         * Which schema should be used by default for problem responses
         */
        @WithName("default-schema")
        @WithDefault("HttpProblem")
        String defaultSchema();

        /**
         * Which schema should be used by default for validation problem responses
         */
        @WithName("validation-problem-schema")
        @WithDefault("HttpValidationProblem")
        String validationProblemSchema();
    }

    /**
     * Config for constraint violation exception mapping and OpenApi schema of HttpValidationProblem
     */
    @WithName("constraint-violation")
    ConstraintViolationMapperConfig constraintViolation();
}
