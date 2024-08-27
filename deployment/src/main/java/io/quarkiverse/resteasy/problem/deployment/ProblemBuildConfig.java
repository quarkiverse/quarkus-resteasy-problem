package io.quarkiverse.resteasy.problem.deployment;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import java.util.Set;

@ConfigRoot(name = "resteasy.problem", phase = ConfigPhase.BUILD_TIME)
public class ProblemBuildConfig {

    /**
     * MDC properties that should be included in problem responses.
     */
    @ConfigItem(defaultValue = "uuid")
    public Set<String> includeMdcProperties;

    /**
     * Whether or not metrics should be enabled if quarkus-smallrye-metrics is used.
     */
    @ConfigItem(name = "metrics.enabled", defaultValue = "false")
    public boolean metricsEnabled;
}
