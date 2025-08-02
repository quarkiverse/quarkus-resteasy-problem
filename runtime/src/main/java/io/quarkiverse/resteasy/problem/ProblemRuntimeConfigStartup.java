package io.quarkiverse.resteasy.problem;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logging.Logger;

import io.quarkiverse.resteasy.problem.validation.ConstraintViolationExceptionMapper;
import io.quarkus.runtime.StartupEvent;

/**
 * Runtime startup bean that provides backward compatibility for deprecated runtime configuration.
 * This bean checks if runtime configuration properties are set and applies them during startup.
 *
 * @deprecated This is compatibility layer for runtime configuration. Should be removed with ProblemRuntimeConfig
 *             in 4.0 version
 */
@ApplicationScoped
@Deprecated(since = "3.20.0", forRemoval = true)
public class ProblemRuntimeConfigStartup {

    private static final Logger LOG = Logger.getLogger("io.quarkiverse.resteasy-problem");

    void onStartup(@Observes StartupEvent ev) {
        System.out.println("=== BOOM!!! ProblemRuntimeConfigStartup.onStartup() called ===");
        LOG.error("=== BOOM!!! ProblemRuntimeConfigStartup.onStartup() called ===");
        if (false) {
            throw new RuntimeException("BOOOOOOOOM! This is a test exception to ensure the startup event is triggered");
        }
        ConstraintViolationMapperConfig defaults = ConstraintViolationMapperConfig.defaults();

        Config config = ConfigProvider.getConfig();

        boolean hasRuntimeConfig = false;

        final int configStatus;
        final String configTitle;
        final String configDescription;

        if (config.getOptionalValue("quarkus.resteasy.problem.constraint-violation.status", Integer.class).isPresent()) {
            configStatus = config.getValue("quarkus.resteasy.problem.constraint-violation.status", Integer.class);
            hasRuntimeConfig = true;
        } else {
            configStatus = defaults.status();
        }

        if (config.getOptionalValue("quarkus.resteasy.problem.constraint-violation.title", String.class).isPresent()) {
            configTitle = config.getValue("quarkus.resteasy.problem.constraint-violation.title", String.class);
            hasRuntimeConfig = true;
        } else {
            configTitle = defaults.title();
        }

        if (config.getOptionalValue("quarkus.resteasy.problem.constraint-violation.description", String.class).isPresent()) {
            configDescription = config.getValue("quarkus.resteasy.problem.constraint-violation.description", String.class);
            hasRuntimeConfig = true;
        } else {
            configDescription = defaults.description();
        }

        if (hasRuntimeConfig) {
            LOG.warnf("Runtime configuration properties for constraint violation are deprecated. " +
                    "Use build-time configuration instead.");

            ConstraintViolationMapperConfig runtimeConfig = new ConstraintViolationMapperConfig() {
                @Override
                public int status() {
                    return configStatus;
                }

                @Override
                public String title() {
                    return configTitle;
                }

                @Override
                public String description() {
                    return configDescription;
                }
            };

            ConstraintViolationExceptionMapper.configure(runtimeConfig);
        }
    }
}
