package io.quarkiverse.resteasy.problem.deployment;

import java.util.Set;

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
}
