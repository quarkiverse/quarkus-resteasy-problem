package io.quarkiverse.resteasy.problem.postprocessing;

import java.util.Set;

import jakarta.enterprise.inject.spi.CDI;

import io.quarkiverse.resteasy.problem.ExceptionMapperBase;
import io.quarkiverse.resteasy.problem.ProblemRuntimeConfig;
import io.quarkiverse.resteasy.problem.validation.ConstraintViolationExceptionMapper;
import io.quarkus.runtime.annotations.Recorder;

/**
 * Quarkus Recorder that applies configuration in the runtime.
 */
@Recorder
public class ProblemRecorder {

    public void reset() {
        ExceptionMapperBase.postProcessorsRegistry.reset();
    }

    public void configureMdc(Set<String> includeMdcProperties) {
        if (!includeMdcProperties.isEmpty()) {
            ExceptionMapperBase.postProcessorsRegistry.register(new MdcPropertiesInjector(includeMdcProperties));
        }
    }

    public void enableMetrics() {
        ExceptionMapperBase.postProcessorsRegistry.register(new MicroprofileMetricsCollector());
    }

    public void registerCustomPostProcessors() {
        CDI.current().select(ProblemPostProcessor.class)
                .forEach(ExceptionMapperBase.postProcessorsRegistry::register);
    }

    public void applyRuntimeConfig(ProblemRuntimeConfig config) {
        ConstraintViolationExceptionMapper.configure(config.constraintViolation());
    }
}
