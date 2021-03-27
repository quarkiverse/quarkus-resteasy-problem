package com.tietoevry.quarkus.resteasy.problem.deployment;

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

    /**
     * Prefix of a `type` field in the default (non-ThrowableProblem) responses. It may be root location for human-readable
     * documentation for different kind of errors. I.e `/api/docs/` will produce "type": "/api/docs/unauthorized" for HTTP 401.
     */
    @ConfigItem(name = "type-prefix", defaultValue = "/")
    public String typePrefix;
}
